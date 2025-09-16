package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求映射注解，用于将 HTTP 请求映射到控制器的处理方法
 * 
 * @RequestMapping 注解可以用在类级别和方法级别：
 * - 类级别：为该控制器的所有方法提供基础路径
 * - 方法级别：指定具体的请求路径和 HTTP 方法
 * 
 * 当同时在类和方法上使用时，最终的请求路径是两者的组合。
 * 
 * 该注解支持以下功能：
 * 1. 路径映射：指定请求的 URL 路径
 * 2. HTTP 方法限制：指定处理的 HTTP 方法类型
 * 3. 路径参数：支持 RESTful 风格的路径参数
 * 4. 通配符匹配：支持路径模式匹配
 * 
 * 路径匹配规则：
 * - 精确匹配：/users/123
 * - 路径参数：/users/{id}
 * - 通配符：/users/*
 * - 多级通配符：/users/**
 * 
 * 使用示例：
 * <pre>
 * {@code
 * // 类级别的基础路径
 * @Controller
 * @RequestMapping("/api/v1")
 * public class UserController {
 *     
 *     // 默认 GET 方法，最终路径：/api/v1/users
 *     @RequestMapping("/users")
 *     public String listUsers() {
 *         return "user-list";
 *     }
 *     
 *     // 指定 POST 方法，最终路径：/api/v1/users
 *     @RequestMapping(value = "/users", method = RequestMethod.POST)
 *     public String createUser(User user) {
 *         return "redirect:/api/v1/users";
 *     }
 *     
 *     // 路径参数，最终路径：/api/v1/users/{id}
 *     @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
 *     public String getUser(@PathVariable Long id) {
 *         return "user-detail";
 *     }
 *     
 *     // 多个路径映射到同一个方法
 *     @RequestMapping(value = {"/users/{id}/edit", "/users/{id}/update"}, 
 *                     method = RequestMethod.GET)
 *     public String editUser(@PathVariable Long id) {
 *         return "user-edit";
 *     }
 * }
 * 
 * // 仅方法级别使用
 * @Controller
 * public class HomeController {
 *     
 *     @RequestMapping("/")
 *     public String home() {
 *         return "index";
 *     }
 *     
 *     @RequestMapping("/about")
 *     public String about() {
 *         return "about";
 *     }
 * }
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    
    /**
     * 指定请求的路径
     * 
     * 可以是单个路径字符串，也可以是路径数组（支持多个路径映射到同一个方法）。
     * 路径可以包含路径参数（如 /users/{id}）和通配符（如 /users/*）。
     * 
     * 如果在类级别使用，则作为该控制器所有方法的基础路径。
     * 如果在方法级别使用，则与类级别的路径组合形成完整路径。
     * 
     * @return 请求路径，默认为空字符串
     */
    String value() default "";
    
    /**
     * 指定处理的 HTTP 方法类型
     * 
     * 如果不指定，则默认处理 GET 请求。
     * 可以指定单个方法或多个方法。
     * 
     * 常用的 HTTP 方法：
     * - GET：获取资源
     * - POST：创建资源
     * - PUT：更新资源
     * - DELETE：删除资源
     * - PATCH：部分更新资源
     * 
     * @return HTTP 方法类型，默认为 GET
     */
    RequestMethod method() default RequestMethod.GET;
}
