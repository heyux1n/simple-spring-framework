package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 前置通知注解，用于标识在目标方法执行前要执行的通知方法
 * 
 * 当方法被 @Before 注解标记时，该方法会在匹配的目标方法执行之前被调用。
 * 前置通知可以用于执行一些预处理逻辑，如参数验证、日志记录、权限检查等。
 * 
 * 前置通知方法的特点：
 * 1. 在目标方法执行前调用
 * 2. 无法阻止目标方法的执行（除非抛出异常）
 * 3. 无法修改目标方法的参数
 * 4. 可以访问目标方法的参数信息
 * 
 * 通知方法可以接收 JoinPoint 参数来获取目标方法的信息：
 * - 目标对象
 * - 方法签名
 * - 方法参数
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @Aspect
 * @Component
 * public class SecurityAspect {
 *     
 *     @Before("execution(* com.example.service.UserService.*(..))")
 *     public void checkPermission(JoinPoint joinPoint) {
 *         System.out.println("检查权限: " + joinPoint.getSignature().getName());
 *         // 执行权限检查逻辑
 *     }
 *     
 *     @Before("execution(* com.example.service.*.*(..))")
 *     public void logMethodEntry(JoinPoint joinPoint) {
 *         Object[] args = joinPoint.getArgs();
 *         System.out.println("进入方法: " + joinPoint.getSignature().getName() 
 *                          + ", 参数: " + Arrays.toString(args));
 *     }
 * }
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
    
    /**
     * 切点表达式，指定在哪些方法上应用此前置通知
     * 
     * 支持的表达式格式：
     * - execution(* com.example.service.*.*(..))：匹配指定包下所有类的所有方法
     * - execution(* com.example.service.UserService.*(..))：匹配指定类的所有方法
     * - execution(* com.example.service.UserService.findUser(..))：匹配指定方法
     * 
     * @return 切点表达式字符串
     */
    String value();
}
