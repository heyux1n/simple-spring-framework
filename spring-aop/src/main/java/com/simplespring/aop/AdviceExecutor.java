package com.simplespring.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知执行器
 * 负责执行不同类型的通知方法
 * 
 * @author SimpleSpring
 */
public class AdviceExecutor {

  /**
   * 执行前置通知
   * 
   * @param adviceDefinition 通知定义
   * @param joinPoint        连接点
   * @throws Throwable 执行异常
   */
  public static void executeBefore(AdviceDefinition adviceDefinition, JoinPoint joinPoint) throws Throwable {
    if (adviceDefinition.getType() != AdviceType.BEFORE) {
      throw new IllegalArgumentException("通知类型不匹配，期望 BEFORE，实际 " + adviceDefinition.getType());
    }

    executeAdvice(adviceDefinition, joinPoint, null, null);
  }

  /**
   * 执行后置通知
   * 
   * @param adviceDefinition 通知定义
   * @param joinPoint        连接点
   * @throws Throwable 执行异常
   */
  public static void executeAfter(AdviceDefinition adviceDefinition, JoinPoint joinPoint) throws Throwable {
    if (adviceDefinition.getType() != AdviceType.AFTER) {
      throw new IllegalArgumentException("通知类型不匹配，期望 AFTER，实际 " + adviceDefinition.getType());
    }

    executeAdvice(adviceDefinition, joinPoint, null, null);
  }

  /**
   * 执行返回后通知
   * 
   * @param adviceDefinition 通知定义
   * @param joinPoint        连接点
   * @param returnValue      方法返回值
   * @throws Throwable 执行异常
   */
  public static void executeAfterReturning(AdviceDefinition adviceDefinition, JoinPoint joinPoint,
      Object returnValue) throws Throwable {
    if (adviceDefinition.getType() != AdviceType.AFTER_RETURNING) {
      throw new IllegalArgumentException("通知类型不匹配，期望 AFTER_RETURNING，实际 " + adviceDefinition.getType());
    }

    executeAdvice(adviceDefinition, joinPoint, returnValue, null);
  }

  /**
   * 执行异常通知
   * 
   * @param adviceDefinition 通知定义
   * @param joinPoint        连接点
   * @param exception        异常
   * @throws Throwable 执行异常
   */
  public static void executeAfterThrowing(AdviceDefinition adviceDefinition, JoinPoint joinPoint,
      Throwable exception) throws Throwable {
    if (adviceDefinition.getType() != AdviceType.AFTER_THROWING) {
      throw new IllegalArgumentException("通知类型不匹配，期望 AFTER_THROWING，实际 " + adviceDefinition.getType());
    }

    executeAdvice(adviceDefinition, joinPoint, null, exception);
  }

  /**
   * 执行环绕通知
   * 
   * @param adviceDefinition 通知定义
   * @param joinPoint        连接点
   * @return 方法返回值
   * @throws Throwable 执行异常
   */
  public static Object executeAround(AdviceDefinition adviceDefinition, JoinPoint joinPoint) throws Throwable {
    if (adviceDefinition.getType() != AdviceType.AROUND) {
      throw new IllegalArgumentException("通知类型不匹配，期望 AROUND，实际 " + adviceDefinition.getType());
    }

    return executeAdvice(adviceDefinition, joinPoint, null, null);
  }

  /**
   * 执行通知方法
   * 
   * @param adviceDefinition 通知定义
   * @param joinPoint        连接点
   * @param returnValue      方法返回值（用于 AfterReturning）
   * @param exception        异常（用于 AfterThrowing）
   * @return 通知方法的返回值
   * @throws Throwable 执行异常
   */
  private static Object executeAdvice(AdviceDefinition adviceDefinition, JoinPoint joinPoint,
      Object returnValue, Throwable exception) throws Throwable {
    Method adviceMethod = adviceDefinition.getAdviceMethod();
    Object aspectInstance = adviceDefinition.getAspectInstance();

    if (adviceMethod == null || aspectInstance == null) {
      throw new IllegalArgumentException("通知方法或切面实例不能为 null");
    }

    // 准备方法参数
    Object[] args = prepareAdviceArgs(adviceDefinition, joinPoint, returnValue, exception);

    try {
      // 确保方法可访问
      if (!adviceMethod.isAccessible()) {
        adviceMethod.setAccessible(true);
      }

      // 执行通知方法
      return adviceMethod.invoke(aspectInstance, args);
    } catch (Exception e) {
      // 处理反射异常
      Throwable cause = e.getCause();
      if (cause != null) {
        throw cause;
      } else {
        throw new RuntimeException("执行通知方法失败: " + adviceMethod.getName(), e);
      }
    }
  }

  /**
   * 准备通知方法的参数
   * 
   * @param adviceDefinition 通知定义
   * @param joinPoint        连接点
   * @param returnValue      方法返回值
   * @param exception        异常
   * @return 参数数组
   */
  private static Object[] prepareAdviceArgs(AdviceDefinition adviceDefinition, JoinPoint joinPoint,
      Object returnValue, Throwable exception) {
    Method adviceMethod = adviceDefinition.getAdviceMethod();
    Class<?>[] paramTypes = adviceMethod.getParameterTypes();

    if (paramTypes.length == 0) {
      return new Object[0];
    }

    List<Object> args = new ArrayList<Object>();

    // JDK 1.7 兼容的参数处理方式
    for (Class<?> paramType : paramTypes) {
      if (JoinPoint.class.isAssignableFrom(paramType)) {
        // JoinPoint 参数
        args.add(joinPoint);
      } else if (paramType == Object.class && returnValue != null &&
          adviceDefinition.getType() == AdviceType.AFTER_RETURNING) {
        // 返回值参数（AfterReturning）
        args.add(returnValue);
      } else if (Throwable.class.isAssignableFrom(paramType) && exception != null &&
          adviceDefinition.getType() == AdviceType.AFTER_THROWING) {
        // 异常参数（AfterThrowing）
        args.add(exception);
      } else {
        // 其他参数类型，尝试从 JoinPoint 获取
        Object[] joinPointArgs = joinPoint.getArgs();
        boolean found = false;

        for (Object arg : joinPointArgs) {
          if (arg != null && paramType.isAssignableFrom(arg.getClass())) {
            args.add(arg);
            found = true;
            break;
          }
        }

        if (!found) {
          // 如果找不到匹配的参数，使用 null
          args.add(null);
        }
      }
    }

    return args.toArray(new Object[args.size()]);
  }

  /**
   * 检查通知方法是否匹配连接点
   * 
   * @param adviceDefinition 通知定义
   * @param joinPoint        连接点
   * @return 如果匹配返回 true，否则返回 false
   */
  public static boolean matches(AdviceDefinition adviceDefinition, JoinPoint joinPoint) {
    try {
      String pointcutExpression = adviceDefinition.getPointcutExpression();
      if (pointcutExpression == null || pointcutExpression.trim().isEmpty()) {
        return false;
      }

      PointcutMatcher matcher = PointcutExpressionParser.parse(pointcutExpression);
      return matcher.matches(joinPoint.getMethod(), joinPoint.getTargetClass());
    } catch (Exception e) {
      // 如果解析切点表达式失败，返回 false
      return false;
    }
  }
}
