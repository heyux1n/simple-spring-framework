package com.simplespring.aop;

import java.lang.reflect.Method;

/**
 * 通知定义类
 * 存储通知方法的详细信息，包括通知类型、切点表达式、方法等
 * 
 * @author SimpleSpring
 */
public class AdviceDefinition {

  /**
   * 通知方法
   */
  private Method adviceMethod;

  /**
   * 通知类型
   */
  private AdviceType type;

  /**
   * 切点表达式
   */
  private String pointcutExpression;

  /**
   * 返回值参数名（用于 @AfterReturning 注解）
   */
  private String returningParameter;

  /**
   * 异常参数名（用于 @AfterThrowing 注解）
   */
  private String throwingParameter;

  /**
   * 通知方法所属的切面实例
   */
  private Object aspectInstance;

  /**
   * 构造函数
   */
  public AdviceDefinition() {
  }

  /**
   * 构造函数
   * 
   * @param adviceMethod       通知方法
   * @param type               通知类型
   * @param pointcutExpression 切点表达式
   * @param aspectInstance     切面实例
   */
  public AdviceDefinition(Method adviceMethod, AdviceType type,
      String pointcutExpression, Object aspectInstance) {
    this.adviceMethod = adviceMethod;
    this.type = type;
    this.pointcutExpression = pointcutExpression;
    this.aspectInstance = aspectInstance;
  }

  // Getter 和 Setter 方法

  public Method getAdviceMethod() {
    return adviceMethod;
  }

  public void setAdviceMethod(Method adviceMethod) {
    this.adviceMethod = adviceMethod;
  }

  public AdviceType getType() {
    return type;
  }

  public void setType(AdviceType type) {
    this.type = type;
  }

  public String getPointcutExpression() {
    return pointcutExpression;
  }

  public void setPointcutExpression(String pointcutExpression) {
    this.pointcutExpression = pointcutExpression;
  }

  public String getReturningParameter() {
    return returningParameter;
  }

  public void setReturningParameter(String returningParameter) {
    this.returningParameter = returningParameter;
  }

  public String getThrowingParameter() {
    return throwingParameter;
  }

  public void setThrowingParameter(String throwingParameter) {
    this.throwingParameter = throwingParameter;
  }

  public Object getAspectInstance() {
    return aspectInstance;
  }

  public void setAspectInstance(Object aspectInstance) {
    this.aspectInstance = aspectInstance;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("AdviceDefinition{");
    sb.append("type=").append(type);
    sb.append(", pointcutExpression='").append(pointcutExpression).append('\'');
    sb.append(", adviceMethod=").append(adviceMethod != null ? adviceMethod.getName() : "null");
    sb.append('}');
    return sb.toString();
  }
}
