package com.simplespring.aop;

import java.lang.reflect.Method;

/**
 * 连接点接口
 * 提供连接点信息，包括目标对象、方法、参数等
 * 
 * @author SimpleSpring
 */
public interface JoinPoint {

  /**
   * 获取目标对象
   * 
   * @return 目标对象实例
   */
  Object getTarget();

  /**
   * 获取目标方法
   * 
   * @return 目标方法
   */
  Method getMethod();

  /**
   * 获取方法参数
   * 
   * @return 方法参数数组
   */
  Object[] getArgs();

  /**
   * 获取方法签名字符串
   * 
   * @return 方法签名
   */
  String getSignature();

  /**
   * 获取目标类
   * 
   * @return 目标类
   */
  Class<?> getTargetClass();

  /**
   * 获取连接点类型
   * 
   * @return 连接点类型
   */
  JoinPointType getJoinPointType();

  /**
   * 连接点类型枚举
   */
  enum JoinPointType {
    /**
     * 方法执行
     */
    METHOD_EXECUTION,

    /**
     * 方法调用
     */
    METHOD_CALL,

    /**
     * 字段访问
     */
    FIELD_ACCESS,

    /**
     * 字段设置
     */
    FIELD_SET
  }
}
