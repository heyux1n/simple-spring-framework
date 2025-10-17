package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * PostConstruct 注解
 * 标识在 Bean 初始化完成后需要调用的方法
 * 
 * 该注解用于标记在依赖注入完成后需要执行的初始化方法。
 * 被标记的方法将在 Bean 的所有依赖注入完成后自动调用。
 * 
 * 使用规则：
 * - 只能标注在方法上
 * - 方法不能有参数
 * - 方法不能是静态方法
 * - 方法可以有任意的访问修饰符
 * - 一个类中只能有一个方法被 @PostConstruct 标注
 * 
 * 示例：
 * 
 * <pre>
 * {
 *   &#64;code
 *   &#64;Component
 *   public class UserService {
 * 
 *     &#64;Autowired
 *     private UserRepository userRepository;
 * 
 *     @PostConstruct
 *     public void init() {
 *       // 初始化逻辑，此时所有依赖已注入完成
 *       System.out.println("UserService 初始化完成");
 *     }
 *   }
 * }
 * </pre>
 * 
 * @author SimpleSpring Framework
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct {
}
