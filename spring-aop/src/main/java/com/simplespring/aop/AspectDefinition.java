package com.simplespring.aop;

import java.util.ArrayList;
import java.util.List;

/**
 * 切面定义类
 * 存储切面的元数据信息，包括切面实例、切面类和通知定义列表
 * 
 * @author SimpleSpring
 */
public class AspectDefinition {

  /**
   * 切面实例
   */
  private Object aspectInstance;

  /**
   * 切面类
   */
  private Class<?> aspectClass;

  /**
   * 通知定义列表
   */
  private List<AdviceDefinition> advices;

  /**
   * 切面名称
   */
  private String aspectName;

  /**
   * 切面优先级（数值越小优先级越高）
   */
  private int order;

  /**
   * 构造函数
   */
  public AspectDefinition() {
    this.advices = new ArrayList<AdviceDefinition>();
    this.order = Integer.MAX_VALUE; // 默认最低优先级
  }

  /**
   * 构造函数
   * 
   * @param aspectInstance 切面实例
   * @param aspectClass    切面类
   */
  public AspectDefinition(Object aspectInstance, Class<?> aspectClass) {
    this();
    this.aspectInstance = aspectInstance;
    this.aspectClass = aspectClass;
    this.aspectName = aspectClass.getSimpleName();
  }

  /**
   * 添加通知定义
   * 
   * @param adviceDefinition 通知定义
   */
  public void addAdvice(AdviceDefinition adviceDefinition) {
    if (adviceDefinition != null) {
      this.advices.add(adviceDefinition);
    }
  }

  /**
   * 移除通知定义
   * 
   * @param adviceDefinition 通知定义
   */
  public void removeAdvice(AdviceDefinition adviceDefinition) {
    if (adviceDefinition != null) {
      this.advices.remove(adviceDefinition);
    }
  }

  /**
   * 获取指定类型的通知定义列表
   * 
   * @param adviceType 通知类型
   * @return 通知定义列表
   */
  public List<AdviceDefinition> getAdvicesByType(AdviceType adviceType) {
    List<AdviceDefinition> result = new ArrayList<AdviceDefinition>();
    for (AdviceDefinition advice : advices) {
      if (advice.getType() == adviceType) {
        result.add(advice);
      }
    }
    return result;
  }

  /**
   * 检查是否有通知定义
   * 
   * @return 如果有通知定义返回 true，否则返回 false
   */
  public boolean hasAdvices() {
    return !advices.isEmpty();
  }

  // Getter 和 Setter 方法

  public Object getAspectInstance() {
    return aspectInstance;
  }

  public void setAspectInstance(Object aspectInstance) {
    this.aspectInstance = aspectInstance;
  }

  public Class<?> getAspectClass() {
    return aspectClass;
  }

  public void setAspectClass(Class<?> aspectClass) {
    this.aspectClass = aspectClass;
    if (aspectClass != null && this.aspectName == null) {
      this.aspectName = aspectClass.getSimpleName();
    }
  }

  public List<AdviceDefinition> getAdvices() {
    return new ArrayList<AdviceDefinition>(advices); // 返回副本以防止外部修改
  }

  public void setAdvices(List<AdviceDefinition> advices) {
    this.advices = advices != null ? new ArrayList<AdviceDefinition>(advices) : new ArrayList<AdviceDefinition>();
  }

  public String getAspectName() {
    return aspectName;
  }

  public void setAspectName(String aspectName) {
    this.aspectName = aspectName;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("AspectDefinition{");
    sb.append("aspectName='").append(aspectName).append('\'');
    sb.append(", aspectClass=").append(aspectClass != null ? aspectClass.getName() : "null");
    sb.append(", advicesCount=").append(advices.size());
    sb.append(", order=").append(order);
    sb.append('}');
    return sb.toString();
  }
}
