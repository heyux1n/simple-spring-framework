package com.simplespring.beans.factory;

/**
 * Bean 类型不匹配异常
 * 当请求的 Bean 类型与实际类型不匹配时抛出此异常
 * 
 * @author SimpleSpring Framework
 */
public class BeanNotOfRequiredTypeException extends RuntimeException {

  /**
   * 序列化版本号
   */
  private static final long serialVersionUID = 1L;

  /**
   * Bean 名称
   */
  private final String beanName;

  /**
   * 期望的类型
   */
  private final Class<?> requiredType;

  /**
   * 实际的类型
   */
  private final Class<?> actualType;

  /**
   * 构造函数
   * 
   * @param beanName     Bean 名称
   * @param requiredType 期望的类型
   * @param actualType   实际的类型
   */
  public BeanNotOfRequiredTypeException(String beanName, Class<?> requiredType, Class<?> actualType) {
    super("Bean '" + beanName + "' 的类型不匹配: 期望类型为 '" +
        requiredType.getName() + "'，实际类型为 '" + actualType.getName() + "'");
    this.beanName = beanName;
    this.requiredType = requiredType;
    this.actualType = actualType;
  }

  /**
   * 获取 Bean 名称
   * 
   * @return Bean 名称
   */
  public String getBeanName() {
    return beanName;
  }

  /**
   * 获取期望的类型
   * 
   * @return 期望的类型
   */
  public Class<?> getRequiredType() {
    return requiredType;
  }

  /**
   * 获取实际的类型
   * 
   * @return 实际的类型
   */
  public Class<?> getActualType() {
    return actualType;
  }
}
