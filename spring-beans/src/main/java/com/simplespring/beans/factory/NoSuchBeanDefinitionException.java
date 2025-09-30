package com.simplespring.beans.factory;

/**
 * 找不到 Bean 定义异常
 * 当请求的 Bean 定义不存在时抛出此异常
 * 
 * @author SimpleSpring Framework
 */
public class NoSuchBeanDefinitionException extends RuntimeException {

  /**
   * 序列化版本号
   */
  private static final long serialVersionUID = 1L;

  /**
   * Bean 名称
   */
  private final String beanName;

  /**
   * Bean 类型
   */
  private final Class<?> beanType;

  /**
   * 构造函数
   * 
   * @param beanName Bean 名称
   */
  public NoSuchBeanDefinitionException(String beanName) {
    super("找不到名为 '" + beanName + "' 的 Bean 定义");
    this.beanName = beanName;
    this.beanType = null;
  }

  /**
   * 构造函数
   * 
   * @param beanType Bean 类型
   */
  public NoSuchBeanDefinitionException(Class<?> beanType) {
    super("找不到类型为 '" + beanType.getName() + "' 的 Bean 定义");
    this.beanName = null;
    this.beanType = beanType;
  }

  /**
   * 构造函数
   * 
   * @param beanName Bean 名称
   * @param message  异常消息
   */
  public NoSuchBeanDefinitionException(String beanName, String message) {
    super(message);
    this.beanName = beanName;
    this.beanType = null;
  }

  /**
   * 构造函数
   * 
   * @param beanType Bean 类型
   * @param message  异常消息
   */
  public NoSuchBeanDefinitionException(Class<?> beanType, String message) {
    super(message);
    this.beanName = null;
    this.beanType = beanType;
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
   * 获取 Bean 类型
   * 
   * @return Bean 类型
   */
  public Class<?> getBeanType() {
    return beanType;
  }
}
