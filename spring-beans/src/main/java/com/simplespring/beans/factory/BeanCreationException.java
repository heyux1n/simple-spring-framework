package com.simplespring.beans.factory;

/**
 * Bean 创建异常
 * 当 Bean 创建过程中发生错误时抛出此异常
 * 
 * @author SimpleSpring Framework
 */
public class BeanCreationException extends RuntimeException {

  /**
   * 序列化版本号
   */
  private static final long serialVersionUID = 1L;

  /**
   * Bean 名称
   */
  private final String beanName;

  /**
   * 构造函数
   * 
   * @param beanName Bean 名称
   * @param message  异常消息
   */
  public BeanCreationException(String beanName, String message) {
    super("创建名为 '" + beanName + "' 的 Bean 时发生错误: " + message);
    this.beanName = beanName;
  }

  /**
   * 构造函数
   * 
   * @param beanName Bean 名称
   * @param message  异常消息
   * @param cause    异常原因
   */
  public BeanCreationException(String beanName, String message, Throwable cause) {
    super("创建名为 '" + beanName + "' 的 Bean 时发生错误: " + message, cause);
    this.beanName = beanName;
  }

  /**
   * 构造函数
   * 
   * @param beanName Bean 名称
   * @param cause    异常原因
   */
  public BeanCreationException(String beanName, Throwable cause) {
    super("创建名为 '" + beanName + "' 的 Bean 时发生错误", cause);
    this.beanName = beanName;
  }

  /**
   * 获取 Bean 名称
   * 
   * @return Bean 名称
   */
  public String getBeanName() {
    return beanName;
  }
}
