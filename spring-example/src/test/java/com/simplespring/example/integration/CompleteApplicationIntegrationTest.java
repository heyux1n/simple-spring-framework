package com.simplespring.example.integration;

import com.simplespring.context.AnnotationConfigApplicationContext;
import com.simplespring.example.Application;
import com.simplespring.example.entity.Order;
import com.simplespring.example.entity.OrderStatus;
import com.simplespring.example.entity.User;
import com.simplespring.example.service.OrderService;
import com.simplespring.example.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 完整应用集成测试
 * 验证整个应用的端到端功能
 */
public class CompleteApplicationIntegrationTest {

  private Application application;
  private AnnotationConfigApplicationContext context;
  private UserService userService;
  private OrderService orderService;

  @Before
  public void setUp() {
    application = new Application();
    application.start();
    context = application.getApplicationContext();
    userService = context.getBean(UserService.class);
    orderService = context.getBean(OrderService.class);
  }

  @After
  public void tearDown() {
    if (application != null) {
      application.shutdown();
    }
  }

  @Test
  public void testCompleteUserWorkflow() {
    // 测试完整的用户工作流程

    // 1. 用户注册
    User user = new User("integrationuser", "integration@example.com", "password123");
    User createdUser = userService.createUser(user);

    assertNotNull("用户应该被创建", createdUser);
    assertNotNull("用户ID应该被生成", createdUser.getId());
    assertEquals("用户名应该匹配", "integrationuser", createdUser.getUsername());
    assertEquals("邮箱应该匹配", "integration@example.com", createdUser.getEmail());
    assertTrue("用户应该是激活状态", createdUser.isActive());

    // 2. 用户登录
    User authenticatedUser = userService.authenticate("integrationuser", "password123");
    assertNotNull("用户认证应该成功", authenticatedUser);
    assertEquals("认证用户应该匹配", createdUser.getId(), authenticatedUser.getId());

    // 3. 用户信息查询
    User foundUser = userService.findById(createdUser.getId());
    assertNotNull("应该能根据ID找到用户", foundUser);
    assertEquals("找到的用户应该匹配", createdUser.getUsername(), foundUser.getUsername());

    User foundByUsername = userService.findByUsername("integrationuser");
    assertNotNull("应该能根据用户名找到用户", foundByUsername);
    assertEquals("找到的用户应该匹配", createdUser.getId(), foundByUsername.getId());

    User foundByEmail = userService.findByEmail("integration@example.com");
    assertNotNull("应该能根据邮箱找到用户", foundByEmail);
    assertEquals("找到的用户应该匹配", createdUser.getId(), foundByEmail.getId());

    // 4. 用户信息更新
    createdUser.setUsername("updateduser");
    createdUser.setEmail("updated@example.com");
    User updatedUser = userService.updateUser(createdUser);

    assertEquals("用户名应该被更新", "updateduser", updatedUser.getUsername());
    assertEquals("邮箱应该被更新", "updated@example.com", updatedUser.getEmail());

    // 5. 用户状态管理
    boolean deactivated = userService.deactivateUser(createdUser.getId());
    assertTrue("用户禁用应该成功", deactivated);
    assertFalse("用户应该是禁用状态", createdUser.isActive());

    boolean activated = userService.activateUser(createdUser.getId());
    assertTrue("用户激活应该成功", activated);
    assertTrue("用户应该是激活状态", createdUser.isActive());
  }

  @Test
  public void testCompleteOrderWorkflow() {
    // 测试完整的订单工作流程

    // 先创建用户
    User user = userService.createUser(new User("orderuser", "order@example.com", "password123"));

    // 1. 创建订单
    Order order = orderService.createOrder(user.getId(), "集成测试商品", 3, new BigDecimal("199.99"));

    assertNotNull("订单应该被创建", order);
    assertNotNull("订单ID应该被生成", order.getId());
    assertNotNull("订单号应该被生成", order.getOrderNumber());
    assertEquals("用户ID应该匹配", user.getId(), order.getUserId());
    assertEquals("商品名称应该匹配", "集成测试商品", order.getProductName());
    assertEquals("数量应该匹配", Integer.valueOf(3), order.getQuantity());
    assertEquals("价格应该匹配", new BigDecimal("199.99"), order.getPrice());
    assertEquals("总金额应该正确", new BigDecimal("599.97"), order.getTotalAmount());
    assertEquals("初始状态应该是待处理", OrderStatus.PENDING, order.getStatus());

    // 2. 订单查询
    Order foundOrder = orderService.findById(order.getId());
    assertNotNull("应该能根据ID找到订单", foundOrder);
    assertEquals("找到的订单应该匹配", order.getOrderNumber(), foundOrder.getOrderNumber());

    Order foundByOrderNumber = orderService.findByOrderNumber(order.getOrderNumber());
    assertNotNull("应该能根据订单号找到订单", foundByOrderNumber);
    assertEquals("找到的订单应该匹配", order.getId(), foundByOrderNumber.getId());

    List<Order> userOrders = orderService.findByUserId(user.getId());
    assertNotNull("用户订单列表不应为空", userOrders);
    assertTrue("用户应该有订单", userOrders.size() > 0);
    assertEquals("订单应该属于正确的用户", user.getId(), userOrders.get(0).getUserId());

    // 3. 订单状态流转
    // 确认订单
    boolean confirmed = orderService.confirmOrder(order.getId());
    assertTrue("订单确认应该成功", confirmed);
    assertEquals("订单状态应该是已确认", OrderStatus.CONFIRMED, order.getStatus());

    // 处理订单
    boolean processed = orderService.processOrder(order.getId());
    assertTrue("订单处理应该成功", processed);
    assertEquals("订单状态应该是处理中", OrderStatus.PROCESSING, order.getStatus());

    // 发货订单
    boolean shipped = orderService.shipOrder(order.getId());
    assertTrue("订单发货应该成功", shipped);
    assertEquals("订单状态应该是已发货", OrderStatus.SHIPPED, order.getStatus());

    // 完成订单
    boolean completed = orderService.completeOrder(order.getId());
    assertTrue("订单完成应该成功", completed);
    assertEquals("订单状态应该是已完成", OrderStatus.COMPLETED, order.getStatus());
  }

  @Test
  public void testOrderCancellationWorkflow() {
    // 测试订单取消工作流程

    User user = userService.createUser(new User("canceluser", "cancel@example.com", "password123"));

    // 创建待处理订单并取消
    Order pendingOrder = orderService.createOrder(user.getId(), "待处理订单", 1, new BigDecimal("100.00"));
    boolean cancelledPending = orderService.cancelOrder(pendingOrder.getId());
    assertTrue("待处理订单取消应该成功", cancelledPending);
    assertEquals("订单状态应该是已取消", OrderStatus.CANCELLED, pendingOrder.getStatus());

    // 创建已确认订单并取消
    Order confirmedOrder = orderService.createOrder(user.getId(), "已确认订单", 1, new BigDecimal("200.00"));
    orderService.confirmOrder(confirmedOrder.getId());
    boolean cancelledConfirmed = orderService.cancelOrder(confirmedOrder.getId());
    assertTrue("已确认订单取消应该成功", cancelledConfirmed);
    assertEquals("订单状态应该是已取消", OrderStatus.CANCELLED, confirmedOrder.getStatus());

    // 尝试取消处理中的订单（应该失败）
    Order processingOrder = orderService.createOrder(user.getId(), "处理中订单", 1, new BigDecimal("300.00"));
    orderService.confirmOrder(processingOrder.getId());
    orderService.processOrder(processingOrder.getId());
    boolean cancelledProcessing = orderService.cancelOrder(processingOrder.getId());
    assertFalse("处理中订单取消应该失败", cancelledProcessing);
    assertEquals("订单状态应该仍是处理中", OrderStatus.PROCESSING, processingOrder.getStatus());
  }

  @Test
  public void testOrderStatistics() {
    // 测试订单统计功能

    User user = userService.createUser(new User("statsuser", "stats@example.com", "password123"));

    // 创建不同状态的订单
    Order order1 = orderService.createOrder(user.getId(), "统计订单1", 1, new BigDecimal("100.00"));
    Order order2 = orderService.createOrder(user.getId(), "统计订单2", 2, new BigDecimal("150.00"));
    Order order3 = orderService.createOrder(user.getId(), "统计订单3", 1, new BigDecimal("200.00"));
    Order order4 = orderService.createOrder(user.getId(), "统计订单4", 3, new BigDecimal("50.00"));

    // 设置不同状态
    orderService.confirmOrder(order2.getId());

    orderService.confirmOrder(order3.getId());
    orderService.processOrder(order3.getId());
    orderService.shipOrder(order3.getId());
    orderService.completeOrder(order3.getId());

    orderService.cancelOrder(order4.getId());

    // 计算统计信息
    OrderService.OrderStatistics statistics = orderService.calculateStatistics();

    assertNotNull("统计信息不应为空", statistics);
    assertTrue("总订单数应该大于等于4", statistics.getTotalOrders() >= 4);
    assertTrue("待处理订单数应该大于等于1", statistics.getPendingOrders() >= 1);
    assertTrue("已确认订单数应该大于等于1", statistics.getConfirmedOrders() >= 1);
    assertTrue("已完成订单数应该大于等于1", statistics.getCompletedOrders() >= 1);
    assertTrue("已取消订单数应该大于等于1", statistics.getCancelledOrders() >= 1);
    assertTrue("总金额应该大于等于200", statistics.getTotalAmount().compareTo(new BigDecimal("200.00")) >= 0);
  }

  @Test
  public void testErrorHandling() {
    // 测试错误处理

    // 测试创建重复用户
    userService.createUser(new User("duplicate", "duplicate@example.com", "password123"));
    try {
      userService.createUser(new User("duplicate", "different@example.com", "password456"));
      fail("创建重复用户名应该抛出异常");
    } catch (IllegalArgumentException e) {
      assertTrue("异常消息应该包含用户名已存在", e.getMessage().contains("用户名已存在"));
    }

    // 测试为不存在用户创建订单
    try {
      orderService.createOrder(999L, "不存在用户订单", 1, new BigDecimal("100.00"));
      fail("为不存在用户创建订单应该抛出异常");
    } catch (IllegalArgumentException e) {
      assertTrue("异常消息应该包含用户不存在", e.getMessage().contains("用户不存在"));
    }

    // 测试无效订单状态转换
    User user = userService.createUser(new User("erroruser", "error@example.com", "password123"));
    Order order = orderService.createOrder(user.getId(), "错误测试订单", 1, new BigDecimal("100.00"));

    // 尝试直接处理未确认的订单
    boolean processed = orderService.processOrder(order.getId());
    assertFalse("处理未确认订单应该失败", processed);
    assertEquals("订单状态应该仍是待处理", OrderStatus.PENDING, order.getStatus());
  }

  @Test
  public void testConcurrentOperations() {
    // 测试并发操作

    User user = userService.createUser(new User("concurrentuser", "concurrent@example.com", "password123"));

    // 并发创建多个订单
    Thread[] threads = new Thread[5];
    Order[] orders = new Order[5];

    for (int i = 0; i < 5; i++) {
      final int index = i;
      threads[i] = new Thread(new Runnable() {
        public void run() {
          try {
            orders[index] = orderService.createOrder(
                user.getId(),
                "并发订单" + index,
                1,
                new BigDecimal("100.00"));
          } catch (Exception e) {
            // 记录异常但不抛出，让测试继续
            e.printStackTrace();
          }
        }
      });
    }

    // 启动所有线程
    for (Thread thread : threads) {
      thread.start();
    }

    // 等待所有线程完成
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    // 验证所有订单都被创建
    int successCount = 0;
    for (Order order : orders) {
      if (order != null) {
        successCount++;
        assertNotNull("订单ID应该被生成", order.getId());
        assertEquals("用户ID应该正确", user.getId(), order.getUserId());
      }
    }

    assertEquals("所有并发订单都应该被成功创建", 5, successCount);
  }

  @Test
  public void testDataConsistency() {
    // 测试数据一致性

    User user = userService.createUser(new User("consistencyuser", "consistency@example.com", "password123"));

    // 创建订单
    Order order = orderService.createOrder(user.getId(), "一致性测试订单", 2, new BigDecimal("250.00"));

    // 验证订单总金额计算正确
    assertEquals("总金额应该正确计算", new BigDecimal("500.00"), order.getTotalAmount());

    // 更新订单数量，验证总金额自动更新
    order.setQuantity(3);
    assertEquals("更新数量后总金额应该重新计算", new BigDecimal("750.00"), order.getTotalAmount());

    // 更新订单价格，验证总金额自动更新
    order.setPrice(new BigDecimal("100.00"));
    assertEquals("更新价格后总金额应该重新计算", new BigDecimal("300.00"), order.getTotalAmount());

    // 验证订单状态变更时间戳更新
    java.util.Date originalUpdateTime = order.getUpdateTime();

    try {
      Thread.sleep(10); // 确保时间差异
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    orderService.confirmOrder(order.getId());
    assertTrue("状态变更后更新时间应该改变", order.getUpdateTime().after(originalUpdateTime));
  }
}
