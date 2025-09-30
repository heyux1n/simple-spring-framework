package com.simplespring.beans.factory;

import java.util.Collection;

/**
 * 非唯一 Bean 定义异常
 * 当根据类型查找 Bean 时找到多个匹配的 Bean 定义时抛出此异常
 * 
 * @author SimpleSpring Framework
 */
public class NoUniqueBeanDefinitionException extends NoSuchBeanDefinitionException {

  /**
   * 序列化版本号
   */
  private static final long serialVersionUID = 1L;

  /**
   * 匹配的 Bean 名称数量
   */
  private final int numberOfBeansFound;

  /**
   * 匹配的 Bean 名称集合
   */
  private final Collection<String> beanNamesFound;

  /**
   * 构造函数
   * 
   * @param type               Bean 类型
   * @param numberOfBeansFound 找到的 Bean 数量
   * @param message            异常消息
   */
  public NoUniqueBeanDefinitionException(Class<?> type, int numberOfBeansFound, String message) {
    super(type, message);
    this.numberOfBeansFound = numberOfBeansFound;
    this.beanNamesFound = null;
  }

  /**
   * 构造函数
   * 
   * @param type           Bean 类型
   * @param beanNamesFound 找到的 Bean 名称集合
   */
  public NoUniqueBeanDefinitionException(Class<?> type, Collection<String> beanNamesFound) {
    super(type, "期望找到唯一的类型为 '" + type.getName() + "' 的 Bean，但找到了 " +
        beanNamesFound.size() + " 个: " + beanNamesFound);
    this.numberOfBeansFound = beanNamesFound.size();
    this.beanNamesFound = beanNamesFound;
  }

  /**
   * 构造函数
   * 
   * @param type           Bean 类型
   * @param beanNamesFound 找到的 Bean 名称集合
   * @param message        异常消息
   */
  public NoUniqueBeanDefinitionException(Class<?> type, Collection<String> beanNamesFound, String message) {
    super(type, message);
    this.numberOfBeansFound = beanNamesFound.size();
    this.beanNamesFound = beanNamesFound;
  }

  /**
   * 获取找到的 Bean 数量
   * 
   * @return Bean 数量
   */
  public int getNumberOfBeansFound() {
    return numberOfBeansFound;
  }

  /**
   * 获取找到的 Bean 名称集合
   * 
   * @return Bean 名称集合
   */
  public Collection<String> getBeanNamesFound() {
    return beanNamesFound;
  }
}
