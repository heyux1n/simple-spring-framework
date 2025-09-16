package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 切面注解，用于标识一个类为 AOP 切面类
 * 
 * 当类被 @Aspect 注解标记时，表示该类是一个切面，包含一个或多个通知方法
 * （如 @Before、@After、@AfterReturning 等）。Spring AOP 容器会识别这些
 * 切面类并将其应用到匹配的目标对象上。
 * 
 * 切面类通常包含：
 * 1. 切点定义：指定在哪些方法上应用通知
 * 2. 通知方法：定义在切点执行时要执行的横切逻辑
 * 
 * 切面类本身也需要被 Spring 容器管理，因此通常还需要使用 @Component
 * 或其他组件注解来标识。
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @Aspect
 * @Component
 * public class LoggingAspect {
 *     
 *     @Before("execution(* com.example.service.*.*(..))")
 *     public void logBefore(JoinPoint joinPoint) {
 *         System.out.println("方法执行前: " + joinPoint.getSignature().getName());
 *     }
 *     
 *     @After("execution(* com.example.service.*.*(..))")
 *     public void logAfter(JoinPoint joinPoint) {
 *         System.out.println("方法执行后: " + joinPoint.getSignature().getName());
 *     }
 *     
 *     @AfterReturning(value = "execution(* com.example.service.*.*(..))", returning = "result")
 *     public void logAfterReturning(JoinPoint joinPoint, Object result) {
 *         System.out.println("方法返回值: " + result);
 *     }
 * }
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    // 切面注解不需要额外的属性
}
