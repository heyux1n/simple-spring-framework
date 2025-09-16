package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 控制器注解，用于标识一个类为 Spring MVC 控制器
 * 
 * 当类被 @Controller 注解标记时，表示该类是一个 Web 控制器，负责处理 HTTP 请求。
 * Spring MVC 框架会自动扫描并注册这些控制器类，使其能够处理来自客户端的请求。
 * 
 * 控制器类通常包含一个或多个处理方法，这些方法使用 @RequestMapping 注解来
 * 指定它们处理的请求路径和 HTTP 方法。
 * 
 * @Controller 注解实际上是 @Component 的特化版本，因此被标记的类也会被
 * Spring IoC 容器管理为一个 Bean。
 * 
 * 控制器方法的特点：
 * 1. 可以接收各种类型的参数（HttpServletRequest、HttpServletResponse、路径参数等）
 * 2. 可以返回不同类型的响应（字符串、对象、视图名等）
 * 3. 可以使用依赖注入来获取服务层组件
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @Controller
 * public class UserController {
 *     
 *     @Autowired
 *     private UserService userService;
 *     
 *     @RequestMapping("/users")
 *     public String listUsers() {
 *         return "user-list";
 *     }
 *     
 *     @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
 *     public String getUser(@PathVariable Long id) {
 *         User user = userService.findById(id);
 *         return "user-detail";
 *     }
 *     
 *     @RequestMapping(value = "/users", method = RequestMethod.POST)
 *     public String createUser(User user) {
 *         userService.save(user);
 *         return "redirect:/users";
 *     }
 * }
 * 
 * @Controller("customUserController")
 * public class CustomUserController {
 *     // 自定义控制器名称
 * }
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    
    /**
     * 指定控制器的名称
     * 
     * 如果不指定，则使用类名的首字母小写形式作为默认名称。
     * 这个名称会作为 Bean 的名称在 Spring 容器中注册。
     * 
     * @return 控制器名称，如果为空字符串则使用默认命名规则（类名首字母小写）
     */
    String value() default "";
}
