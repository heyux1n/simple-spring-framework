package com.simplespring.beans.factory;

import java.util.List;

/**
 * 循环依赖异常
 * 当检测到 Bean 之间存在循环依赖时抛出此异常
 * 
 * @author SimpleSpring Framework
 */
public class CircularDependencyException extends BeanCreationException {

  /**
   * 循环依赖路径
   */
  private final List<String> dependencyPath;

  /**
   * 构造函数
   * 
   * @param message        异常消息
   * @param dependencyPath 循环依赖路径
   */
  public CircularDependencyException(String message, List<String> dependencyPath) {
    super(message);
    this.dependencyPath = dependencyPath;
  }

  /**
   * 构造函数
   * 
   * @param beanName       Bean 名称
   * @param message        异常消息
   * @param dependencyPath 循环依赖路径
   */
  public CircularDependencyException(String beanName, String message, List<String> dependencyPath) {
    super(beanName, message);
    this.dependencyPath = dependencyPath;
  }

  /**
   * 构造函数
   * 
   * @param beanName       Bean 名称
   * @param message        异常消息
   * @param cause          原因异常
   * @param dependencyPath 循环依赖路径
   */
  public CircularDependencyException(String beanName, String message, Throwable cause, List<String> dependencyPath) {
    super(beanName, message, cause);
    this.dependencyPath = dependencyPath;
  }

  /**
   * 获取循环依赖路径
   * 
   * @return 循环依赖路径
   */
  public List<String> getDependencyPath() {
    return dependencyPath;
  }

  /**
   * 获取格式化的循环依赖路径字符串
   * 
   * @return 格式化的循环依赖路径
   */
  public String getFormattedDependencyPath() {
    if (dependencyPath == null || dependencyPath.isEmpty()) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < dependencyPath.size(); i++) {
      if (i > 0) {
        sb.append(" -> ");
      }
      sb.append(dependencyPath.get(i));
    }
    return sb.toString();
  }

  @Override
  public String getMessage() {
    String baseMessage = super.getMessage();
    String pathString = getFormattedDependencyPath();

    if (pathString.length() > 0) {
      return baseMessage + " 循环依赖路径: " + pathString;
    }

    return baseMessage;
  }
}
