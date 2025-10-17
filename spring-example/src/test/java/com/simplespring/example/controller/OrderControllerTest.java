package com.simplespring.example.controller;

import com.simplespring.example.controller.OrderController;
import com.simplespring.example.entity.Order;
import com.simplespring.example.entity.User;
import com.simplespring.example.service.OrderService;
import com.simplespring.example.service.UserService;
import com.simplespring.example.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * 订单控制器测试
 * 验证订单相关 HTTP 请求的处理逻辑
 */
public class OrderControllerTest {

  private OrderController orderController;
  private OrderService orderService;
  private UserService userService;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private StringWriter responseWriter;
  private User testUser;

  @Before
  public void setUp() throws Exception {
    // 创建真实的服务
    userService = new UserServiceImpl();
    orderService = new OrderService();

    // 设置OrderService的依赖
    java.lang.reflect.Field userServiceField = OrderService.class.getDeclaredField("userService");
    userServiceField.setAccessible(true);
    userServiceField.set(orderService, userService);

    // 创建控制器并设置依赖
    orderController = new OrderController();
    java.lang.reflect.Field orderServiceField = OrderController.class.getDeclaredField("orderService");
    orderServiceField.setAccessible(true);
    orderServiceField.set(orderController, orderService);

    // 创建模拟的请求和响应对象
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);

    // 设置响应写入器
    responseWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(responseWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // 创建测试用户
    testUser = userService.createUser(new User("testuser", "test@example.com", "password123"));
  }

  @Test
  public void testGetAllOrders() throws Exception {
    // 先创建一些订单
    orderService.createOrder(testUser.getId(), "测试商品1", 1, new BigDecimal("100.00"));
    orderService.createOrder(testUser.getId(), "测试商品2", 2, new BigDecimal("50.00"));

    String result = orderController.getAllOrders(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含data字段", responseContent.contains("\"data\":["));
    assertTrue("响应应包含count字段", responseContent.contains("\"count\":"));
    assertTrue("响应应包含订单信息", responseContent.contains("测试商品"));

    // 验证响应头设置
    verify(response).setContentType("application/json;charset=UTF-8");
  }

  @Test
  public void testGetOrderById() throws Exception {
    // 先创建一个订单
    Order order = orderService.createOrder(testUser.getId(), "测试商品", 1, new BigDecimal("100.00"));

    // 模拟请求路径
    when(request.getPathInfo()).thenReturn("/orders/" + order.getId());

    String result = orderController.getOrderById(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含订单号", responseContent.contains(order.getOrderNumber()));
    assertTrue("响应应包含商品名称", responseContent.contains("测试商品"));
    assertTrue("响应应包含价格", responseContent.contains("100"));
  }

  @Test
  public void testGetOrderByIdNotFound() throws Exception {
    // 模拟请求不存在的订单ID
    when(request.getPathInfo()).thenReturn("/orders/999");

    String result = orderController.getOrderById(request, response);

    // 验证错误响应
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含错误信息", responseContent.contains("\"success\":false"));
    assertTrue("响应应包含错误消息", responseContent.contains("订单不存在"));

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testGetOrdersByUserId() throws Exception {
    // 先创建一些订单
    orderService.createOrder(testUser.getId(), "用户商品1", 1, new BigDecimal("100.00"));
    orderService.createOrder(testUser.getId(), "用户商品2", 2, new BigDecimal("50.00"));

    // 模拟请求路径
    when(request.getPathInfo()).thenReturn("/orders/user/" + testUser.getId());

    String result = orderController.getOrdersByUserId(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含data字段", responseContent.contains("\"data\":["));
    assertTrue("响应应包含用户的订单", responseContent.contains("用户商品"));
    assertTrue("响应应包含订单数量", responseContent.contains("\"count\":2"));
  }

  @Test
  public void testCreateOrder() throws Exception {
    // 模拟请求参数
    when(request.getParameter("userId")).thenReturn(testUser.getId().toString());
    when(request.getParameter("productName")).thenReturn("新订单商品");
    when(request.getParameter("quantity")).thenReturn("3");
    when(request.getParameter("price")).thenReturn("75.50");

    String result = orderController.createOrder(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含创建成功消息", responseContent.contains("订单创建成功"));
    assertTrue("响应应包含商品名称", responseContent.contains("新订单商品"));
    assertTrue("响应应包含数量", responseContent.contains("\"quantity\":3"));
    assertTrue("响应应包含总金额", responseContent.contains("226.5")); // 3 * 75.50

    // 验证订单确实被创建
    Order createdOrder = orderService.findByOrderNumber(responseContent.split("\"orderNumber\":\"")[1].split("\"")[0]);
    assertNotNull("订单应该被创建", createdOrder);
    assertEquals("商品名称应该匹配", "新订单商品", createdOrder.getProductName());
  }

  @Test
  public void testCreateOrderMissingParameters() throws Exception {
    // 模拟缺少参数的请求
    when(request.getParameter("userId")).thenReturn("");
    when(request.getParameter("productName")).thenReturn("商品");
    when(request.getParameter("quantity")).thenReturn("1");
    when(request.getParameter("price")).thenReturn("100.00");

    String result = orderController.createOrder(request, response);

    // 验证错误响应
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含错误信息", responseContent.contains("\"success\":false"));
    assertTrue("响应应包含错误消息", responseContent.contains("用户ID不能为空"));

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testCreateOrderInvalidParameters() throws Exception {
    // 模拟无效参数的请求
    when(request.getParameter("userId")).thenReturn("invalid");
    when(request.getParameter("productName")).thenReturn("商品");
    when(request.getParameter("quantity")).thenReturn("1");
    when(request.getParameter("price")).thenReturn("100.00");

    String result = orderController.createOrder(request, response);

    // 验证错误响应
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含错误信息", responseContent.contains("\"success\":false"));
    assertTrue("响应应包含参数格式错误消息", responseContent.contains("参数格式错误"));

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testConfirmOrder() throws Exception {
    // 先创建一个订单
    Order order = orderService.createOrder(testUser.getId(), "确认测试商品", 1, new BigDecimal("100.00"));

    // 模拟确认请求
    when(request.getPathInfo()).thenReturn("/orders/" + order.getId() + "/confirm");

    String result = orderController.confirmOrder(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含确认成功消息", responseContent.contains("订单确认成功"));

    // 验证订单状态确实被更新
    Order confirmedOrder = orderService.findById(order.getId());
    assertEquals("订单状态应该是已确认", "CONFIRMED", confirmedOrder.getStatus().toString());
  }

  @Test
  public void testProcessOrder() throws Exception {
    // 先创建并确认一个订单
    Order order = orderService.createOrder(testUser.getId(), "处理测试商品", 1, new BigDecimal("100.00"));
    orderService.confirmOrder(order.getId());

    // 模拟处理请求
    when(request.getPathInfo()).thenReturn("/orders/" + order.getId() + "/process");

    String result = orderController.processOrder(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含处理成功消息", responseContent.contains("订单处理成功"));

    // 验证订单状态确实被更新
    Order processedOrder = orderService.findById(order.getId());
    assertEquals("订单状态应该是处理中", "PROCESSING", processedOrder.getStatus().toString());
  }

  @Test
  public void testShipOrder() throws Exception {
    // 先创建、确认并处理一个订单
    Order order = orderService.createOrder(testUser.getId(), "发货测试商品", 1, new BigDecimal("100.00"));
    orderService.confirmOrder(order.getId());
    orderService.processOrder(order.getId());

    // 模拟发货请求
    when(request.getPathInfo()).thenReturn("/orders/" + order.getId() + "/ship");

    String result = orderController.shipOrder(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含发货成功消息", responseContent.contains("订单发货成功"));

    // 验证订单状态确实被更新
    Order shippedOrder = orderService.findById(order.getId());
    assertEquals("订单状态应该是已发货", "SHIPPED", shippedOrder.getStatus().toString());
  }

  @Test
  public void testCompleteOrder() throws Exception {
    // 先创建完整的订单流程
    Order order = orderService.createOrder(testUser.getId(), "完成测试商品", 1, new BigDecimal("100.00"));
    orderService.confirmOrder(order.getId());
    orderService.processOrder(order.getId());
    orderService.shipOrder(order.getId());

    // 模拟完成请求
    when(request.getPathInfo()).thenReturn("/orders/" + order.getId() + "/complete");

    String result = orderController.completeOrder(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含完成成功消息", responseContent.contains("订单完成成功"));

    // 验证订单状态确实被更新
    Order completedOrder = orderService.findById(order.getId());
    assertEquals("订单状态应该是已完成", "COMPLETED", completedOrder.getStatus().toString());
  }

  @Test
  public void testCancelOrder() throws Exception {
    // 先创建一个订单
    Order order = orderService.createOrder(testUser.getId(), "取消测试商品", 1, new BigDecimal("100.00"));

    // 模拟取消请求
    when(request.getPathInfo()).thenReturn("/orders/" + order.getId() + "/cancel");

    String result = orderController.cancelOrder(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含取消成功消息", responseContent.contains("订单取消成功"));

    // 验证订单状态确实被更新
    Order cancelledOrder = orderService.findById(order.getId());
    assertEquals("订单状态应该是已取消", "CANCELLED", cancelledOrder.getStatus().toString());
  }

  @Test
  public void testGetOrderStatistics() throws Exception {
    // 先创建一些不同状态的订单
    Order order1 = orderService.createOrder(testUser.getId(), "统计商品1", 1, new BigDecimal("100.00"));
    Order order2 = orderService.createOrder(testUser.getId(), "统计商品2", 2, new BigDecimal("50.00"));
    Order order3 = orderService.createOrder(testUser.getId(), "统计商品3", 1, new BigDecimal("200.00"));

    // 设置不同状态
    orderService.confirmOrder(order2.getId());
    orderService.confirmOrder(order3.getId());
    orderService.processOrder(order3.getId());
    orderService.shipOrder(order3.getId());
    orderService.completeOrder(order3.getId());

    String result = orderController.getOrderStatistics(request, response);

    // 验证返回值
    assertNull("应该直接写入响应，返回null", result);

    // 验证响应内容
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含success字段", responseContent.contains("\"success\":true"));
    assertTrue("响应应包含统计数据", responseContent.contains("\"totalOrders\":3"));
    assertTrue("响应应包含待处理订单数", responseContent.contains("\"pendingOrders\":1"));
    assertTrue("响应应包含已确认订单数", responseContent.contains("\"confirmedOrders\":1"));
    assertTrue("响应应包含已完成订单数", responseContent.contains("\"completedOrders\":1"));
    assertTrue("响应应包含总金额", responseContent.contains("\"totalAmount\":200"));
  }

  @Test
  public void testInvalidOrderIdInPath() throws Exception {
    // 模拟无效的订单ID
    when(request.getPathInfo()).thenReturn("/orders/invalid");

    String result = orderController.getOrderById(request, response);

    // 验证错误响应
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含错误信息", responseContent.contains("\"success\":false"));
    assertTrue("响应应包含无效ID错误", responseContent.contains("无效的订单ID"));

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testConfirmOrderWithWrongStatus() throws Exception {
    // 先创建并确认一个订单
    Order order = orderService.createOrder(testUser.getId(), "状态测试商品", 1, new BigDecimal("100.00"));
    orderService.confirmOrder(order.getId()); // 已经确认过了

    // 再次尝试确认
    when(request.getPathInfo()).thenReturn("/orders/" + order.getId() + "/confirm");

    String result = orderController.confirmOrder(request, response);

    // 验证错误响应
    String responseContent = responseWriter.toString();
    assertTrue("响应应包含错误信息", responseContent.contains("\"success\":false"));
    assertTrue("响应应包含状态错误消息", responseContent.contains("订单确认失败，请检查订单状态"));

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
