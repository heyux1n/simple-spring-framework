package com.simplespring.example;

import com.simplespring.context.AnnotationConfigApplicationContext;
import com.simplespring.example.config.AppConfig;
import com.simplespring.example.service.OrderService;
import com.simplespring.example.service.UserService;
import com.simplespring.webmvc.DispatcherServlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 应用启动集成测试
 * 验证完整的应用功能
 */
public class ApplicationTest {

  private Application application;

  @Before
  public void setUp() {
    application = new Application();
  }

  @After
  public void tearDown() {
    if (application != null) {
      application.shutdown();
    }
  }

  @Test
  public void testApplicationStartup() {
    // 测试应用启动
    try {
      application.start();

      // 验证ApplicationContext已初始化
      AnnotationConfigApplicationContext context = application.getApplicationContext();
      assertNotNull("ApplicationContext应该被初始化", context);

      // 验证DispatcherServlet已初始化
      DispatcherServlet servlet = application.getDispatcherServlet();
      assertNotNull("DispatcherServlet应该被初始化", servlet);

    } catch (Exception e) {
      fail("应用启动不应该抛出异常: " + e.getMessage());
    }
  }

  @Test
  public void testBeanCreation() {
    // 测试Bean的创建和获取
    application.start();

    AnnotationConfigApplicationContext context = application.getApplicationContext();

    // 测试配置类Bean
    AppConfig.AppProperties appProperties = context.getBean(AppConfig.AppProperties.class);
    assertNotNull("AppProperties Bean应该存在", appProperties);
    assertEquals("应用名称应该正确", "Simple Spring Framework Example", appProperties.getAppName());
    assertEquals("版本应该正确", "1.0.0", appProperties.getVersion());

    // 测试服务Bean
    UserService userService = context.getBean(UserService.class);
    assertNotNull("UserService Bean应该存在", userService);

    OrderService orderService = context.getBean(OrderService.class);
    assertNotNull("OrderService Bean应该存在", orderService);

    // 测试切面Bean
    Object loggingAspect = context.getBean("loggingAspect");
    assertNotNull("LoggingAspect Bean应该存在", loggingAspect);

    Object performanceAspect = context.getBean("performanceAspect");
    assertNotNull("PerformanceAspect Bean应该存在", performanceAspect);

    // 测试控制器Bean
    Object userController = context.getBean("userController");
    assertNotNull("UserController Bean应该存在", userController);

    Object orderController = context.getBean("orderController");
    assertNotNull("OrderController Bean应该存在", orderController);
  }

  @Test
  public void testDependencyInjection() {
    // 测试依赖注入功能
    application.start();

    AnnotationConfigApplicationContext context = application.getApplicationContext();

    // 获取OrderService并验证其依赖注入
    OrderService orderService = context.getBean(OrderService.class);
    UserService userService = context.getBean(UserService.class);

    // 通过反射检查OrderService中的UserService依赖
    try {
      java.lang.reflect.Field userServiceField = OrderService.class.getDeclaredField("userService");
      userServiceField.setAccessible(true);
      UserService injectedUserService = (UserService) userServiceField.get(orderService);

      assertNotNull("OrderService中的UserService依赖应该被注入", injectedUserService);
      // 注意：由于我们使用的是简单的依赖注入实现，这里可能不是同一个实例
      // 但应该是同一个类型
      assertEquals("注入的UserService应该是正确的类型",
          userService.getClass(), injectedUserService.getClass());

    } catch (Exception e) {
      fail("检查依赖注入时出现异常: " + e.getMessage());
    }
  }

  @Test
  public void testFeatureDemonstration() {
    // 测试功能演示不会抛出异常
    application.start();

    try {
      application.demonstrateFeatures();
      // 如果没有抛出异常，说明演示成功
    } catch (Exception e) {
      fail("功能演示不应该抛出异常: " + e.getMessage());
    }
  }

  @Test
  public void testApplicationShutdown() {
    // 测试应用关闭
    application.start();

    // 验证启动后的状态
    assertNotNull("ApplicationContext应该存在", application.getApplicationContext());
    assertNotNull("DispatcherServlet应该存在", application.getDispatcherServlet());

    // 关闭应用
    try {
      application.shutdown();
      // 关闭后应该不抛出异常
    } catch (Exception e) {
      fail("应用关闭不应该抛出异常: " + e.getMessage());
    }
  }

  @Test
  public void testConfigurationClass() {
    // 测试配置类功能
    AppConfig config = new AppConfig();

    // 测试Bean方法
    UserService userService = config.userService();
    assertNotNull("配置类应该能创建UserService", userService);

    Object loggingAspect = config.loggingAspect();
    assertNotNull("配置类应该能创建LoggingAspect", loggingAspect);

    Object performanceAspect = config.performanceAspect();
    assertNotNull("配置类应该能创建PerformanceAspect", performanceAspect);

    AppConfig.AppProperties appProperties = config.appProperties();
    assertNotNull("配置类应该能创建AppProperties", appProperties);
    assertEquals("应用名称应该正确", "Simple Spring Framework Example", appProperties.getAppName());
  }

  @Test
  public void testAppProperties() {
    // 测试应用属性类
    AppConfig.AppProperties properties = new AppConfig.AppProperties();

    properties.setAppName("测试应用");
    properties.setVersion("2.0.0");
    properties.setDescription("测试描述");
    properties.setAuthor("测试作者");

    assertEquals("应用名称应该正确", "测试应用", properties.getAppName());
    assertEquals("版本应该正确", "2.0.0", properties.getVersion());
    assertEquals("描述应该正确", "测试描述", properties.getDescription());
    assertEquals("作者应该正确", "测试作者", properties.getAuthor());

    String toString = properties.toString();
    assertNotNull("toString不应该为空", toString);
    assertTrue("toString应该包含应用名称", toString.contains("测试应用"));
    assertTrue("toString应该包含版本", toString.contains("2.0.0"));
  }

  @Test
  public void testMultipleStartupShutdown() {
    // 测试多次启动和关闭
    for (int i = 0; i < 3; i++) {
      try {
        application.start();
        assertNotNull("第" + (i + 1) + "次启动后ApplicationContext应该存在",
            application.getApplicationContext());

        application.shutdown();

        // 重新创建Application实例用于下次测试
        if (i < 2) {
          application = new Application();
        }

      } catch (Exception e) {
        fail("第" + (i + 1) + "次启动/关闭不应该抛出异常: " + e.getMessage());
      }
    }
  }
}
