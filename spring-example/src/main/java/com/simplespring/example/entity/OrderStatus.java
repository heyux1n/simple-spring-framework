package com.simplespring.example.entity;

/**
 * 订单状态枚举
 * 定义订单的各种状态
 */
public enum OrderStatus {
  /**
   * 待处理
   */
  PENDING("待处理"),

  /**
   * 已确认
   */
  CONFIRMED("已确认"),

  /**
   * 处理中
   */
  PROCESSING("处理中"),

  /**
   * 已发货
   */
  SHIPPED("已发货"),

  /**
   * 已完成
   */
  COMPLETED("已完成"),

  /**
   * 已取消
   */
  CANCELLED("已取消");

  private final String description;

  OrderStatus(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return description;
  }
}
