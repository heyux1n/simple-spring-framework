package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 后置通知注解，用于标识在目标方法执行后要执行的通知方法
 * 
 * 当方法被 @After 注解标记时，该方法会在匹配的目标方法执行之后被调用，
 * 无论目标方法是正常返回还是抛出异常。这类似于 try-finally 块中的 finally 部分。
 * 
 * 后置通知方法的特点：
 * 1. 在目标方法执行后调用（无论成功还是异常）
 * 2. 无法访问目标方法的返回值
 * 3. 无法阻止异常的传播
 * 4. 通常用于资源清理、日志记录等
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
 * public class MonitoringAspect {
 *     
 *     @After("execution(* com.example.service.*.*(..))")
 *     public void logMethodExit(JoinPoint joinPoint) {
 *         System.out.println("方法执行完成: " + joinPoint.getSignature().getName());
 *     }
 *     
 *     @After("execution(* com.example.service.DatabaseService.*(..))")
 *     public void cleanupResources(JoinPoint joinPoint) {
 *         System.out.println("清理数据库连接资源");
 *         // 执行资源清理逻辑
 *     }
 *     
 *     @After("execution(* com.example.service.UserService.updateUser(..))")
 *     public void auditUserUpdate(JoinPoint joinPoint) {
 *         System.out.println("记录用户更新操作审计日志");
 *         // 记录审计信息
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
public @interface After {
    
    /**
     * 切点表达式，指定在哪些方法上应用此后置通知
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
