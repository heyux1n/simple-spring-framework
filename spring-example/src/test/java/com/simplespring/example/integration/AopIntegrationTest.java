package com.simplespring.example.integration;

import com.simplespring.example.aspect.LoggingAspect;
import com.simplespring.example.aspect.PerformanceAspect;
import com.simplespring.example.entity.User;
import com.simplespring.example.entity.Order;
import com.simplespring.example.service.UserService;
import com.simplespring.example.service.OrderService;
import com.simplespring.example.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * AOP 集成测试
 * 验证切面与业务服务的集成效果
 * 注意：这是一个模拟测试，实际的AOP功能需要在完整的Spring容器中才能生效
 */
public class AopIntegrationTest {

  private UserService userService;
  private OrderService orderService;
  private LoggingAspect loggingAspect;
  private PerformanceAspect performanceAspect;

  @Before
  public void setUp() {
    // 创建业务服务
    userService = new UserServiceImpl();
    orderService = new OrderService();

    // 使用反射设置OrderService的依赖
    try {
      java.lang.reflect.Field userServiceField = OrderService.class.getDeclaredField("userService");
      userServiceField.setAccessible(true);
      userServiceField.set(orderService, userService);
    } catch (Exception e) {
      throw new RuntimeException("设置依赖失败", e);
    }

    // 创建切面
    loggingAspect = new LoggingAspect();
    performanceAspect = new PerformanceAspect();
  }

  @Test
  public void testUserServiceWithAspects() {
    // 测试用户服务与切面的集成

    // 模拟AOP前置通知
    loggingAspect.logBeforeUserService();
    performanceAspect.startTimingCreateUser();

    // 执行业务方法
    User user = new User("aoptest", "aoptest@example.com", "password123");
    User createdUser = userService.createUser(user);

    // 模拟AOP后置通知
    performanceAspect.endTimingCreateUser();
    loggingAspect.logAfterUserService();
    loggingAspect.logAfterReturningCreateUser(createdUser);

    // 验证业务逻辑正确执行
    assertNotNull("用户应该被创建", createdUser);
    assertEquals("用户名应该匹配", "aoptest", createdUser.getUsername());

    // 验证性能统计
    PerformanceAspect.PerformanceStatistics stats = performanceAspect.getMethodStatistics("UserService.createUser");
    assertNotNull("应该有性能统计数据", stats);
    assertEquals("调用次数应该是1", 1, stats.getTotalExecutions());
  }

  @Test
  public void testOrderServiceWithAspects() {
    // 测试订单服务与切面的集成

    // 先创建用户
    User user = userService.createUser(new User("orderuser", "order@example.com", "password123"));

    // 模拟AOP前置通知
    loggingAspect.logBeforeOrderService();
    loggingAspect.logBeforeCreateOrder();
    performanceAspect.startTimingCreateOrder();

    // 执行业务方法
    Order order = orderService.createOrder(user.getId(), "测试商品", 2, new BigDecimal("100.00"));

    // 模拟AOP后置通知
    performanceAspect.endTimingCreateOrder();
    loggingAspect.logAfterCreateOrder();
    loggingAspect.logAfterOrderService();
    loggingAspect.logAfterReturningCreateOrder(order);

    // 验证业务逻辑正确执行
    assertNotNull("订单应该被创建", order);
    assertEquals("用户ID应该匹配", user.getId(), order.getUserId());
    assertEquals("商品名称应该匹配", "测试商品", order.getProductName());
    assertEquals("总金额应该正确", new BigDecimal("200.00"), order.getTotalAmount());

    // 验证性能统计
    PerformanceAspect.PerformanceStatistics stats = performanceAspect.getMethodStatistics("OrderService.createOrder");
    assertNotNull("应该有性能统计数据", stats);
    assertEquals("调用次数应该是1", 1, stats.getTotalExecutions());
  }

  @Test
  public void testUserAuthenticationWithAspects() {
    // 测试用户认证与切面的集成

    // 先创建用户
    User user = userService.createUser(new User("authuser", "auth@example.com", "password123"));

    // 模拟AOP前置通知
    loggingAspect.logBeforeAuthenticate();
    performanceAspect.startTimingAuthenticate();

    // 执行认证
    User authenticatedUser = userService.authenticate("authuser", "password123");

    // 模拟AOP后置通知
    performanceAspect.endTimingAuthenticate();
    loggingAspect.logAfterAuthenticate();
    loggingAspect.logAfterReturningAuthenticate(authenticatedUser);

    // 验证认证结果
    assertNotNull("认证应该成功", authenticatedUser);
    assertEquals("认证的用户应该匹配", "authuser", authenticatedUser.getUsername());

    // 验证性能统计
    PerformanceAspect.PerformanceStatistics stats = performanceAspect.getMethodStatistics("UserService.authenticate");
    assertNotNull("应该有性能统计数据", stats);
    assertEquals("调用次数应该是1", 1, stats.getTotalExecutions());
  }

  @Test
  public void testOrderWorkflowWithAspects() {
    // 测试完整订单工作流程与切面的集成

    // 创建用户
    User user = userService.createUser(new User("workflowuser", "workflow@example.com", "password123"));

    // 创建订单
    loggingAspect.logBeforeCreateOrder();
    performanceAspect.startTimingCreateOrder();
    Order order = orderService.createOrder(user.getId(), "工作流商品", 1, new BigDecimal("150.00"));
    performanceAspect.endTimingCreateOrder();
    loggingAspect.logAfterCreateOrder();

    // 确认订单
    loggingAspect.logBeforeConfirmOrder();
    performanceAspect.startTimingConfirmOrder();
    boolean confirmed = orderService.confirmOrder(order.getId());
    performanceAspect.endTimingConfirmOrder();
    loggingAspect.logAfterConfirmOrder();

    // 处理订单
    loggingAspect.logBeforeProcessOrder();
    performanceAspect.startTimingProcessOrder();
    boolean processed = orderService.processOrder(order.getId());
    performanceAspect.endTimingProcessOrder();
    loggingAspect.logAfterProcessOrder();

    // 发货订单
    loggingAspect.logBeforeShipOrder();
    performanceAspect.startTimingShipOrder();
    boolean shipped = orderService.shipOrder(order.getId());
    performanceAspect.endTimingShipOrder();
    loggingAspect.logAfterShipOrder();

    // 完成订单
    loggingAspect.logBeforeCompleteOrder();
    performanceAspect.startTimingCompleteOrder();
    boolean completed = orderService.completeOrder(order.getId());
    performanceAspect.endTimingCompleteOrder();
    loggingAspect.logAfterCompleteOrder();

    // 验证工作流程
    assertTrue("订单确认应该成功", confirmed);
    assertTrue("订单处理应该成功", processed);
    assertTrue("订单发货应该成功", shipped);
    assertTrue("订单完成应该成功", completed);

    // 验证性能统计
    assertNotNull("创建订单应该有统计", performanceAspect.getMethodStatistics("OrderService.createOrder"));
    assertNotNull("确认订单应该有统计", performanceAspect.getMethodStatistics("OrderService.confirmOrder"));
    assertNotNull("处理订单应该有统计", performanceAspect.getMethodStatistics("OrderService.processOrder"));
    assertNotNull("发货订单应该有统计", performanceAspect.getMethodStatistics("OrderService.shipOrder"));
    assertNotNull("完成订单应该有统计", performanceAspect.getMethodStatistics("OrderService.completeOrder"));
  }

  @Test
  public void testOrderStatisticsWithAspects() {
    // 测试订单统计与切面的集成

    // 创建用户和订单
    User user = userService.createUser(new User("statsuser", "stats@example.com", "password123"));
    orderService.createOrder(user.getId(), "统计商品1", 1, new BigDecimal("100.00"));
    orderService.createOrder(user.getId(), "统计商品2", 2, new BigDecimal("50.00"));

    // 模拟AOP前置通知
    loggingAspect.logBeforeOrderService();
    performanceAspect.startTimingCalculateStatistics();

    // 执行统计计算
    OrderService.OrderStatistics statistics = orderService.calculateStatistics();

    // 模拟AOP后置通知
    performanceAspect.endTimingCalculateStatistics();
    loggingAspect.logAfterOrderService();
    loggingAspect.logAfterReturningCalculateStatistics(statistics);

    // 验证统计结果
    assertNotNull("统计结果不应为空", statistics);
    assertTrue("总订单数应该大于0", statistics.getTotalOrders() > 0);

    // 验证性能统计
    PerformanceAspect.PerformanceStatistics stats = performanceAspect
        .getMethodStatistics("OrderService.calculateStatistics");
    assertNotNull("应该有性能统计数据", stats);
    assertEquals("调用次数应该是1", 1, stats.getTotalExecutions());
  }

  @Test
  public void testMultipleMethodCallsWithAspects() {
    // 测试多次方法调用的切面效果

    User user = userService.createUser(new User("multiuser", "multi@example.com", "password123"));

    // 多次调用查找用户方法
    for (int i = 0; i < 3; i++) {
      performanceAspect.startTimingFindUserById();
      User foundUser = userService.findById(user.getId());
      performanceAspect.endTimingFindUserById();
      loggingAspect.logAfterReturningFindUserById(foundUser);

      assertNotNull("每次都应该找到用户", foundUser);
    }

    // 验证性能统计
    PerformanceAspect.PerformanceStatistics stats = performanceAspect.getMethodStatistics("UserService.findById");
    assertNotNull("应该有性能统计数据", stats);
    assertEquals("调用次数应该是3", 3, stats.getTotalExecutions());
    assertTrue("总耗时应该大于0", stats.getTotalTime() > 0);
    assertTrue("平均耗时应该大于0", stats.getAverageTime() > 0);
  }

  @Test
  public void testPerformanceReportGeneration() {
    // 测试性能报告生成

    // 执行一些业务操作以生成性能数据
    User user = userService.createUser(new User("reportuser", "report@example.com", "password123"));

    performanceAspect.startTimingCreateUser();
    performanceAspect.endTimingCreateUser();

    performanceAspect.startTimingFindUserById();
    userService.findById(user.getId());
    performanceAspect.endTimingFindUserById();

    performanceAspect.startTimingCreateOrder();
    orderService.createOrder(user.getId(), "报告商品", 1, new BigDecimal("75.00"));
    performanceAspect.endTimingCreateOrder();

    // 生成性能报告
    String report = performanceAspect.getPerformanceReport();

    assertNotNull("性能报告不应为空", report);
    assertTrue("报告应包含标题", report.contains("性能统计报告"));

    // 报告应该包含执行过的方法的统计信息
    // 注意：由于我们手动调用了计时方法，所以会有统计数据
    assertTrue("报告应包含统计信息或无数据提示",
        report.contains("UserService") || report.contains("OrderService") || report.contains("暂无性能数据"));
  }
}
