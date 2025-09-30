package com.simplespring.aop;

import java.lang.reflect.Method;

/**
 * 切点匹配器接口
 * 用于判断方法是否匹配切点表达式
 * 
 * @author SimpleSpring
 */
public interface PointcutMatcher {

  /**
   * 判断方法是否匹配切点表达式
   * 
   * @param method      目标方法
   * @param targetClass 目标类
   * @return 如果匹配返回 true，否则返回 false
   */
  boolean matches(Method method, Class<?> targetClass);

  /**
   * 判断类是否匹配切点表达式
   * 
   * @param targetClass 目标类
   * @return 如果匹配返回 true，否则返回 false
   */
  boolean matches(Class<?> targetClass);

  /**
   * 获取切点表达式
   * 
   * @return 切点表达式字符串
   */
  String getExpression();
}
