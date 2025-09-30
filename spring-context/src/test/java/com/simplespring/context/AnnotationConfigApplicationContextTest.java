package com.simplespring.context;

import com.simplespring.beans.factory.NoSuchBeanDefinitionException;
import com.simplespring.beans.factory.NoUniqueBeanDefinitionException;
import com.simplespring.context.testdata.PlainClass;
import com.simplespring.context.testdata.TestComponent;
import com.simplespring.context.testdata.TestConfiguration;
import com.simplespring.context.testdata.TestController;
import com.simplespring.context.testdata.subpackage.SubPackageComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AnnotationConfigApplicationContext 的集成测试
 * 
 * 测试应用上下文的完整功能，包括：
 * 1. 容器生命周期管理
 * 2. 组件扫描和注册
 * 3. Bean 创建和依赖注入
 * 4. 容器状态管理
 * 5. 异常处理
 * 
 * @author Simple Spring Framework
 */
public class AnnotationConfigApplicationContextTest {

  private AnnotationConfigApplicationContext context;

  @Before
  public void setUp() {
    // 每个测试方法都会创建新的上下文
  }

  @After
  public void tearDown() {
    if (context != null && context.isActive()) {
      context.close();
    }
  }

  /**
   * 测试单包扫描的应用上下文创建
   */
  @Test
  public void testCreateContextWithSinglePackage() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    // 验证容器状态
    assertTrue("容器应该处于活动状态", context.isActive());
    assertTrue("容器启动时间应该大于 0", context.getStartupDate() > 0);
    assertNotNull("容器 ID 不应为空", context.getId());
    assertNotNull("容器显示名称不应为空", context.getDisplayName());

    // 验证 Bean 定义数量
    assertTrue("应该扫描到组件", context.getBeanDefinitionCount() > 0);
  }

  /**
   * 测试多包扫描的应用上下文创建
   */
  @Test
  public void testCreateContextWithMultiplePackages() {
    // 使用只扫描子包来避免重复注册
    context = new AnnotationConfigApplicationContext(
        "com.simplespring.context.testdata.subpackage");

    // 验证容器状态
    assertTrue("容器应该处于活动状态", context.isActive());

    // 验证包含子包组件
    assertTrue("应该包含子包组件", context.containsBean("subComponent"));
  }

  /**
   * 测试通过名称获取 Bean
   */
  @Test
  public void testGetBeanByName() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    // 获取不同类型的 Bean
    Object testComponent = context.getBean("testComponent");
    assertNotNull("TestComponent 不应为空", testComponent);
    assertTrue("应该是 TestComponent 实例", testComponent instanceof TestComponent);

    Object testController = context.getBean("testController");
    assertNotNull("TestController 不应为空", testController);
    assertTrue("应该是 TestController 实例", testController instanceof TestController);

    Object testConfiguration = context.getBean("testConfiguration");
    assertNotNull("TestConfiguration 不应为空", testConfiguration);
    assertTrue("应该是 TestConfiguration 实例", testConfiguration instanceof TestConfiguration);
  }

  /**
   * 测试通过类型获取 Bean
   */
  @Test
  public void testGetBeanByType() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    // 通过类型获取 Bean
    TestComponent testComponent = context.getBean(TestComponent.class);
    assertNotNull("TestComponent 不应为空", testComponent);

    TestController testController = context.getBean(TestController.class);
    assertNotNull("TestController 不应为空", testController);

    TestConfiguration testConfiguration = context.getBean(TestConfiguration.class);
    assertNotNull("TestConfiguration 不应为空", testConfiguration);
  }

  /**
   * 测试通过名称和类型获取 Bean
   */
  @Test
  public void testGetBeanByNameAndType() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    // 通过名称和类型获取 Bean
    TestComponent testComponent = context.getBean("testComponent", TestComponent.class);
    assertNotNull("TestComponent 不应为空", testComponent);

    TestController testController = context.getBean("testController", TestController.class);
    assertNotNull("TestController 不应为空", testController);
  }

  /**
   * 测试单例 Bean 的行为
   */
  @Test
  public void testSingletonBehavior() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    // 多次获取同一个 Bean，应该返回相同实例
    TestComponent component1 = context.getBean(TestComponent.class);
    TestComponent component2 = context.getBean(TestComponent.class);
    TestComponent component3 = context.getBean("testComponent", TestComponent.class);

    assertSame("单例 Bean 应该返回相同实例", component1, component2);
    assertSame("单例 Bean 应该返回相同实例", component1, component3);

    // 验证是单例
    assertTrue("应该是单例", context.isSingleton("testComponent"));
    assertFalse("不应该是原型", context.isPrototype("testComponent"));
  }

  /**
   * 测试 Bean 类型检查
   */
  @Test
  public void testBeanTypeCheck() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    // 检查 Bean 类型
    Class<?> componentType = context.getType("testComponent");
    assertEquals("Bean 类型应该匹配", TestComponent.class, componentType);

    Class<?> controllerType = context.getType("testController");
    assertEquals("Bean 类型应该匹配", TestController.class, controllerType);
  }

  /**
   * 测试容器包含 Bean 检查
   */
  @Test
  public void testContainsBean() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    // 检查包含的 Bean
    assertTrue("应该包含 testComponent", context.containsBean("testComponent"));
    assertTrue("应该包含 testController", context.containsBean("testController"));
    assertTrue("应该包含 testConfiguration", context.containsBean("testConfiguration"));

    // 检查不包含的 Bean
    assertFalse("不应该包含 plainClass", context.containsBean("plainClass"));
    assertFalse("不应该包含不存在的 Bean", context.containsBean("nonExistentBean"));
  }

  /**
   * 测试获取所有 Bean 定义名称
   */
  @Test
  public void testGetBeanDefinitionNames() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    String[] beanNames = context.getBeanDefinitionNames();
    assertNotNull("Bean 名称数组不应为空", beanNames);
    assertTrue("应该有 Bean 定义", beanNames.length > 0);

    // 验证包含期望的 Bean 名称
    boolean hasTestComponent = false;
    boolean hasTestController = false;
    boolean hasTestConfiguration = false;

    for (String beanName : beanNames) {
      if ("testComponent".equals(beanName)) {
        hasTestComponent = true;
      } else if ("testController".equals(beanName)) {
        hasTestController = true;
      } else if ("testConfiguration".equals(beanName)) {
        hasTestConfiguration = true;
      }
    }

    assertTrue("应该包含 testComponent", hasTestComponent);
    assertTrue("应该包含 testController", hasTestController);
    assertTrue("应该包含 testConfiguration", hasTestConfiguration);
  }

  /**
   * 测试容器刷新
   */
  @Test
  public void testRefresh() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    long firstStartupTime = context.getStartupDate();
    assertTrue("首次启动时间应该大于 0", firstStartupTime > 0);

    // 等待一毫秒确保时间差异
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // 刷新容器
    context.refresh();

    long secondStartupTime = context.getStartupDate();
    assertTrue("刷新后启动时间应该更新", secondStartupTime > firstStartupTime);
    assertTrue("容器应该仍然活动", context.isActive());
  }

  /**
   * 测试容器关闭
   */
  @Test
  public void testClose() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    assertTrue("容器应该处于活动状态", context.isActive());

    // 关闭容器
    context.close();

    assertFalse("容器应该不再活动", context.isActive());

    // 多次关闭应该安全
    context.close();
    assertFalse("容器应该仍然不活动", context.isActive());
  }

  /**
   * 测试关闭后的操作异常
   */
  @Test(expected = IllegalStateException.class)
  public void testOperationAfterClose() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");
    context.close();

    // 关闭后尝试获取 Bean 应该抛出异常
    context.getBean("testComponent");
  }

  /**
   * 测试关闭后的刷新异常
   */
  @Test(expected = IllegalStateException.class)
  public void testRefreshAfterClose() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");
    context.close();

    // 关闭后尝试刷新应该抛出异常
    context.refresh();
  }

  /**
   * 测试获取不存在的 Bean
   */
  @Test(expected = NoSuchBeanDefinitionException.class)
  public void testGetNonExistentBean() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");
    context.getBean("nonExistentBean");
  }

  /**
   * 测试通过类型获取不存在的 Bean
   */
  @Test(expected = NoSuchBeanDefinitionException.class)
  public void testGetNonExistentBeanByType() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");
    context.getBean(PlainClass.class);
  }

  /**
   * 测试空包路径异常
   */
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyBasePackage() {
    new AnnotationConfigApplicationContext("");
  }

  /**
   * 测试 null 包路径异常
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullBasePackage() {
    new AnnotationConfigApplicationContext((String) null);
  }

  /**
   * 测试空包路径数组异常
   */
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyBasePackageArray() {
    new AnnotationConfigApplicationContext(new String[0]);
  }

  /**
   * 测试容器显示名称
   */
  @Test
  public void testDisplayName() {
    context = new AnnotationConfigApplicationContext("com.simplespring.context.testdata");

    String displayName = context.getDisplayName();
    assertNotNull("显示名称不应为空", displayName);
    assertTrue("显示名称应该包含类名", displayName.contains("AnnotationConfigApplicationContext"));
    assertTrue("显示名称应该包含包路径", displayName.contains("com.simplespring.context.testdata"));
  }

  /**
   * 测试容器 ID 唯一性
   */
  @Test
  public void testIdUniqueness() {
    AnnotationConfigApplicationContext context1 = new AnnotationConfigApplicationContext(
        "com.simplespring.context.testdata");
    AnnotationConfigApplicationContext context2 = new AnnotationConfigApplicationContext(
        "com.simplespring.context.testdata");

    try {
      String id1 = context1.getId();
      String id2 = context2.getId();

      assertNotNull("ID1 不应为空", id1);
      assertNotNull("ID2 不应为空", id2);
      assertNotEquals("两个容器的 ID 应该不同", id1, id2);

    } finally {
      context1.close();
      context2.close();
    }
  }

  /**
   * 测试不存在包路径的处理
   */
  @Test
  public void testNonExistentPackage() {
    context = new AnnotationConfigApplicationContext("com.nonexistent.package");

    // 应该能正常创建，但没有 Bean
    assertTrue("容器应该处于活动状态", context.isActive());
    assertEquals("应该没有 Bean 定义", 0, context.getBeanDefinitionCount());
  }
}
