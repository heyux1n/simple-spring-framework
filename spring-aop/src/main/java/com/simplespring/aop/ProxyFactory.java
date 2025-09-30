package com.simplespring.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * 代理工厂类
 * 根据目标类选择合适的代理方式（JDK 动态代理或 CGLIB 代理）
 * 
 * @author SimpleSpring
 */
public class ProxyFactory {

  /**
   * 目标对象
   */
  private Object target;

  /**
   * 目标类
   */
  private Class<?> targetClass;

  /**
   * 方法拦截器列表
   */
  private List<MethodInterceptor> interceptors;

  /**
   * 切面定义列表
   */
  private List<AspectDefinition> aspectDefinitions;

  /**
   * 是否强制使用 CGLIB 代理
   */
  private boolean forceCglib;

  /**
   * 构造函数
   */
  public ProxyFactory() {
    this.interceptors = new ArrayList<MethodInterceptor>();
    this.aspectDefinitions = new ArrayList<AspectDefinition>();
    this.forceCglib = false;
  }

  /**
   * 构造函数
   * 
   * @param target 目标对象
   */
  public ProxyFactory(Object target) {
    this();
    this.target = target;
    this.targetClass = target.getClass();
  }

  /**
   * 构造函数
   * 
   * @param target      目标对象
   * @param targetClass 目标类
   */
  public ProxyFactory(Object target, Class<?> targetClass) {
    this();
    this.target = target;
    this.targetClass = targetClass;
  }

  /**
   * 创建代理对象
   * 
   * @return 代理对象
   */
  public Object createProxy() {
    if (target == null) {
      throw new IllegalStateException("目标对象不能为 null");
    }

    // 决定使用哪种代理方式
    if (shouldUseJdkProxy()) {
      return createJdkProxy();
    } else {
      return createCglibProxy();
    }
  }

  /**
   * 判断是否应该使用 JDK 动态代理
   * 
   * @return 如果应该使用 JDK 代理返回 true，否则返回 false
   */
  private boolean shouldUseJdkProxy() {
    if (forceCglib) {
      return false;
    }

    // 如果目标类实现了接口，使用 JDK 动态代理
    Class<?>[] interfaces = targetClass.getInterfaces();
    return interfaces.length > 0;
  }

  /**
   * 创建 JDK 动态代理
   * 
   * @return 代理对象
   */
  private Object createJdkProxy() {
    Class<?>[] interfaces = targetClass.getInterfaces();
    if (interfaces.length == 0) {
      throw new IllegalStateException("目标类没有实现接口，无法使用 JDK 动态代理");
    }

    InvocationHandler handler = new JdkProxyInvocationHandler(target, targetClass,
        interceptors, aspectDefinitions);

    return Proxy.newProxyInstance(
        targetClass.getClassLoader(),
        interfaces,
        handler);
  }

  /**
   * 创建 CGLIB 代理
   * 注意：这里只是一个占位实现，实际项目中需要集成 CGLIB 库
   * 
   * @return 代理对象
   */
  private Object createCglibProxy() {
    // 在简易实现中，我们暂时不集成 CGLIB
    // 如果目标类没有实现接口，我们抛出异常提示
    throw new UnsupportedOperationException(
        "CGLIB 代理暂未实现。请确保目标类实现了接口以使用 JDK 动态代理，" +
            "或者在实际项目中集成 CGLIB 库。目标类: " + targetClass.getName());
  }

  /**
   * 添加方法拦截器
   * 
   * @param interceptor 方法拦截器
   */
  public void addInterceptor(MethodInterceptor interceptor) {
    if (interceptor != null) {
      this.interceptors.add(interceptor);
    }
  }

  /**
   * 添加切面定义
   * 
   * @param aspectDefinition 切面定义
   */
  public void addAspectDefinition(AspectDefinition aspectDefinition) {
    if (aspectDefinition != null) {
      this.aspectDefinitions.add(aspectDefinition);
    }
  }

  /**
   * 设置是否强制使用 CGLIB 代理
   * 
   * @param forceCglib 是否强制使用 CGLIB
   */
  public void setForceCglib(boolean forceCglib) {
    this.forceCglib = forceCglib;
  }

  // Getter 和 Setter 方法

  public Object getTarget() {
    return target;
  }

  public void setTarget(Object target) {
    this.target = target;
    if (target != null && this.targetClass == null) {
      this.targetClass = target.getClass();
    }
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  public void setTargetClass(Class<?> targetClass) {
    this.targetClass = targetClass;
  }

  public List<MethodInterceptor> getInterceptors() {
    return new ArrayList<MethodInterceptor>(interceptors);
  }

  public List<AspectDefinition> getAspectDefinitions() {
    return new ArrayList<AspectDefinition>(aspectDefinitions);
  }

  /**
   * JDK 动态代理的 InvocationHandler 实现
   */
  private static class JdkProxyInvocationHandler implements InvocationHandler {

    private final Object target;
    private final Class<?> targetClass;
    private final List<MethodInterceptor> interceptors;
    private final List<AspectDefinition> aspectDefinitions;

    public JdkProxyInvocationHandler(Object target, Class<?> targetClass,
        List<MethodInterceptor> interceptors,
        List<AspectDefinition> aspectDefinitions) {
      this.target = target;
      this.targetClass = targetClass;
      this.interceptors = new ArrayList<MethodInterceptor>(interceptors);
      this.aspectDefinitions = new ArrayList<AspectDefinition>(aspectDefinitions);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      // 创建方法调用对象
      MethodInvocation invocation = new MethodInvocation(target, method, args, targetClass);

      // 收集匹配的通知
      List<AdviceDefinition> matchingAdvices = collectMatchingAdvices(invocation);

      if (matchingAdvices.isEmpty() && interceptors.isEmpty()) {
        // 没有通知和拦截器，直接调用目标方法
        return invocation.proceed();
      }

      // 执行通知和拦截器
      return executeWithAdvices(invocation, matchingAdvices);
    }

    /**
     * 收集匹配的通知定义
     * 
     * @param invocation 方法调用
     * @return 匹配的通知定义列表
     */
    private List<AdviceDefinition> collectMatchingAdvices(MethodInvocation invocation) {
      List<AdviceDefinition> matchingAdvices = new ArrayList<AdviceDefinition>();

      for (AspectDefinition aspectDef : aspectDefinitions) {
        for (AdviceDefinition adviceDef : aspectDef.getAdvices()) {
          if (AdviceExecutor.matches(adviceDef, invocation)) {
            matchingAdvices.add(adviceDef);
          }
        }
      }

      return matchingAdvices;
    }

    /**
     * 执行带通知的方法调用
     * 
     * @param invocation 方法调用
     * @param advices    通知定义列表
     * @return 方法返回值
     * @throws Throwable 执行异常
     */
    private Object executeWithAdvices(MethodInvocation invocation,
        List<AdviceDefinition> advices) throws Throwable {
      Object result = null;
      Throwable exception = null;

      try {
        // 执行前置通知
        for (AdviceDefinition advice : advices) {
          if (advice.getType() == AdviceType.BEFORE) {
            AdviceExecutor.executeBefore(advice, invocation);
          }
        }

        // 执行拦截器
        if (!interceptors.isEmpty()) {
          result = executeInterceptors(invocation);
        } else {
          result = invocation.proceed();
        }

        invocation.setReturnValue(result);

        // 执行返回后通知
        for (AdviceDefinition advice : advices) {
          if (advice.getType() == AdviceType.AFTER_RETURNING) {
            AdviceExecutor.executeAfterReturning(advice, invocation, result);
          }
        }

      } catch (Throwable t) {
        exception = t;
        invocation.setException(t);

        // 执行异常通知
        for (AdviceDefinition advice : advices) {
          if (advice.getType() == AdviceType.AFTER_THROWING) {
            try {
              AdviceExecutor.executeAfterThrowing(advice, invocation, t);
            } catch (Throwable adviceException) {
              // 通知执行异常，记录但不影响原异常的抛出
              System.err.println("执行异常通知时发生错误: " + adviceException.getMessage());
            }
          }
        }

        throw t;
      } finally {
        // 执行后置通知
        for (AdviceDefinition advice : advices) {
          if (advice.getType() == AdviceType.AFTER) {
            try {
              AdviceExecutor.executeAfter(advice, invocation);
            } catch (Throwable adviceException) {
              // 通知执行异常，记录但不影响主流程
              System.err.println("执行后置通知时发生错误: " + adviceException.getMessage());
            }
          }
        }
      }

      return result;
    }

    /**
     * 执行拦截器链
     * 
     * @param invocation 方法调用
     * @return 方法返回值
     * @throws Throwable 执行异常
     */
    private Object executeInterceptors(MethodInvocation invocation) throws Throwable {
      if (interceptors.isEmpty()) {
        return invocation.proceed();
      }

      // 简单实现：依次执行所有拦截器
      Object result = null;
      for (MethodInterceptor interceptor : interceptors) {
        result = interceptor.intercept(invocation);
      }
      return result;
    }
  }
}
