package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.BeanCreationException;
import com.simplespring.beans.factory.config.BeanPostProcessor;
import com.simplespring.core.annotation.PostConstruct;
import com.simplespring.core.annotation.PreDestroy;
import com.simplespring.core.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生命周期处理器
 * 
 * 负责处理 Bean 的生命周期注解，包括 @PostConstruct 和 @PreDestroy。
 * 实现了 BeanPostProcessor 接口，在 Bean 初始化前后执行相应的生命周期方法。
 * 
 * 功能特性：
 * - 扫描并缓存带有生命周期注解的方法
 * - 在 Bean 初始化后自动调用 @PostConstruct 方法
 * - 提供销毁前调用 @PreDestroy 方法的能力
 * - 支持方法访问权限处理
 * - 提供详细的错误信息和异常处理
 * 
 * @author SimpleSpring Framework
 */
public class LifecycleProcessor implements BeanPostProcessor {

  /**
   * 缓存每个类的 @PostConstruct 方法
   * Key: 类对象，Value: @PostConstruct 方法列表
   */
  private final Map<Class<?>, List<Method>> postConstructMethodsCache = new ConcurrentHashMap<Class<?>, List<Method>>();

  /**
   * 缓存每个类的 @PreDestroy 方法
   * Key: 类对象，Value: @PreDestroy 方法列表
   */
  private final Map<Class<?>, List<Method>> preDestroyMethodsCache = new ConcurrentHashMap<Class<?>, List<Method>>();

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    // 在初始化前不需要特殊处理
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    // 在初始化后调用 @PostConstruct 方法
    invokePostConstructMethods(bean, beanName);
    return bean;
  }

  /**
   * 调用 Bean 的 @PostConstruct 方法
   * 
   * @param bean     Bean 实例
   * @param beanName Bean 名称
   */
  public void invokePostConstructMethods(Object bean, String beanName) {
    Class<?> beanClass = bean.getClass();
    List<Method> postConstructMethods = getPostConstructMethods(beanClass);

    for (Method method : postConstructMethods) {
      try {
        ReflectionUtils.makeAccessible(method);
        method.invoke(bean);
      } catch (Exception e) {
        throw new BeanCreationException(beanName,
            "调用 @PostConstruct 方法 '" + method.getName() + "' 失败", e);
      }
    }
  }

  /**
   * 调用 Bean 的 @PreDestroy 方法
   * 
   * @param bean     Bean 实例
   * @param beanName Bean 名称
   */
  public void invokePreDestroyMethods(Object bean, String beanName) {
    Class<?> beanClass = bean.getClass();
    List<Method> preDestroyMethods = getPreDestroyMethods(beanClass);

    for (Method method : preDestroyMethods) {
      try {
        ReflectionUtils.makeAccessible(method);
        method.invoke(bean);
      } catch (Exception e) {
        // PreDestroy 方法执行失败不应该阻止应用程序关闭
        // 这里可以记录日志，但不抛出异常
        System.err.println("调用 @PreDestroy 方法 '" + method.getName() +
            "' 失败 (Bean: " + beanName + "): " + e.getMessage());
      }
    }
  }

  /**
   * 获取类的 @PostConstruct 方法
   * 
   * @param beanClass Bean 类
   * @return @PostConstruct 方法列表
   */
  private List<Method> getPostConstructMethods(Class<?> beanClass) {
    List<Method> methods = postConstructMethodsCache.get(beanClass);
    if (methods == null) {
      methods = scanLifecycleMethods(beanClass, PostConstruct.class);
      postConstructMethodsCache.put(beanClass, methods);
    }
    return methods;
  }

  /**
   * 获取类的 @PreDestroy 方法
   * 
   * @param beanClass Bean 类
   * @return @PreDestroy 方法列表
   */
  private List<Method> getPreDestroyMethods(Class<?> beanClass) {
    List<Method> methods = preDestroyMethodsCache.get(beanClass);
    if (methods == null) {
      methods = scanLifecycleMethods(beanClass, PreDestroy.class);
      preDestroyMethodsCache.put(beanClass, methods);
    }
    return methods;
  }

  /**
   * 扫描类中带有指定生命周期注解的方法
   * 
   * @param beanClass       Bean 类
   * @param annotationClass 注解类
   * @return 带有指定注解的方法列表
   */
  private List<Method> scanLifecycleMethods(Class<?> beanClass, Class<?> annotationClass) {
    List<Method> methods = new ArrayList<Method>();
    Class<?> currentClass = beanClass;

    // 遍历类层次结构，包括父类
    while (currentClass != null && currentClass != Object.class) {
      Method[] declaredMethods = currentClass.getDeclaredMethods();

      for (Method method : declaredMethods) {
        if (method.isAnnotationPresent(annotationClass)) {
          // 验证方法签名
          validateLifecycleMethod(method, annotationClass);
          methods.add(method);
        }
      }

      currentClass = currentClass.getSuperclass();
    }

    return methods;
  }

  /**
   * 验证生命周期方法的签名
   * 
   * @param method          方法
   * @param annotationClass 注解类
   */
  private void validateLifecycleMethod(Method method, Class<?> annotationClass) {
    String annotationName = annotationClass.getSimpleName();

    // 检查方法参数
    if (method.getParameterTypes().length > 0) {
      throw new IllegalStateException(
          "@" + annotationName + " 方法不能有参数: " + method);
    }

    // 检查是否为静态方法
    if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
      throw new IllegalStateException(
          "@" + annotationName + " 方法不能是静态方法: " + method);
    }
  }

  /**
   * 检查类是否有 @PostConstruct 方法
   * 
   * @param beanClass Bean 类
   * @return 如果有 @PostConstruct 方法返回 true
   */
  public boolean hasPostConstructMethods(Class<?> beanClass) {
    return !getPostConstructMethods(beanClass).isEmpty();
  }

  /**
   * 检查类是否有 @PreDestroy 方法
   * 
   * @param beanClass Bean 类
   * @return 如果有 @PreDestroy 方法返回 true
   */
  public boolean hasPreDestroyMethods(Class<?> beanClass) {
    return !getPreDestroyMethods(beanClass).isEmpty();
  }

  /**
   * 清除缓存（主要用于测试）
   */
  public void clearCache() {
    postConstructMethodsCache.clear();
    preDestroyMethodsCache.clear();
  }
}
