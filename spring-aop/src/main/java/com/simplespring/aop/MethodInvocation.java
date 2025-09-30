package com.simplespring.aop;

import java.lang.reflect.Method;

/**
 * 方法调用类
 * 封装方法调用的上下文信息，实现 JoinPoint 接口
 * 
 * @author SimpleSpring
 */
public class MethodInvocation implements JoinPoint {

  /**
   * 目标对象
   */
  private final Object target;

  /**
   * 目标方法
   */
  private final Method method;

  /**
   * 方法参数
   */
  private final Object[] args;

  /**
   * 目标类
   */
  private final Class<?> targetClass;

  /**
   * 方法返回值
   */
  private Object returnValue;

  /**
   * 方法执行异常
   */
  private Throwable exception;

  /**
   * 构造函数
   * 
   * @param target 目标对象
   * @param method 目标方法
   * @param args   方法参数
   */
  public MethodInvocation(Object target, Method method, Object[] args) {
    this.target = target;
    this.method = method;
    this.args = args != null ? args.clone() : new Object[0];
    this.targetClass = target != null ? target.getClass() : method.getDeclaringClass();
  }

  /**
   * 构造函数
   * 
   * @param target      目标对象
   * @param method      目标方法
   * @param args        方法参数
   * @param targetClass 目标类
   */
  public MethodInvocation(Object target, Method method, Object[] args, Class<?> targetClass) {
    this.target = target;
    this.method = method;
    this.args = args != null ? args.clone() : new Object[0];
    this.targetClass = targetClass != null ? targetClass
        : (target != null ? target.getClass() : method.getDeclaringClass());
  }

  @Override
  public Object getTarget() {
    return target;
  }

  @Override
  public Method getMethod() {
    return method;
  }

  @Override
  public Object[] getArgs() {
    return args.clone(); // 返回副本以防止外部修改
  }

  @Override
  public String getSignature() {
    StringBuilder signature = new StringBuilder();
    signature.append(method.getReturnType().getSimpleName()).append(" ");
    signature.append(targetClass.getSimpleName()).append(".");
    signature.append(method.getName()).append("(");

    Class<?>[] paramTypes = method.getParameterTypes();
    for (int i = 0; i < paramTypes.length; i++) {
      if (i > 0) {
        signature.append(", ");
      }
      signature.append(paramTypes[i].getSimpleName());
    }

    signature.append(")");
    return signature.toString();
  }

  @Override
  public Class<?> getTargetClass() {
    return targetClass;
  }

  @Override
  public JoinPointType getJoinPointType() {
    return JoinPointType.METHOD_EXECUTION;
  }

  /**
   * 获取方法返回值
   * 
   * @return 方法返回值
   */
  public Object getReturnValue() {
    return returnValue;
  }

  /**
   * 设置方法返回值
   * 
   * @param returnValue 方法返回值
   */
  public void setReturnValue(Object returnValue) {
    this.returnValue = returnValue;
  }

  /**
   * 获取方法执行异常
   * 
   * @return 方法执行异常
   */
  public Throwable getException() {
    return exception;
  }

  /**
   * 设置方法执行异常
   * 
   * @param exception 方法执行异常
   */
  public void setException(Throwable exception) {
    this.exception = exception;
  }

  /**
   * 执行目标方法
   * 
   * @return 方法返回值
   * @throws Throwable 方法执行异常
   */
  public Object proceed() throws Throwable {
    try {
      Object result = method.invoke(target, args);
      this.returnValue = result;
      return result;
    } catch (Exception e) {
      // 处理反射异常
      Throwable cause = e.getCause();
      if (cause != null) {
        this.exception = cause;
        throw cause;
      } else {
        this.exception = e;
        throw e;
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("MethodInvocation{");
    sb.append("signature='").append(getSignature()).append('\'');
    sb.append(", target=").append(target != null ? target.getClass().getSimpleName() : "null");
    sb.append(", argsCount=").append(args.length);
    sb.append('}');
    return sb.toString();
  }
}
