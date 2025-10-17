package com.simplespring.context;

import com.simplespring.beans.factory.BeanFactory;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.support.DefaultBeanFactory;
import com.simplespring.core.annotation.Autowired;
import com.simplespring.core.annotation.Component;
import com.simplespring.core.annotation.Configuration;
import com.simplespring.core.annotation.Bean;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 模块集成测试，验证 spring-core、spring-beans、spring-context 模块的协作
 * 
 * @author SimpleSpring Framework
 */
public class ModuleIntegrationTest {

  private AnnotationConfigApplicationContext applicationContext;

  @Before
  public void setUp() {
    applicationContext = new AnnotationConfigApplicationContext();
  }

  @Test
  public void testCoreBeansContextIntegration() {
    // 测试核心模块、Bean 模块和上下文模块的集成

    // 扫描测试包
    applicationContext.scan("com.simplespring.context.ModuleIntegrationTest");
    applicationContext.refresh();

    // 验证组件扫描和依赖注入
    TestService testService = applicationContext.getBean(TestService.class);
    assertNotNull("TestService should be created", testService);

    TestController testController = applicationContext.getBean(TestController.class);
    assertNotNull("TestController should be created", testController);
    assertNotNull("TestController should have injected TestService",
        testController.getTestService());
    assertSame("Injected service should be the same instance",
        testService, testController.getTestService());
  }

  @Test
  public void testConfigurationClassProcessing() {
    // 测试配置类处理

    applicationContext.register(TestConfiguration.class);
    applicationContext.refresh();

    // 验证配置类被处理
    TestConfiguration config = applicationContext.getBean(TestConfiguration.class);
    assertNotNull("Configuration class should be created", config);

    // 验证 @Bean 方法创建的 Bean
    TestBean testBean = applicationContext.getBean("testBean", TestBean.class);
    assertNotNull("Bean from @Bean method should be created", testBean);
    assertEquals("Bean should have correct value", "configured", testBean.getValue());
  }

  @Test
  public void testBeanFactoryIntegration() {
    // 测试 BeanFactory 集成

    applicationContext.scan("com.simplespring.context.ModuleIntegrationTest");
    applicationContext.refresh();

    // 获取底层的 BeanFactory
    BeanFactory beanFactory = applicationContext.getBeanFactory();
    assertNotNull("BeanFactory should be available", beanFactory);
    assertTrue("BeanFactory should be DefaultBeanFactory",
        beanFactory instanceof DefaultBeanFactory);

    // 验证 BeanFactory 功能
    TestService testService = (TestService) beanFactory.getBean("testService");
    assertNotNull("BeanFactory should provide beans", testService);
  }

  @Test
  public void testClassPathScanning() {
    // 测试类路径扫描功能

    ClassPathScanner scanner = new ClassPathScanner();

    // 扫描当前包
    String packageName = this.getClass().getPackage().getName();
    java.util.Set<Class<?>> scannedClasses = scanner.scanPackage(packageName);

    assertNotNull("Scanned classes should not be null", scannedClasses);
    assertFalse("Should find some classes", scannedClasses.isEmpty());

    // 验证找到了测试类
    boolean foundTestClass = false;
    for (Class<?> clazz : scannedClasses) {
      if (clazz.getSimpleName().equals("ModuleIntegrationTest")) {
        foundTestClass = true;
        break;
      }
    }
    assertTrue("Should find the test class", foundTestClass);
  }

  @Test
  public void testAnnotationProcessing() {
    // 测试注解处理

    applicationContext.scan("com.simplespring.context.ModuleIntegrationTest");
    applicationContext.refresh();

    // 验证 @Component 注解被正确处理
    assertTrue("Should contain TestService",
        applicationContext.containsBean("testService"));
    assertTrue("Should contain TestController",
        applicationContext.containsBean("testController"));

    // 验证 @Autowired 注解被正确处理
    TestController controller = applicationContext.getBean(TestController.class);
    assertNotNull("Controller should have injected dependencies",
        controller.getTestService());
  }

  @Test
  public void testBeanDefinitionProcessing() {
    // 测试 BeanDefinition 处理

    applicationContext.scan("com.simplespring.context.ModuleIntegrationTest");
    applicationContext.refresh();

    // 获取 BeanDefinition
    DefaultBeanFactory beanFactory = (DefaultBeanFactory) applicationContext.getBeanFactory();
    BeanDefinition testServiceDef = beanFactory.getBeanDefinition("testService");

    assertNotNull("BeanDefinition should exist", testServiceDef);
    assertEquals("BeanDefinition should have correct class",
        TestService.class, testServiceDef.getBeanClass());
    assertTrue("BeanDefinition should be singleton", testServiceDef.isSingleton());
  }

  @Test
  public void testCircularDependencyDetection() {
    // 测试循环依赖检测

    try {
      applicationContext.register(CircularDependencyA.class);
      applicationContext.register(CircularDependencyB.class);
      applicationContext.refresh();

      // 如果没有抛出异常，说明循环依赖处理正常
      CircularDependencyA beanA = applicationContext.getBean(CircularDependencyA.class);
      CircularDependencyB beanB = applicationContext.getBean(CircularDependencyB.class);

      assertNotNull("Bean A should be created", beanA);
      assertNotNull("Bean B should be created", beanB);

    } catch (Exception e) {
      // 如果抛出循环依赖异常，也是正常的
      assertTrue("Should be circular dependency related exception",
          e.getMessage().contains("循环依赖") ||
              e.getMessage().contains("circular"));
    }
  }

  @Test
  public void testApplicationContextLifecycle() {
    // 测试应用上下文生命周期

    assertFalse("Context should not be active initially", applicationContext.isActive());

    applicationContext.scan("com.simplespring.context.ModuleIntegrationTest");
    applicationContext.refresh();

    assertTrue("Context should be active after refresh", applicationContext.isActive());

    // 验证可以获取 Bean
    TestService testService = applicationContext.getBean(TestService.class);
    assertNotNull("Should be able to get beans when active", testService);

    applicationContext.close();
    assertFalse("Context should not be active after close", applicationContext.isActive());
  }

  @Test
  public void testMultiplePackageScanning() {
    // 测试多包扫描

    applicationContext.scan(
        "com.simplespring.context.ModuleIntegrationTest",
        "com.simplespring.context");
    applicationContext.refresh();

    // 验证扫描到了多个包中的组件
    assertTrue("Should find beans from multiple packages",
        applicationContext.getBeanDefinitionNames().length > 0);
  }

  // 测试用的组件类
  @Component
  public static class TestService {
    public String doSomething() {
      return "TestService working";
    }
  }

  @Component
  public static class TestController {
    @Autowired
    private TestService testService;

    public TestService getTestService() {
      return testService;
    }

    public String handleRequest() {
      return testService.doSomething();
    }
  }

  @Configuration
  public static class TestConfiguration {

    @Bean
    public TestBean testBean() {
      return new TestBean("configured");
    }
  }

  public static class TestBean {
    private String value;

    public TestBean(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  // 循环依赖测试类
  @Component
  public static class CircularDependencyA {
    @Autowired
    private CircularDependencyB dependencyB;

    public CircularDependencyB getDependencyB() {
      return dependencyB;
    }
  }

  @Component
  public static class CircularDependencyB {
    @Autowired
    private CircularDependencyA dependencyA;

    public CircularDependencyA getDependencyA() {
      return dependencyA;
    }
  }
}
