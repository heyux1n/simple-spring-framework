package com.simplespring.example.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体类
 * 演示业务实体之间的关联关系
 */
public class Order {
  private Long id;
  private String orderNumber;
  private Long userId;
  private String productName;
  private Integer quantity;
  private BigDecimal price;
  private BigDecimal totalAmount;
  private OrderStatus status;
  private Date createTime;
  private Date updateTime;

  public Order() {
    this.createTime = new Date();
    this.updateTime = new Date();
    this.status = OrderStatus.PENDING;
  }

  public Order(String orderNumber, Long userId, String productName, Integer quantity, BigDecimal price) {
    this();
    this.orderNumber = orderNumber;
    this.userId = userId;
    this.productName = productName;
    this.quantity = quantity;
    this.price = price;
    this.totalAmount = price.multiply(new BigDecimal(quantity));
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
    if (this.price != null) {
      this.totalAmount = this.price.multiply(new BigDecimal(quantity));
    }
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
    if (this.quantity != null) {
      this.totalAmount = price.multiply(new BigDecimal(this.quantity));
    }
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
    this.updateTime = new Date();
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  /**
   * 计算订单总金额
   */
  public void calculateTotalAmount() {
    if (this.price != null && this.quantity != null) {
      this.totalAmount = this.price.multiply(new BigDecimal(this.quantity));
    }
  }

  @Override
  public String toString() {
    return "Order{" +
        "id=" + id +
        ", orderNumber='" + orderNumber + '\'' +
        ", userId=" + userId +
        ", productName='" + productName + '\'' +
        ", quantity=" + quantity +
        ", price=" + price +
        ", totalAmount=" + totalAmount +
        ", status=" + status +
        ", createTime=" + createTime +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Order order = (Order) o;

    if (id != null ? !id.equals(order.id) : order.id != null)
      return false;
    return orderNumber != null ? orderNumber.equals(order.orderNumber) : order.orderNumber == null;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (orderNumber != null ? orderNumber.hashCode() : 0);
    return result;
  }
}
