package com.simplespring.aop;

/**
 * 方法拦截器接口
 * 用于拦截方法调用并执行通知
 * 
 * @author SimpleSpring
 */
public interface MethodInterceptor {

  /**
   * 拦截方法调用
   * 
   * @param invocation 方法调用信息
   * @return 方法返回值
   * @throws Throwable 方法执行异常
   */
  Object intercept(MethodInvocation invocation) throws Throwable;
}
