package com.simplespring.example.service;

import com.simplespring.example.entity.Order;
import com.simplespring.example.entity.OrderStatus;
import com.simplespring.example.entity.User;
import com.simplespring.example.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 订单服务单元测试
 * 验证订单服务的业务逻辑正确性
 */
public class OrderServiceTest {

  private OrderService orderService;
  private UserService userService;
  private User testUser;

  @Before
  public void setUp() {
    userService = new UserServiceImpl();
    orderService = new OrderService();

    // 使用反射设置依赖（模拟依赖注入）
    try {
      java.lang.reflect.Field userServiceField = OrderService.class.getDeclaredField("userService");
      userServiceField.setAccessible(true);
      userServiceField.set(orderService, userService);
    } catch (Exception e) {
      throw new RuntimeException("设置依赖失败", e);
    }

    // 创建测试用户
    testUser = userService.createUser(new User("testuser", "test@example.com", "password123"));
  }

  @Test
  public void testCreateOrder() {
    // 测试创建订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 2, new BigDecimal("100.00"));

    assertNotNull("创建的订单不应为空", order);
    assertNotNull("订单ID应该被自动生成", order.getId());
    assertNotNull("订单号应该被自动生成", order.getOrderNumber());
    assertEquals("用户ID应该匹配", testUser.getId(), order.getUserId());
    assertEquals("商品名称应该匹配", "测试商品", order.getProductName());
    assertEquals("商品数量应该匹配", Integer.valueOf(2), order.getQuantity());
    assertEquals("商品价格应该匹配", new BigDecimal("100.00"), order.getPrice());
    assertEquals("总金额应该正确计算", new BigDecimal("200.00"), order.getTotalAmount());
    assertEquals("订单状态应该是待处理", OrderStatus.PENDING, order.getStatus());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateOrderWithNullUserId() {
    // 测试创建订单时用户ID为空
    orderService.createOrder(null, "测试商品", 2, new BigDecimal("100.00"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateOrderWithNullProductName() {
    // 测试创建订单时商品名称为空
    orderService.createOrder(testUser.getId(), null, 2, new BigDecimal("100.00"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateOrderWithZeroQuantity() {
    // 测试创建订单时数量为0
    orderService.createOrder(testUser.getId(), "测试商品", 0, new BigDecimal("100.00"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateOrderWithNegativeQuantity() {
    // 测试创建订单时数量为负数
    orderService.createOrder(testUser.getId(), "测试商品", -1, new BigDecimal("100.00"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateOrderWithZeroPrice() {
    // 测试创建订单时价格为0
    orderService.createOrder(testUser.getId(), "测试商品", 2, BigDecimal.ZERO);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateOrderWithNegativePrice() {
    // 测试创建订单时价格为负数
    orderService.createOrder(testUser.getId(), "测试商品", 2, new BigDecimal("-100.00"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateOrderWithNonExistentUser() {
    // 测试创建订单时用户不存在
    orderService.createOrder(999L, "测试商品", 2, new BigDecimal("100.00"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateOrderWithInactiveUser() {
    // 测试创建订单时用户被禁用
    userService.deactivateUser(testUser.getId());
    orderService.createOrder(testUser.getId(), "测试商品", 2, new BigDecimal("100.00"));
  }

  @Test
  public void testFindById() {
    // 测试根据ID查找订单
    Order createdOrder = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));

    Order foundOrder = orderService.findById(createdOrder.getId());
    assertNotNull("应该能找到订单", foundOrder);
    assertEquals("订单ID应该匹配", createdOrder.getId(), foundOrder.getId());
    assertEquals("订单号应该匹配", createdOrder.getOrderNumber(), foundOrder.getOrderNumber());
  }

  @Test
  public void testFindByIdWithNullId() {
    // 测试使用空ID查找订单
    Order foundOrder = orderService.findById(null);
    assertNull("空ID应该返回null", foundOrder);
  }

  @Test
  public void testFindByOrderNumber() {
    // 测试根据订单号查找订单
    Order createdOrder = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));

    Order foundOrder = orderService.findByOrderNumber(createdOrder.getOrderNumber());
    assertNotNull("应该能找到订单", foundOrder);
    assertEquals("订单号应该匹配", createdOrder.getOrderNumber(), foundOrder.getOrderNumber());
  }

  @Test
  public void testFindByUserId() {
    // 测试根据用户ID查找订单列表
    Order order1 = orderService.createOrder(testUser.getId(), "商品1", 1, new BigDecimal("50.00"));
    Order order2 = orderService.createOrder(testUser.getId(), "商品2", 2, new BigDecimal("30.00"));

    List<Order> userOrders = orderService.findByUserId(testUser.getId());
    assertNotNull("订单列表不应为空", userOrders);
    assertEquals("应该有2个订单", 2, userOrders.size());

    // 验证订单属于正确的用户
    for (Order order : userOrders) {
      assertEquals("订单应该属于测试用户", testUser.getId(), order.getUserId());
    }
  }

  @Test
  public void testFindAll() {
    // 测试查找所有订单
    int initialCount = orderService.findAll().size();

    orderService.createOrder(testUser.getId(), "商品1", 1, new BigDecimal("50.00"));
    orderService.createOrder(testUser.getId(), "商品2", 2, new BigDecimal("30.00"));

    List<Order> allOrders = orderService.findAll();
    assertNotNull("订单列表不应为空", allOrders);
    assertEquals("订单数量应该增加2", initialCount + 2, allOrders.size());
  }

  @Test
  public void testConfirmOrder() {
    // 测试确认订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));

    boolean confirmed = orderService.confirmOrder(order.getId());
    assertTrue("确认订单应该成功", confirmed);
    assertEquals("订单状态应该是已确认", OrderStatus.CONFIRMED, order.getStatus());
  }

  @Test
  public void testConfirmOrderWithNonExistentOrder() {
    // 测试确认不存在的订单
    boolean confirmed = orderService.confirmOrder(999L);
    assertFalse("确认不存在的订单应该失败", confirmed);
  }

  @Test
  public void testConfirmOrderWithWrongStatus() {
    // 测试确认状态不正确的订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));
    orderService.confirmOrder(order.getId()); // 先确认一次

    boolean confirmed = orderService.confirmOrder(order.getId()); // 再次确认
    assertFalse("重复确认订单应该失败", confirmed);
  }

  @Test
  public void testProcessOrder() {
    // 测试处理订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));
    orderService.confirmOrder(order.getId());

    boolean processed = orderService.processOrder(order.getId());
    assertTrue("处理订单应该成功", processed);
    assertEquals("订单状态应该是处理中", OrderStatus.PROCESSING, order.getStatus());
  }

  @Test
  public void testProcessOrderWithWrongStatus() {
    // 测试处理状态不正确的订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));

    boolean processed = orderService.processOrder(order.getId()); // 未确认就处理
    assertFalse("处理未确认的订单应该失败", processed);
  }

  @Test
  public void testShipOrder() {
    // 测试发货订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));
    orderService.confirmOrder(order.getId());
    orderService.processOrder(order.getId());

    boolean shipped = orderService.shipOrder(order.getId());
    assertTrue("发货订单应该成功", shipped);
    assertEquals("订单状态应该是已发货", OrderStatus.SHIPPED, order.getStatus());
  }

  @Test
  public void testCompleteOrder() {
    // 测试完成订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));
    orderService.confirmOrder(order.getId());
    orderService.processOrder(order.getId());
    orderService.shipOrder(order.getId());

    boolean completed = orderService.completeOrder(order.getId());
    assertTrue("完成订单应该成功", completed);
    assertEquals("订单状态应该是已完成", OrderStatus.COMPLETED, order.getStatus());
  }

  @Test
  public void testCancelOrder() {
    // 测试取消订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));

    boolean cancelled = orderService.cancelOrder(order.getId());
    assertTrue("取消订单应该成功", cancelled);
    assertEquals("订单状态应该是已取消", OrderStatus.CANCELLED, order.getStatus());
  }

  @Test
  public void testCancelConfirmedOrder() {
    // 测试取消已确认的订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));
    orderService.confirmOrder(order.getId());

    boolean cancelled = orderService.cancelOrder(order.getId());
    assertTrue("取消已确认的订单应该成功", cancelled);
    assertEquals("订单状态应该是已取消", OrderStatus.CANCELLED, order.getStatus());
  }

  @Test
  public void testCancelOrderWithWrongStatus() {
    // 测试取消状态不允许的订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("50.00"));
    orderService.confirmOrder(order.getId());
    orderService.processOrder(order.getId());

    boolean cancelled = orderService.cancelOrder(order.getId()); // 处理中的订单不能取消
    assertFalse("取消处理中的订单应该失败", cancelled);
  }

  @Test
  public void testCalculateStatistics() {
    // 测试计算订单统计信息
    // 创建不同状态的订单
    Order order1 = orderService.createOrder(testUser.getId(), "商品1", 1, new BigDecimal("100.00"));
    Order order2 = orderService.createOrder(testUser.getId(), "商品2", 2, new BigDecimal("50.00"));
    Order order3 = orderService.createOrder(testUser.getId(), "商品3", 1, new BigDecimal("200.00"));

    // 设置不同状态
    orderService.confirmOrder(order2.getId());
    orderService.confirmOrder(order3.getId());
    orderService.processOrder(order3.getId());
    orderService.shipOrder(order3.getId());
    orderService.completeOrder(order3.getId());

    OrderService.OrderStatistics statistics = orderService.calculateStatistics();

    assertNotNull("统计信息不应为空", statistics);
    assertEquals("总订单数应该正确", 3, statistics.getTotalOrders());
    assertEquals("待处理订单数应该正确", 1, statistics.getPendingOrders());
    assertEquals("已确认订单数应该正确", 1, statistics.getConfirmedOrders());
    assertEquals("已完成订单数应该正确", 1, statistics.getCompletedOrders());
    assertEquals("总金额应该正确", new BigDecimal("200.00"), statistics.getTotalAmount());
  }

  @Test
  public void testOrderWorkflow() {
    // 测试完整的订单工作流程
    Order order = orderService.createOrder(testUser.getId(), "工作流测试商品", 3, new BigDecimal("66.66"));

    // 验证初始状态
    assertEquals("初始状态应该是待处理", OrderStatus.PENDING, order.getStatus());

    // 确认订单
    assertTrue("确认订单应该成功", orderService.confirmOrder(order.getId()));
    assertEquals("状态应该是已确认", OrderStatus.CONFIRMED, order.getStatus());

    // 处理订单
    assertTrue("处理订单应该成功", orderService.processOrder(order.getId()));
    assertEquals("状态应该是处理中", OrderStatus.PROCESSING, order.getStatus());

    // 发货订单
    assertTrue("发货订单应该成功", orderService.shipOrder(order.getId()));
    assertEquals("状态应该是已发货", OrderStatus.SHIPPED, order.getStatus());

    // 完成订单
    assertTrue("完成订单应该成功", orderService.completeOrder(order.getId()));
    assertEquals("状态应该是已完成", OrderStatus.COMPLETED, order.getStatus());

    // 验证总金额计算
    assertEquals("总金额应该正确", new BigDecimal("199.98"), order.getTotalAmount());
  }
}
