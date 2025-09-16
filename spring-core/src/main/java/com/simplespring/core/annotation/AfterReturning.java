package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 返回后通知注解，用于标识在目标方法正常返回后要执行的通知方法
 * 
 * 当方法被 @AfterReturning 注解标记时，该方法会在匹配的目标方法正常返回后被调用。
 * 如果目标方法抛出异常，则不会执行此通知。返回后通知可以访问目标方法的返回值。
 * 
 * 返回后通知方法的特点：
 * 1. 只在目标方法正常返回时调用（不包括异常情况）
 * 2. 可以访问目标方法的返回值
 * 3. 无法修改返回值
 * 4. 通常用于结果处理、缓存、日志记录等
 * 
 * 通知方法可以接收以下参数：
 * - JoinPoint：获取目标方法信息
 * - 返回值：通过 returning 属性指定参数名来接收返回值
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @Aspect
 * @Component
 * public class ResultProcessingAspect {
 *     
 *     @AfterReturning("execution(* com.example.service.*.*(..))")
 *     public void logMethodReturn(JoinPoint joinPoint) {
 *         System.out.println("方法正常返回: " + joinPoint.getSignature().getName());
 *     }
 *     
 *     @AfterReturning(value = "execution(* com.example.service.UserService.findUser(..))", 
 *                     returning = "user")
 *     public void processUserResult(JoinPoint joinPoint, Object user) {
 *         System.out.println("查询到用户: " + user);
 *         // 处理用户查询结果
 *     }
 *     
 *     @AfterReturning(value = "execution(* com.example.service.OrderService.createOrder(..))", 
 *                     returning = "orderId")
 *     public void cacheOrderResult(JoinPoint joinPoint, Long orderId) {
 *         System.out.println("缓存订单结果: " + orderId);
 *         // 缓存订单创建结果
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
public @interface AfterReturning {
    
    /**
     * 切点表达式，指定在哪些方法上应用此返回后通知
     * 
     * 支持的表达式格式：
     * - execution(* com.example.service.*.*(..))：匹配指定包下所有类的所有方法
     * - execution(* com.example.service.UserService.*(..))：匹配指定类的所有方法
     * - execution(* com.example.service.UserService.findUser(..))：匹配指定方法
     * 
     * @return 切点表达式字符串
     */
    String value();
    
    /**
     * 指定用于接收返回值的参数名
     * 
     * 如果指定了此属性，通知方法必须有一个与此名称匹配的参数来接收目标方法的返回值。
     * 参数类型应该与目标方法的返回类型兼容，或者使用 Object 类型来接收任意返回值。
     * 
     * 如果不指定此属性，通知方法将无法访问返回值。
     * 
     * @return 用于接收返回值的参数名，默认为空字符串表示不接收返回值
     */
    String returning() default "";
}
