package com.simplespring.example.controller;

import com.simplespring.core.annotation.Autowired;
import com.simplespring.core.annotation.Controller;
import com.simplespring.core.annotation.RequestMapping;
import com.simplespring.core.annotation.RequestMethod;
import com.simplespring.example.entity.Order;
import com.simplespring.example.entity.OrderStatus;
import com.simplespring.example.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单控制器
 * 演示请求参数绑定和响应处理，提供订单相关的 HTTP 接口
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

  private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

  @Autowired
  private OrderService orderService;

  public OrderController() {
    logger.info("OrderController 初始化完成");
  }

  /**
   * 获取所有订单
   * GET /orders
   */
  @RequestMapping(method = RequestMethod.GET)
  public String getAllOrders(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理获取所有订单请求");

    try {
      List<Order> orders = orderService.findAll();

      // 构建JSON响应
      StringBuilder json = new StringBuilder();
      json.append("{\"success\":true,\"data\":[");

      for (int i = 0; i < orders.size(); i++) {
        if (i > 0) {
          json.append(",");
        }
        Order order = orders.get(i);
        json.append(buildOrderJson(order));
      }

      json.append("],\"count\":").append(orders.size()).append("}");

      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(json.toString());

      logger.info("成功返回 {} 个订单", orders.size());
      return null;

    } catch (Exception e) {
      logger.error("获取订单列表失败", e);
      return handleError(response, "获取订单列表失败: " + e.getMessage());
    }
  }

  /**
   * 根据ID获取订单
   * GET /orders/{id}
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public String getOrderById(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理根据ID获取订单请求");

    try {
      Long orderId = parseOrderIdFromPath(request.getPathInfo());
      if (orderId == null) {
        return handleError(response, "无效的订单ID");
      }

      Order order = orderService.findById(orderId);
      if (order == null) {
        return handleError(response, "订单不存在: " + orderId);
      }

      String json = "{\"success\":true,\"data\":" + buildOrderJson(order) + "}";

      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(json);

      logger.info("成功返回订单信息: {}", order.getOrderNumber());
      return null;

    } catch (Exception e) {
      logger.error("获取订单信息失败", e);
      return handleError(response, "获取订单信息失败: " + e.getMessage());
    }
  }

  /**
   * 根据用户ID获取订单列表
   * GET /orders/user/{userId}
   */
  @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
  public String getOrdersByUserId(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理根据用户ID获取订单请求");

    try {
      String pathInfo = request.getPathInfo();
      if (pathInfo == null || pathInfo.length() <= 1) {
        return handleError(response, "缺少用户ID参数");
      }

      String[] pathParts = pathInfo.split("/");
      if (pathParts.length < 4) {
        return handleError(response, "无效的请求路径");
      }

      Long userId;
      try {
        userId = Long.parseLong(pathParts[3]);
      } catch (NumberFormatException e) {
        return handleError(response, "无效的用户ID格式");
      }

      List<Order> orders = orderService.findByUserId(userId);

      // 构建JSON响应
      StringBuilder json = new StringBuilder();
      json.append("{\"success\":true,\"data\":[");

      for (int i = 0; i < orders.size(); i++) {
        if (i > 0) {
          json.append(",");
        }
        Order order = orders.get(i);
        json.append(buildOrderJson(order));
      }

      json.append("],\"count\":").append(orders.size()).append("}");

      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(json.toString());

      logger.info("成功返回用户 {} 的 {} 个订单", userId, orders.size());
      return null;

    } catch (Exception e) {
      logger.error("获取用户订单列表失败", e);
      return handleError(response, "获取用户订单列表失败: " + e.getMessage());
    }
  }

  /**
   * 创建订单
   * POST /orders
   */
  @RequestMapping(method = RequestMethod.POST)
  public String createOrder(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理创建订单请求");

    try {
      // 获取请求参数
      String userIdStr = request.getParameter("userId");
      String productName = request.getParameter("productName");
      String quantityStr = request.getParameter("quantity");
      String priceStr = request.getParameter("price");

      // 参数验证
      if (userIdStr == null || userIdStr.trim().isEmpty()) {
        return handleError(response, "用户ID不能为空");
      }
      if (productName == null || productName.trim().isEmpty()) {
        return handleError(response, "商品名称不能为空");
      }
      if (quantityStr == null || quantityStr.trim().isEmpty()) {
        return handleError(response, "商品数量不能为空");
      }
      if (priceStr == null || priceStr.trim().isEmpty()) {
        return handleError(response, "商品价格不能为空");
      }

      // 参数转换
      Long userId;
      Integer quantity;
      BigDecimal price;

      try {
        userId = Long.parseLong(userIdStr.trim());
        quantity = Integer.parseInt(quantityStr.trim());
        price = new BigDecimal(priceStr.trim());
      } catch (NumberFormatException e) {
        return handleError(response, "参数格式错误: " + e.getMessage());
      }

      // 创建订单
      Order order = orderService.createOrder(userId, productName.trim(), quantity, price);

      String json = "{\"success\":true,\"message\":\"订单创建成功\",\"data\":" + buildOrderJson(order) + "}";

      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(json);

      logger.info("成功创建订单: {}", order.getOrderNumber());
      return null;

    } catch (Exception e) {
      logger.error("创建订单失败", e);
      return handleError(response, "创建订单失败: " + e.getMessage());
    }
  }

  /**
   * 确认订单
   * PUT /orders/{id}/confirm
   */
  @RequestMapping(value = "/{id}/confirm", method = RequestMethod.PUT)
  public String confirmOrder(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理确认订单请求");

    try {
      Long orderId = parseOrderIdFromPath(request.getPathInfo());
      if (orderId == null) {
        return handleError(response, "无效的订单ID");
      }

      boolean confirmed = orderService.confirmOrder(orderId);

      if (confirmed) {
        String json = "{\"success\":true,\"message\":\"订单确认成功\"}";
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
        logger.info("成功确认订单: {}", orderId);
      } else {
        return handleError(response, "订单确认失败，请检查订单状态");
      }

      return null;

    } catch (Exception e) {
      logger.error("确认订单失败", e);
      return handleError(response, "确认订单失败: " + e.getMessage());
    }
  }

  /**
   * 处理订单
   * PUT /orders/{id}/process
   */
  @RequestMapping(value = "/{id}/process", method = RequestMethod.PUT)
  public String processOrder(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理订单处理请求");

    try {
      Long orderId = parseOrderIdFromPath(request.getPathInfo());
      if (orderId == null) {
        return handleError(response, "无效的订单ID");
      }

      boolean processed = orderService.processOrder(orderId);

      if (processed) {
        String json = "{\"success\":true,\"message\":\"订单处理成功\"}";
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
        logger.info("成功处理订单: {}", orderId);
      } else {
        return handleError(response, "订单处理失败，请检查订单状态");
      }

      return null;

    } catch (Exception e) {
      logger.error("处理订单失败", e);
      return handleError(response, "处理订单失败: " + e.getMessage());
    }
  }

  /**
   * 发货订单
   * PUT /orders/{id}/ship
   */
  @RequestMapping(value = "/{id}/ship", method = RequestMethod.PUT)
  public String shipOrder(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理发货订单请求");

    try {
      Long orderId = parseOrderIdFromPath(request.getPathInfo());
      if (orderId == null) {
        return handleError(response, "无效的订单ID");
      }

      boolean shipped = orderService.shipOrder(orderId);

      if (shipped) {
        String json = "{\"success\":true,\"message\":\"订单发货成功\"}";
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
        logger.info("成功发货订单: {}", orderId);
      } else {
        return handleError(response, "订单发货失败，请检查订单状态");
      }

      return null;

    } catch (Exception e) {
      logger.error("发货订单失败", e);
      return handleError(response, "发货订单失败: " + e.getMessage());
    }
  }

  /**
   * 完成订单
   * PUT /orders/{id}/complete
   */
  @RequestMapping(value = "/{id}/complete", method = RequestMethod.PUT)
  public String completeOrder(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理完成订单请求");

    try {
      Long orderId = parseOrderIdFromPath(request.getPathInfo());
      if (orderId == null) {
        return handleError(response, "无效的订单ID");
      }

      boolean completed = orderService.completeOrder(orderId);

      if (completed) {
        String json = "{\"success\":true,\"message\":\"订单完成成功\"}";
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
        logger.info("成功完成订单: {}", orderId);
      } else {
        return handleError(response, "订单完成失败，请检查订单状态");
      }

      return null;

    } catch (Exception e) {
      logger.error("完成订单失败", e);
      return handleError(response, "完成订单失败: " + e.getMessage());
    }
  }

  /**
   * 取消订单
   * PUT /orders/{id}/cancel
   */
  @RequestMapping(value = "/{id}/cancel", method = RequestMethod.PUT)
  public String cancelOrder(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理取消订单请求");

    try {
      Long orderId = parseOrderIdFromPath(request.getPathInfo());
      if (orderId == null) {
        return handleError(response, "无效的订单ID");
      }

      boolean cancelled = orderService.cancelOrder(orderId);

      if (cancelled) {
        String json = "{\"success\":true,\"message\":\"订单取消成功\"}";
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
        logger.info("成功取消订单: {}", orderId);
      } else {
        return handleError(response, "订单取消失败，请检查订单状态");
      }

      return null;

    } catch (Exception e) {
      logger.error("取消订单失败", e);
      return handleError(response, "取消订单失败: " + e.getMessage());
    }
  }

  /**
   * 获取订单统计信息
   * GET /orders/statistics
   */
  @RequestMapping(value = "/statistics", method = RequestMethod.GET)
  public String getOrderStatistics(HttpServletRequest request, HttpServletResponse response) {
    logger.info("处理获取订单统计请求");

    try {
      OrderService.OrderStatistics statistics = orderService.calculateStatistics();

      String json = "{\"success\":true,\"data\":{" +
          "\"totalOrders\":" + statistics.getTotalOrders() + "," +
          "\"pendingOrders\":" + statistics.getPendingOrders() + "," +
          "\"confirmedOrders\":" + statistics.getConfirmedOrders() + "," +
          "\"processingOrders\":" + statistics.getProcessingOrders() + "," +
          "\"shippedOrders\":" + statistics.getShippedOrders() + "," +
          "\"completedOrders\":" + statistics.getCompletedOrders() + "," +
          "\"cancelledOrders\":" + statistics.getCancelledOrders() + "," +
          "\"totalAmount\":" + statistics.getTotalAmount() +
          "}}";

      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(json);

      logger.info("成功返回订单统计信息");
      return null;

    } catch (Exception e) {
      logger.error("获取订单统计失败", e);
      return handleError(response, "获取订单统计失败: " + e.getMessage());
    }
  }

  /**
   * 从路径中解析订单ID
   */
  private Long parseOrderIdFromPath(String pathInfo) {
    if (pathInfo == null || pathInfo.length() <= 1) {
      return null;
    }

    String[] pathParts = pathInfo.split("/");
    if (pathParts.length < 3) {
      return null;
    }

    try {
      return Long.parseLong(pathParts[2]);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * 构建订单JSON字符串
   */
  private String buildOrderJson(Order order) {
    return "{" +
        "\"id\":" + order.getId() + "," +
        "\"orderNumber\":\"" + order.getOrderNumber() + "\"," +
        "\"userId\":" + order.getUserId() + "," +
        "\"productName\":\"" + order.getProductName() + "\"," +
        "\"quantity\":" + order.getQuantity() + "," +
        "\"price\":" + order.getPrice() + "," +
        "\"totalAmount\":" + order.getTotalAmount() + "," +
        "\"status\":\"" + order.getStatus() + "\"," +
        "\"createTime\":\"" + order.getCreateTime() + "\"," +
        "\"updateTime\":\"" + order.getUpdateTime() + "\"" +
        "}";
  }

  /**
   * 处理错误响应
   */
  private String handleError(HttpServletResponse response, String message) {
    try {
      String json = "{\"success\":false,\"error\":\"" + message + "\"}";
      response.setContentType("application/json;charset=UTF-8");
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write(json);
    } catch (IOException e) {
      logger.error("写入错误响应失败", e);
    }
    return null;
  }
}
