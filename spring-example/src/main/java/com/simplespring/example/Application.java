package com.simplespring.example;

import com.simplespring.context.AnnotationConfigApplicationContext;
import com.simplespring.example.config.AppConfig;
import com.simplespring.example.entity.Order;
import com.simplespring.example.entity.User;
import com.simplespring.example.service.OrderService;
import com.simplespring.example.service.UserService;
import com.simplespring.webmvc.DispatcherServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 应用主类
 * 初始化 ApplicationContext 和 DispatcherServlet
 * 演示完整的应用功能
 */
public class Application {

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  private AnnotationConfigApplicationContext applicationContext;
  private DispatcherServlet dispatcherServlet;

  public static void main(String[] args) {
    logger.info("=== 简易Spring框架示例应用启动 ===");

    Application app = new Application();
    try {
      app.start();
      app.demonstrateFeatures();
    } catch (Exception e) {
      logger.error("应用启动失败", e);
    } finally {
      app.shutdown();
    }

    logger.info("=== 简易Spring框架示例应用结束 ===");
  }

  /**
   * 启动应用
   */
  public void start() {
    logger.info("正在启动应用...");

    // 初始化ApplicationContext
    initializeApplicationContext();

    // 初始化DispatcherServlet
    initializeDispatcherServlet();

    logger.info("应用启动完成");
  }

  /**
   * 初始化ApplicationContext
   */
  private void initializeApplicationContext() {
    logger.info("初始化 ApplicationContext...");

    try {
      // 创建基于注解的应用上下文
      applicationContext = new AnnotationConfigApplicationContext();

      // 注册配置类
      applicationContext.register(AppConfig.class);

      // 设置包扫描路径
      applicationContext.scan("com.simplespring.example");

      // 刷新容器
      applicationContext.refresh();

      logger.info("ApplicationContext 初始化完成");

      // 显示容器中的Bean信息
      displayBeanInfo();

    } catch (Exception e) {
      logger.error("ApplicationContext 初始化失败", e);
      throw new RuntimeException("ApplicationContext 初始化失败", e);
    }
  }

  /**
   * 初始化DispatcherServlet
   */
  private void initializeDispatcherServlet() {
    logger.info("初始化 DispatcherServlet...");

    try {
      // 创建DispatcherServlet并设置ApplicationContext
      dispatcherServlet = new DispatcherServlet();
      dispatcherServlet.setApplicationContext(applicationContext);

      // 初始化DispatcherServlet
      dispatcherServlet.init();

      logger.info("DispatcherServlet 初始化完成");

    } catch (Exception e) {
      logger.error("DispatcherServlet 初始化失败", e);
      throw new RuntimeException("DispatcherServlet 初始化失败", e);
    }
  }

  /**
   * 显示容器中的Bean信息
   */
  private void displayBeanInfo() {
    logger.info("=== 容器中的Bean信息 ===");

    try {
      // 显示配置属性
      AppConfig.AppProperties appProperties = applicationContext.getBean(AppConfig.AppProperties.class);
      logger.info("应用属性: {}", appProperties);

      // 显示服务Bean
      UserService userService = applicationContext.getBean(UserService.class);
      logger.info("用户服务: {}", userService.getClass().getSimpleName());

      OrderService orderService = applicationContext.getBean(OrderService.class);
      logger.info("订单服务: {}", orderService.getClass().getSimpleName());

      // 显示切面Bean
      logger.info("日志切面: {}", applicationContext.getBean("loggingAspect").getClass().getSimpleName());
      logger.info("性能切面: {}", applicationContext.getBean("performanceAspect").getClass().getSimpleName());

      // 显示控制器Bean
      logger.info("用户控制器: {}", applicationContext.getBean("userController").getClass().getSimpleName());
      logger.info("订单控制器: {}", applicationContext.getBean("orderController").getClass().getSimpleName());

    } catch (Exception e) {
      logger.warn("显示Bean信息时出现异常", e);
    }

    logger.info("========================");
  }

  /**
   * 演示框架功能
   */
  public void demonstrateFeatures() {
    logger.info("=== 开始演示框架功能 ===");

    try {
      // 演示IoC和依赖注入
      demonstrateIoC();

      // 演示AOP功能
      demonstrateAOP();

      // 演示完整的业务流程
      demonstrateBusinessWorkflow();

    } catch (Exception e) {
      logger.error("演示功能时出现异常", e);
    }

    logger.info("=== 框架功能演示完成 ===");
  }

  /**
   * 演示IoC和依赖注入功能
   */
  private void demonstrateIoC() {
    logger.info("--- 演示IoC和依赖注入功能 ---");

    try {
      // 从容器获取服务Bean
      UserService userService = applicationContext.getBean(UserService.class);
      OrderService orderService = applicationContext.getBean(OrderService.class);

      logger.info("成功从容器获取UserService: {}", userService != null);
      logger.info("成功从容器获取OrderService: {}", orderService != null);

      // 验证依赖注入
      // OrderService应该自动注入了UserService
      User testUser = userService.createUser(new User("ioctest", "ioc@example.com", "password123"));
      logger.info("通过IoC容器创建用户成功: {}", testUser.getUsername());

      // 测试OrderService中注入的UserService
      Order testOrder = orderService.createOrder(testUser.getId(), "IoC测试商品", 1, new BigDecimal("99.99"));
      logger.info("通过依赖注入创建订单成功: {}", testOrder.getOrderNumber());

    } catch (Exception e) {
      logger.error("IoC演示失败", e);
    }
  }

  /**
   * 演示AOP功能
   */
  private void demonstrateAOP() {
    logger.info("--- 演示AOP功能 ---");

    try {
      UserService userService = applicationContext.getBean(UserService.class);
      OrderService orderService = applicationContext.getBean(OrderService.class);

      logger.info("注意观察以下操作的AOP日志输出:");

      // 创建用户 - 应该触发AOP日志
      User aopUser = userService.createUser(new User("aoptest", "aop@example.com", "password123"));

      // 用户认证 - 应该触发AOP日志和性能监控
      User authenticatedUser = userService.authenticate("aoptest", "password123");
      logger.info("AOP用户认证结果: {}", authenticatedUser != null ? "成功" : "失败");

      // 创建订单 - 应该触发AOP日志和性能监控
      Order aopOrder = orderService.createOrder(aopUser.getId(), "AOP测试商品", 2, new BigDecimal("150.00"));

      // 订单状态变更 - 应该触发AOP日志
      orderService.confirmOrder(aopOrder.getId());
      orderService.processOrder(aopOrder.getId());
      orderService.shipOrder(aopOrder.getId());
      orderService.completeOrder(aopOrder.getId());

      // 计算统计信息 - 应该触发性能监控
      OrderService.OrderStatistics statistics = orderService.calculateStatistics();
      logger.info("订单统计信息: {}", statistics);

    } catch (Exception e) {
      logger.error("AOP演示失败", e);
    }
  }

  /**
   * 演示完整的业务工作流程
   */
  private void demonstrateBusinessWorkflow() {
    logger.info("--- 演示完整业务工作流程 ---");

    try {
      UserService userService = applicationContext.getBean(UserService.class);
      OrderService orderService = applicationContext.getBean(OrderService.class);

      // 1. 用户注册
      logger.info("1. 用户注册");
      User customer = userService.createUser(new User("customer", "customer@example.com", "password123"));

      // 2. 用户登录
      logger.info("2. 用户登录");
      User loggedInUser = userService.authenticate("customer", "password123");
      if (loggedInUser == null) {
        throw new RuntimeException("用户登录失败");
      }

      // 3. 创建订单
      logger.info("3. 创建订单");
      Order order1 = orderService.createOrder(customer.getId(), "笔记本电脑", 1, new BigDecimal("5999.00"));
      Order order2 = orderService.createOrder(customer.getId(), "无线鼠标", 2, new BigDecimal("99.00"));

      // 4. 订单处理流程
      logger.info("4. 处理第一个订单");
      orderService.confirmOrder(order1.getId());
      orderService.processOrder(order1.getId());
      orderService.shipOrder(order1.getId());
      orderService.completeOrder(order1.getId());

      logger.info("5. 取消第二个订单");
      orderService.cancelOrder(order2.getId());

      // 6. 查询用户订单
      logger.info("6. 查询用户订单");
      java.util.List<Order> userOrders = orderService.findByUserId(customer.getId());
      logger.info("用户 {} 共有 {} 个订单", customer.getUsername(), userOrders.size());

      // 7. 生成统计报告
      logger.info("7. 生成统计报告");
      OrderService.OrderStatistics finalStats = orderService.calculateStatistics();
      logger.info("最终统计: 总订单数={}, 已完成={}, 已取消={}, 总金额={}",
          finalStats.getTotalOrders(),
          finalStats.getCompletedOrders(),
          finalStats.getCancelledOrders(),
          finalStats.getTotalAmount());

    } catch (Exception e) {
      logger.error("业务工作流程演示失败", e);
    }
  }

  /**
   * 关闭应用
   */
  public void shutdown() {
    logger.info("正在关闭应用...");

    try {
      if (dispatcherServlet != null) {
        dispatcherServlet.destroy();
        logger.info("DispatcherServlet 已关闭");
      }

      if (applicationContext != null) {
        applicationContext.close();
        logger.info("ApplicationContext 已关闭");
      }

    } catch (Exception e) {
      logger.error("关闭应用时出现异常", e);
    }

    logger.info("应用关闭完成");
  }

  /**
   * 获取ApplicationContext
   */
  public AnnotationConfigApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
   * 获取DispatcherServlet
   */
  public DispatcherServlet getDispatcherServlet() {
    return dispatcherServlet;
  }
}
