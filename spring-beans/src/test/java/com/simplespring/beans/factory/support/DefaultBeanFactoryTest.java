package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.*;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.Scope;
import com.simplespring.core.annotation.Autowired;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * DefaultBeanFactory 测试类
 * 
 * @author SimpleSpring Framework
 */
public class DefaultBeanFactoryTest {

  private DefaultBeanFactory beanFactory;

  @Before
  public void setUp() {
    beanFactory = new DefaultBeanFactory();
  }

  @Test
  public void testRegisterAndGetBeanDefinition() {
    // 测试注册和获取 Bean 定义
    BeanDefinition beanDefinition = new BeanDefinition(TestService.class, "testService");
    beanFactory.registerBeanDefinition("testService", beanDefinition);

    BeanDefinition retrieved = beanFactory.getBeanDefinition("testService");
    assertNotNull(retrieved);
    assertEquals(TestService.class, retrieved.getBeanClass());
    assertEquals("testService", retrieved.getBeanName());
  }

  @Test
  public void testGetBeanByName() {
    // 测试根据名称获取 Bean
    BeanDefinition beanDefinition = new BeanDefinition(TestService.class, "testService");
    beanFactory.registerBeanDefinition("testService", beanDefinition);

    Object bean = beanFactory.getBean("testService");
    assertNotNull(bean);
    assertTrue(bean instanceof TestService);
  }

  @Test
  public void testGetBeanByNameAndType() {
    // 测试根据名称和类型获取 Bean
    BeanDefinition beanDefinition = new BeanDefinition(TestService.class, "testService");
    beanFactory.registerBeanDefinition("testService", beanDefinition);

    TestService bean = beanFactory.getBean("testService", TestService.class);
    assertNotNull(bean);
    assertTrue(bean instanceof TestService);
  }

  @Test
  public void testGetBeanByType() {
    // 测试根据类型获取 Bean
    BeanDefinition beanDefinition = new BeanDefinition(TestService.class, "testService");
    beanFactory.registerBeanDefinition("testService", beanDefinition);

    TestService bean = beanFactory.getBean(TestService.class);
    assertNotNull(bean);
    assertTrue(bean instanceof TestService);
  }

  @Test(expected = NoSuchBeanDefinitionException.class)
  public void testGetBeanWithNonExistentName() {
    // 测试获取不存在的 Bean
    beanFactory.getBean("nonExistent");
  }

  @Test(expected = NoSuchBeanDefinitionException.class)
  public void testGetBeanWithNonExistentType() {
    // 测试获取不存在类型的 Bean
    beanFactory.getBean(String.class);
  }

  @Test(expected = NoUniqueBeanDefinitionException.class)
  public void testGetBeanByTypeWithMultipleCandidates() {
    // 测试根据类型获取 Bean 时有多个候选者
    BeanDefinition bd1 = new BeanDefinition(TestService.class, "service1");
    BeanDefinition bd2 = new BeanDefinition(TestService.class, "service2");

    beanFactory.registerBeanDefinition("service1", bd1);
    beanFactory.registerBeanDefinition("service2", bd2);

    beanFactory.getBean(TestService.class);
  }

  @Test(expected = BeanNotOfRequiredTypeException.class)
  public void testGetBeanWithWrongType() {
    // 测试获取错误类型的 Bean
    BeanDefinition beanDefinition = new BeanDefinition(TestService.class, "testService");
    beanFactory.registerBeanDefinition("testService", beanDefinition);

    beanFactory.getBean("testService", String.class);
  }

  @Test
  public void testSingletonScope() {
    // 测试单例作用域
    BeanDefinition beanDefinition = new BeanDefinition(TestService.class, "testService");
    beanDefinition.setScope(Scope.SINGLETON);
    beanFactory.registerBeanDefinition("testService", beanDefinition);

    Object bean1 = beanFactory.getBean("testService");
    Object bean2 = beanFactory.getBean("testService");

    assertSame(bean1, bean2); // 应该是同一个实例
    assertTrue(beanFactory.isSingleton("testService"));
    assertFalse(beanFactory.isPrototype("testService"));
  }

  @Test
  public void testPrototypeScope() {
    // 测试原型作用域
    BeanDefinition beanDefinition = new BeanDefinition(TestService.class, "testService");
    beanDefinition.setScope(Scope.PROTOTYPE);
    beanFactory.registerBeanDefinition("testService", beanDefinition);

    Object bean1 = beanFactory.getBean("testService");
    Object bean2 = beanFactory.getBean("testService");

    assertNotSame(bean1, bean2); // 应该是不同的实例
    assertFalse(beanFactory.isSingleton("testService"));
    assertTrue(beanFactory.isPrototype("testService"));
  }

  @Test
  public void testFieldInjection() {
    // 测试字段注入

    // 注册依赖 Bean
    BeanDefinition repoBd = new BeanDefinition(TestRepository.class, "testRepository");
    beanFactory.registerBeanDefinition("testRepository", repoBd);

    // 注册目标 Bean
    BeanDefinition serviceBd = new BeanDefinition(TestServiceWithFieldInjection.class, "testService");
    beanFactory.registerBeanDefinition("testService", serviceBd);

    // 获取 Bean 并验证注入
    TestServiceWithFieldInjection service = beanFactory.getBean("testService", TestServiceWithFieldInjection.class);
    assertNotNull(service);
    assertNotNull(service.getRepository());
    assertTrue(service.getRepository() instanceof TestRepository);
  }

  @Test
  public void testMethodInjection() {
    // 测试方法注入

    // 注册依赖 Bean
    BeanDefinition repoBd = new BeanDefinition(TestRepository.class, "testRepository");
    beanFactory.registerBeanDefinition("testRepository", repoBd);

    // 注册目标 Bean
    BeanDefinition serviceBd = new BeanDefinition(TestServiceWithMethodInjection.class, "testService");
    beanFactory.registerBeanDefinition("testService", serviceBd);

    // 获取 Bean 并验证注入
    TestServiceWithMethodInjection service = beanFactory.getBean("testService", TestServiceWithMethodInjection.class);
    assertNotNull(service);
    assertNotNull(service.getRepository());
    assertTrue(service.getRepository() instanceof TestRepository);
  }

  @Test
  public void testConstructorInjection() {
    // 测试构造函数注入

    // 注册依赖 Bean
    BeanDefinition repoBd = new BeanDefinition(TestRepository.class, "testRepository");
    beanFactory.registerBeanDefinition("testRepository", repoBd);

    // 注册目标 Bean
    BeanDefinition serviceBd = new BeanDefinition(TestServiceWithConstructorInjection.class, "testService");
    beanFactory.registerBeanDefinition("testService", serviceBd);

    // 获取 Bean 并验证注入
    TestServiceWithConstructorInjection service = beanFactory.getBean("testService",
        TestServiceWithConstructorInjection.class);
    assertNotNull(service);
    assertNotNull(service.getRepository());
    assertTrue(service.getRepository() instanceof TestRepository);
  }

  @Test
  public void testComplexDependencyInjection() {
    // 测试复杂的依赖注入场景

    // 注册依赖 Bean
    BeanDefinition repoBd = new BeanDefinition(TestRepository.class, "testRepository");
    BeanDefinition utilBd = new BeanDefinition(TestUtil.class, "testUtil");
    beanFactory.registerBeanDefinition("testRepository", repoBd);
    beanFactory.registerBeanDefinition("testUtil", utilBd);

    // 注册目标 Bean
    BeanDefinition serviceBd = new BeanDefinition(TestServiceWithComplexInjection.class, "testService");
    beanFactory.registerBeanDefinition("testService", serviceBd);

    // 获取 Bean 并验证注入
    TestServiceWithComplexInjection service = beanFactory.getBean("testService", TestServiceWithComplexInjection.class);
    assertNotNull(service);
    assertNotNull(service.getRepository());
    assertNotNull(service.getUtil());
    assertTrue(service.getRepository() instanceof TestRepository);
    assertTrue(service.getUtil() instanceof TestUtil);
  }

  @Test
  public void testContainsBean() {
    // 测试检查 Bean 是否存在
    assertFalse(beanFactory.containsBean("testService"));

    BeanDefinition beanDefinition = new BeanDefinition(TestService.class, "testService");
    beanFactory.registerBeanDefinition("testService", beanDefinition);

    assertTrue(beanFactory.containsBean("testService"));
  }

  @Test
  public void testGetType() {
    // 测试获取 Bean 类型
    BeanDefinition beanDefinition = new BeanDefinition(TestService.class, "testService");
    beanFactory.registerBeanDefinition("testService", beanDefinition);

    Class<?> type = beanFactory.getType("testService");
    assertEquals(TestService.class, type);

    assertNull(beanFactory.getType("nonExistent"));
  }

  @Test
  public void testGetBeanDefinitionNames() {
    // 测试获取所有 Bean 定义名称
    assertEquals(0, beanFactory.getBeanDefinitionNames().length);

    BeanDefinition bd1 = new BeanDefinition(TestService.class, "service1");
    BeanDefinition bd2 = new BeanDefinition(TestRepository.class, "repository1");

    beanFactory.registerBeanDefinition("service1", bd1);
    beanFactory.registerBeanDefinition("repository1", bd2);

    String[] names = beanFactory.getBeanDefinitionNames();
    assertEquals(2, names.length);
    assertTrue(contains(names, "service1"));
    assertTrue(contains(names, "repository1"));
  }

  @Test
  public void testGetBeanDefinitionCount() {
    // 测试获取 Bean 定义数量
    assertEquals(0, beanFactory.getBeanDefinitionCount());

    BeanDefinition bd1 = new BeanDefinition(TestService.class, "service1");
    beanFactory.registerBeanDefinition("service1", bd1);
    assertEquals(1, beanFactory.getBeanDefinitionCount());

    BeanDefinition bd2 = new BeanDefinition(TestRepository.class, "repository1");
    beanFactory.registerBeanDefinition("repository1", bd2);
    assertEquals(2, beanFactory.getBeanDefinitionCount());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRegisterBeanDefinitionWithEmptyName() {
    // 测试注册空名称的 Bean 定义
    BeanDefinition beanDefinition = new BeanDefinition(TestService.class);
    beanFactory.registerBeanDefinition("", beanDefinition);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRegisterBeanDefinitionWithNullDefinition() {
    // 测试注册 null Bean 定义
    beanFactory.registerBeanDefinition("testService", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetBeanWithEmptyName() {
    // 测试获取空名称的 Bean
    beanFactory.getBean("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetBeanWithNullType() {
    // 测试获取 null 类型的 Bean
    beanFactory.getBean((Class<?>) null);
  }

  // 辅助方法
  private boolean contains(String[] array, String value) {
    for (String item : array) {
      if (item.equals(value)) {
        return true;
      }
    }
    return false;
  }

  // 测试用的类

  public static class TestService {
  }

  public static class TestRepository {
  }

  public static class TestUtil {
  }

  public static class TestServiceWithFieldInjection {
    @Autowired
    private TestRepository repository;

    public TestRepository getRepository() {
      return repository;
    }
  }

  public static class TestServiceWithMethodInjection {
    private TestRepository repository;

    @Autowired
    public void setRepository(TestRepository repository) {
      this.repository = repository;
    }

    public TestRepository getRepository() {
      return repository;
    }
  }

  public static class TestServiceWithConstructorInjection {
    private final TestRepository repository;

    @Autowired
    public TestServiceWithConstructorInjection(TestRepository repository) {
      this.repository = repository;
    }

    public TestRepository getRepository() {
      return repository;
    }
  }

  public static class TestServiceWithComplexInjection {
    @Autowired
    private TestRepository repository;

    private TestUtil util;

    @Autowired
    public void setUtil(TestUtil util) {
      this.util = util;
    }

    public TestRepository getRepository() {
      return repository;
    }

    public TestUtil getUtil() {
      return util;
    }
  }
}
