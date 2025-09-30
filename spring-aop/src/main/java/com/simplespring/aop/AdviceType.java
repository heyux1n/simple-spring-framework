package com.simplespring.aop;

/**
 * 通知类型枚举
 * 定义 AOP 中支持的通知类型
 * 
 * @author SimpleSpring
 */
public enum AdviceType {

  /**
   * 前置通知 - 在目标方法执行前执行
   */
  BEFORE,

  /**
   * 后置通知 - 在目标方法执行后执行（无论是否抛出异常）
   */
  AFTER,

  /**
   * 返回后通知 - 在目标方法正常返回后执行
   */
  AFTER_RETURNING,

  /**
   * 异常通知 - 在目标方法抛出异常后执行
   */
  AFTER_THROWING,

  /**
   * 环绕通知 - 包围目标方法执行
   */
  AROUND
}
