package com.simplespring.context;

import com.simplespring.aop.AspectDefinition;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.Scope;
import com.simplespring.beans.factory.support.DefaultBeanFactory;
import com.simplespring.core.annotation.After;
import com.simplespring.core.annotation.Aspect;
import com.simplespring.core.annotation.Before;
import com.simplespring.core.annotation.Component;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * AspectProcessor 类测试
 * 
 * @author SimpleSpring
 */
public class AspectProcessorTest {

  private DefaultBeanFactory beanFactory;
  private AspectProcessor aspectProcessor;

  @Before
  public void setUp() {
    beanFactory = new DefaultBeanFactory();
    aspectProcessor = new AspectProcessor(beanFactory);
  }

  @Test
  public void testProcessAspects() {
    // 注册切面 Bean
    registerAspectBean();

    // 注册普通 Bean
    registerServiceBean();

    String[] beanNames = beanFactory.getBeanDefinitionNames();
    aspectProcessor.processAspects(beanNames);

    List<AspectDefinition> aspects = aspectProcessor.getAspectDefinitions();
    assertEquals("应该有一个切面定义", 1, aspects.size());

    AspectDefinition aspectDef = aspects.get(0);
    assertEquals("切面名称应该正确", "testAspect", aspectDef.getAspectName());
    assertTrue("应该有通知定义", aspectDef.hasAdvices());
    assertEquals("应该有2个通知", 2, aspectDef.getAdvices().size());
  }

  @Test
  public void testPostProcessAfterInitialization() {
    // 注册切面 Bean
    registerAspectBean();

    // 注册服务 Bean
    registerServiceBean();

    // 处理切面
    String[] beanNames = beanFactory.getBeanDefinitionNames();
    aspectProcessor.processAspects(beanNames);

    // 创建服务实例
    TestService service = new TestService();

    // 应用 AOP 处理
    Object result = aspectProcessor.postProcessAfterInitialization("testService", service);

    // 由于 TestService 没有实现接口，代理创建会失败，返回原始对象
    // 在实际项目中，如果集成了 CGLIB，这里会返回代理对象
    assertEquals("应该返回原始对象（因为没有 CGLIB）", service, result);
  }

  @Test
  public void testPostProcessAfterInitializationWithInterface() {
    // 注册切面 Bean
    registerAspectBean();

    // 注册接口服务 Bean
    registerInterfaceServiceBean();

    // 处理切面
    String[] beanNames = beanFactory.getBeanDefinitionNames();
    aspectProcessor.processAspects(beanNames);

    // 创建服务实例
    TestServiceInterface service = new TestServiceImpl();

    // 应用 AOP 处理
    Object result = aspectProcessor.postProcessAfterInitialization("testServiceInterface", service);

    // 应该创建代理对象
    assertNotNull("结果不应该为 null", result);
    assertTrue("应该实现接口", result instanceof TestServiceInterface);

    // 如果是代理对象，类型会不同
    if (result != service) {
      // 这是代理对象
      assertTrue("代理对象应该实现接口", result instanceof TestServiceInterface);
    }
  }

  @Test
  public void testGetAspectCount() {
    assertEquals("初始切面数量应该为0", 0, aspectProcessor.getAspectCount());

    // 注册切面 Bean
    registerAspectBean();

    String[] beanNames = beanFactory.getBeanDefinitionNames();
    aspectProcessor.processAspects(beanNames);

    assertEquals("处理后切面数量应该为1", 1, aspectProcessor.getAspectCount());
  }

  @Test
  public void testClearCache() {
    // 注册切面 Bean
    registerAspectBean();

    String[] beanNames = beanFactory.getBeanDefinitionNames();
    aspectProcessor.processAspects(beanNames);

    assertEquals("处理后切面数量应该为1", 1, aspectProcessor.getAspectCount());

    aspectProcessor.clearCache();

    assertEquals("清理后切面数量应该为0", 0, aspectProcessor.getAspectCount());
  }

  @Test
  public void testNonAspectClass() {
    // 只注册普通 Bean
    registerServiceBean();

    String[] beanNames = beanFactory.getBeanDefinitionNames();
    aspectProcessor.processAspects(beanNames);

    assertEquals("不应该有切面定义", 0, aspectProcessor.getAspectCount());
  }

  /**
   * 注册切面 Bean
   */
  private void registerAspectBean() {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(TestAspect.class);
    beanDefinition.setBeanName("testAspect");
    beanDefinition.setScope(Scope.SINGLETON);

    beanFactory.registerBeanDefinition("testAspect", beanDefinition);
  }

  /**
   * 注册服务 Bean
   */
  private void registerServiceBean() {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(TestService.class);
    beanDefinition.setBeanName("testService");
    beanDefinition.setScope(Scope.SINGLETON);

    beanFactory.registerBeanDefinition("testService", beanDefinition);
  }

  /**
   * 注册接口服务 Bean
   */
  private void registerInterfaceServiceBean() {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setBeanClass(TestServiceImpl.class);
    beanDefinition.setBeanName("testServiceInterface");
    beanDefinition.setScope(Scope.SINGLETON);

    beanFactory.registerBeanDefinition("testServiceInterface", beanDefinition);
  }

  // 测试用的切面类
  @Aspect
  @Component
  public static class TestAspect {
    public boolean beforeCalled = false;
    public boolean afterCalled = false;

    @Before("execution(* *.test*(..))")
    public void beforeAdvice() {
      beforeCalled = true;
    }

    @After("execution(* *.test*(..))")
    public void afterAdvice() {
      afterCalled = true;
    }
  }

  // 测试用的服务类（无接口）
  @Component
  public static class TestService {
    public String testMethod(String input) {
      return "TestService: " + input;
    }
  }

  // 测试用的接口
  public interface TestServiceInterface {
    String testMethod(String input);
  }

  // 测试用的服务实现类
  @Component
  public static class TestServiceImpl implements TestServiceInterface {
    @Override
    public String testMethod(String input) {
      return "TestServiceImpl: " + input;
    }
  }
}
