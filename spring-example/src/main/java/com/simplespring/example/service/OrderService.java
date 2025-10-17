package com.simplespring.example.service;

import com.simplespring.core.annotation.Autowired;
import com.simplespring.core.annotation.Component;
import com.simplespring.example.entity.Order;
import com.simplespring.example.entity.OrderStatus;
import com.simplespring.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单服务类
 * 演示 AOP 切面的应用，包含业务逻辑处理
 */
@Component
public class OrderService {

  private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

  @Autowired
  private UserService userService;

  // 使用内存存储模拟数据库
  private final Map<Long, Order> orderStorage = new ConcurrentHashMap<Long, Order>();
  private final AtomicLong idGenerator = new AtomicLong(1);
  private final AtomicLong orderNumberGenerator = new AtomicLong(1000);

  public OrderService() {
    logger.info("OrderService 初始化完成");
  }

  /**
   * 创建订单
   * 此方法将被 AOP 切面拦截，用于演示切面功能
   */
  public Order createOrder(Long userId, String productName, Integer quantity, BigDecimal price) {
    logger.info("开始创建订单 - 用户ID: {}, 商品: {}, 数量: {}, 价格: {}",
        userId, productName, quantity, price);

    // 验证参数
    if (userId == null || productName == null || quantity == null || price == null) {
      throw new IllegalArgumentException("订单参数不能为空");
    }

    if (quantity <= 0) {
      throw new IllegalArgumentException("商品数量必须大于0");
    }

    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("商品价格必须大于0");
    }

    // 验证用户是否存在
    User user = userService.findById(userId);
    if (user == null) {
      throw new IllegalArgumentException("用户不存在: " + userId);
    }

    if (!user.isActive()) {
      throw new IllegalArgumentException("用户已被禁用，无法创建订单: " + userId);
    }

    // 生成订单号
    String orderNumber = "ORD" + System.currentTimeMillis() + orderNumberGenerator.getAndIncrement();

    // 创建订单
    Order order = new Order(orderNumber, userId, productName, quantity, price);
    order.setId(idGenerator.getAndIncrement());

    // 保存订单
    orderStorage.put(order.getId(), order);

    logger.info("订单创建成功 - 订单号: {}, 总金额: {}", order.getOrderNumber(), order.getTotalAmount());
    return order;
  }

  /**
   * 根据ID查找订单
   */
  public Order findById(Long id) {
    if (id == null) {
      return null;
    }

    Order order = orderStorage.get(id);
    logger.debug("根据ID查找订单: {} -> {}", id, order != null ? order.getOrderNumber() : "未找到");
    return order;
  }

  /**
   * 根据订单号查找订单
   */
  public Order findByOrderNumber(String orderNumber) {
    if (orderNumber == null || orderNumber.trim().isEmpty()) {
      return null;
    }

    for (Order order : orderStorage.values()) {
      if (orderNumber.equals(order.getOrderNumber())) {
        logger.debug("根据订单号查找订单: {} -> 找到", orderNumber);
        return order;
      }
    }

    logger.debug("根据订单号查找订单: {} -> 未找到", orderNumber);
    return null;
  }

  /**
   * 根据用户ID查找订单列表
   */
  public List<Order> findByUserId(Long userId) {
    List<Order> userOrders = new ArrayList<Order>();

    if (userId != null) {
      for (Order order : orderStorage.values()) {
        if (userId.equals(order.getUserId())) {
          userOrders.add(order);
        }
      }
    }

    logger.debug("根据用户ID查找订单: {} -> {} 个订单", userId, userOrders.size());
    return userOrders;
  }

  /**
   * 获取所有订单
   */
  public List<Order> findAll() {
    List<Order> orders = new ArrayList<Order>(orderStorage.values());
    logger.debug("查找所有订单，共 {} 个", orders.size());
    return orders;
  }

  /**
   * 确认订单
   * 此方法将被 AOP 切面拦截，用于演示切面功能
   */
  public boolean confirmOrder(Long orderId) {
    logger.info("开始确认订单: {}", orderId);

    Order order = findById(orderId);
    if (order == null) {
      logger.warn("确认订单失败，订单不存在: {}", orderId);
      return false;
    }

    if (order.getStatus() != OrderStatus.PENDING) {
      logger.warn("确认订单失败，订单状态不正确: {} - {}", orderId, order.getStatus());
      return false;
    }

    order.setStatus(OrderStatus.CONFIRMED);
    logger.info("订单确认成功: {}", order.getOrderNumber());
    return true;
  }

  /**
   * 处理订单
   * 此方法将被 AOP 切面拦截，用于演示切面功能
   */
  public boolean processOrder(Long orderId) {
    logger.info("开始处理订单: {}", orderId);

    Order order = findById(orderId);
    if (order == null) {
      logger.warn("处理订单失败，订单不存在: {}", orderId);
      return false;
    }

    if (order.getStatus() != OrderStatus.CONFIRMED) {
      logger.warn("处理订单失败，订单状态不正确: {} - {}", orderId, order.getStatus());
      return false;
    }

    // 模拟处理时间
    try {
      Thread.sleep(100); // 模拟处理耗时
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    order.setStatus(OrderStatus.PROCESSING);
    logger.info("订单处理成功: {}", order.getOrderNumber());
    return true;
  }

  /**
   * 发货订单
   * 此方法将被 AOP 切面拦截，用于演示切面功能
   */
  public boolean shipOrder(Long orderId) {
    logger.info("开始发货订单: {}", orderId);

    Order order = findById(orderId);
    if (order == null) {
      logger.warn("发货订单失败，订单不存在: {}", orderId);
      return false;
    }

    if (order.getStatus() != OrderStatus.PROCESSING) {
      logger.warn("发货订单失败，订单状态不正确: {} - {}", orderId, order.getStatus());
      return false;
    }

    order.setStatus(OrderStatus.SHIPPED);
    logger.info("订单发货成功: {}", order.getOrderNumber());
    return true;
  }

  /**
   * 完成订单
   * 此方法将被 AOP 切面拦截，用于演示切面功能
   */
  public boolean completeOrder(Long orderId) {
    logger.info("开始完成订单: {}", orderId);

    Order order = findById(orderId);
    if (order == null) {
      logger.warn("完成订单失败，订单不存在: {}", orderId);
      return false;
    }

    if (order.getStatus() != OrderStatus.SHIPPED) {
      logger.warn("完成订单失败，订单状态不正确: {} - {}", orderId, order.getStatus());
      return false;
    }

    order.setStatus(OrderStatus.COMPLETED);
    logger.info("订单完成: {}", order.getOrderNumber());
    return true;
  }

  /**
   * 取消订单
   * 此方法将被 AOP 切面拦截，用于演示切面功能
   */
  public boolean cancelOrder(Long orderId) {
    logger.info("开始取消订单: {}", orderId);

    Order order = findById(orderId);
    if (order == null) {
      logger.warn("取消订单失败，订单不存在: {}", orderId);
      return false;
    }

    // 只有待处理和已确认的订单可以取消
    if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
      logger.warn("取消订单失败，订单状态不允许取消: {} - {}", orderId, order.getStatus());
      return false;
    }

    order.setStatus(OrderStatus.CANCELLED);
    logger.info("订单取消成功: {}", order.getOrderNumber());
    return true;
  }

  /**
   * 计算订单统计信息
   * 此方法将被 AOP 切面拦截，用于演示性能监控
   */
  public OrderStatistics calculateStatistics() {
    logger.info("开始计算订单统计信息");

    int totalOrders = orderStorage.size();
    int pendingOrders = 0;
    int confirmedOrders = 0;
    int processingOrders = 0;
    int shippedOrders = 0;
    int completedOrders = 0;
    int cancelledOrders = 0;
    BigDecimal totalAmount = BigDecimal.ZERO;

    for (Order order : orderStorage.values()) {
      switch (order.getStatus()) {
        case PENDING:
          pendingOrders++;
          break;
        case CONFIRMED:
          confirmedOrders++;
          break;
        case PROCESSING:
          processingOrders++;
          break;
        case SHIPPED:
          shippedOrders++;
          break;
        case COMPLETED:
          completedOrders++;
          totalAmount = totalAmount.add(order.getTotalAmount());
          break;
        case CANCELLED:
          cancelledOrders++;
          break;
      }
    }

    OrderStatistics statistics = new OrderStatistics();
    statistics.setTotalOrders(totalOrders);
    statistics.setPendingOrders(pendingOrders);
    statistics.setConfirmedOrders(confirmedOrders);
    statistics.setProcessingOrders(processingOrders);
    statistics.setShippedOrders(shippedOrders);
    statistics.setCompletedOrders(completedOrders);
    statistics.setCancelledOrders(cancelledOrders);
    statistics.setTotalAmount(totalAmount);

    logger.info("订单统计信息计算完成 - 总订单数: {}, 总金额: {}", totalOrders, totalAmount);
    return statistics;
  }

  /**
   * 订单统计信息内部类
   */
  public static class OrderStatistics {
    private int totalOrders;
    private int pendingOrders;
    private int confirmedOrders;
    private int processingOrders;
    private int shippedOrders;
    private int completedOrders;
    private int cancelledOrders;
    private BigDecimal totalAmount;

    // Getters and Setters
    public int getTotalOrders() {
      return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
      this.totalOrders = totalOrders;
    }

    public int getPendingOrders() {
      return pendingOrders;
    }

    public void setPendingOrders(int pendingOrders) {
      this.pendingOrders = pendingOrders;
    }

    public int getConfirmedOrders() {
      return confirmedOrders;
    }

    public void setConfirmedOrders(int confirmedOrders) {
      this.confirmedOrders = confirmedOrders;
    }

    public int getProcessingOrders() {
      return processingOrders;
    }

    public void setProcessingOrders(int processingOrders) {
      this.processingOrders = processingOrders;
    }

    public int getShippedOrders() {
      return shippedOrders;
    }

    public void setShippedOrders(int shippedOrders) {
      this.shippedOrders = shippedOrders;
    }

    public int getCompletedOrders() {
      return completedOrders;
    }

    public void setCompletedOrders(int completedOrders) {
      this.completedOrders = completedOrders;
    }

    public int getCancelledOrders() {
      return cancelledOrders;
    }

    public void setCancelledOrders(int cancelledOrders) {
      this.cancelledOrders = cancelledOrders;
    }

    public BigDecimal getTotalAmount() {
      return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
      this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
      return "OrderStatistics{" +
          "totalOrders=" + totalOrders +
          ", pendingOrders=" + pendingOrders +
          ", confirmedOrders=" + confirmedOrders +
          ", processingOrders=" + processingOrders +
          ", shippedOrders=" + shippedOrders +
          ", completedOrders=" + completedOrders +
          ", cancelledOrders=" + cancelledOrders +
          ", totalAmount=" + totalAmount +
          '}';
    }
  }
}
