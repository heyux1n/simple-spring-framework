package com.simplespring.webmvc;

import java.lang.reflect.Method;

/**
 * 处理器方法类，封装控制器方法的元数据
 * 
 * 该类包含了执行控制器方法所需的所有信息：
 * - 控制器实例
 * - 处理方法
 * - 方法参数类型
 * - 方法返回类型
 * 
 * 用于在请求处理过程中调用相应的控制器方法。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class HandlerMethod {

  /**
   * 控制器实例
   */
  private Object controller;

  /**
   * 处理方法
   */
  private Method method;

  /**
   * 方法参数类型数组
   */
  private Class<?>[] parameterTypes;

  /**
   * 方法返回类型
   */
  private Class<?> returnType;

  /**
   * 构造函数
   * 
   * @param controller 控制器实例
   * @param method     处理方法
   */
  public HandlerMethod(Object controller, Method method) {
    this.controller = controller;
    this.method = method;
    this.parameterTypes = method.getParameterTypes();
    this.returnType = method.getReturnType();
  }

  /**
   * 调用处理器方法
   * 
   * @param args 方法参数
   * @return 方法执行结果
   * @throws Exception 如果方法调用失败
   */
  public Object invoke(Object... args) throws Exception {
    try {
      // 确保方法可访问
      if (!method.isAccessible()) {
        method.setAccessible(true);
      }

      return method.invoke(controller, args);
    } catch (Exception e) {
      throw new RuntimeException("调用处理器方法失败: " + getMethodSignature(), e);
    }
  }

  /**
   * 获取方法签名字符串
   * 
   * @return 方法签名，格式为 "类名.方法名(参数类型...)"
   */
  public String getMethodSignature() {
    StringBuilder sb = new StringBuilder();
    sb.append(controller.getClass().getSimpleName());
    sb.append(".");
    sb.append(method.getName());
    sb.append("(");

    for (int i = 0; i < parameterTypes.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(parameterTypes[i].getSimpleName());
    }

    sb.append(")");
    return sb.toString();
  }

  /**
   * 检查方法是否可以处理指定数量的参数
   * 
   * @param argCount 参数数量
   * @return 如果可以处理返回 true，否则返回 false
   */
  public boolean canHandle(int argCount) {
    return parameterTypes.length == argCount;
  }

  /**
   * 检查指定位置的参数类型是否匹配
   * 
   * @param index   参数位置
   * @param argType 参数类型
   * @return 如果类型匹配返回 true，否则返回 false
   */
  public boolean isParameterTypeMatch(int index, Class<?> argType) {
    if (index < 0 || index >= parameterTypes.length) {
      return false;
    }

    Class<?> paramType = parameterTypes[index];

    // 精确匹配
    if (paramType.equals(argType)) {
      return true;
    }

    // 检查是否可以赋值（包括继承关系）
    return paramType.isAssignableFrom(argType);
  }

  // Getter 方法

  public Object getController() {
    return controller;
  }

  public Method getMethod() {
    return method;
  }

  public Class<?>[] getParameterTypes() {
    return parameterTypes;
  }

  public Class<?> getReturnType() {
    return returnType;
  }

  /**
   * 获取方法名
   * 
   * @return 方法名
   */
  public String getMethodName() {
    return method.getName();
  }

  /**
   * 获取控制器类名
   * 
   * @return 控制器类名
   */
  public String getControllerName() {
    return controller.getClass().getSimpleName();
  }

  @Override
  public String toString() {
    return "HandlerMethod{" +
        "controller=" + controller.getClass().getSimpleName() +
        ", method=" + getMethodSignature() +
        ", returnType=" + returnType.getSimpleName() +
        '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;

    HandlerMethod that = (HandlerMethod) obj;

    if (controller != null ? !controller.equals(that.controller) : that.controller != null)
      return false;
    return method != null ? method.equals(that.method) : that.method == null;
  }

  @Override
  public int hashCode() {
    int result = controller != null ? controller.hashCode() : 0;
    result = 31 * result + (method != null ? method.hashCode() : 0);
    return result;
  }
}
