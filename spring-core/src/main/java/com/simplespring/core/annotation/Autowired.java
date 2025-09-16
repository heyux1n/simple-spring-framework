package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动装配注解，用于标识需要进行依赖注入的字段、方法或构造函数
 * 
 * 当字段、方法或构造函数被 @Autowired 注解标记时，Spring IoC 容器会自动
 * 查找匹配的 Bean 并进行注入。注入是基于类型匹配的，容器会查找与目标类型
 * 兼容的 Bean 实例。
 * 
 * 支持的注入方式：
 * 1. 字段注入：直接在字段上使用 @Autowired
 * 2. 方法注入：在 setter 方法或其他方法上使用 @Autowired
 * 3. 构造函数注入：在构造函数上使用 @Autowired
 * 
 * 使用示例：
 * <pre>
 * {@code
 * public class UserController {
 *     
 *     // 字段注入
 *     @Autowired
 *     private UserService userService;
 *     
 *     // 方法注入
 *     @Autowired
 *     public void setOrderService(OrderService orderService) {
 *         this.orderService = orderService;
 *     }
 *     
 *     // 构造函数注入
 *     @Autowired
 *     public UserController(UserService userService) {
 *         this.userService = userService;
 *     }
 * }
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    
    /**
     * 指定依赖是否为必需的
     * 
     * 如果设置为 true（默认值），当找不到匹配的 Bean 时会抛出异常。
     * 如果设置为 false，当找不到匹配的 Bean 时会忽略注入，保持原值。
     * 
     * @return 是否为必需的依赖，默认为 true
     */
    boolean required() default true;
}
