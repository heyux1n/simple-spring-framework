package com.simplespring.core.annotation;

/**
 * HTTP 请求方法枚举，定义 Spring MVC 支持的 HTTP 方法类型
 * 
 * 该枚举用于 @RequestMapping 注解中，指定控制器方法处理的 HTTP 请求类型。
 * 不同的 HTTP 方法有不同的语义和用途：
 * 
 * - GET：用于获取资源，应该是安全和幂等的
 * - POST：用于创建资源或提交数据，可能有副作用
 * - PUT：用于更新资源，应该是幂等的
 * - DELETE：用于删除资源，应该是幂等的
 * - PATCH：用于部分更新资源
 * - HEAD：类似 GET，但只返回响应头，不返回响应体
 * - OPTIONS：用于获取资源支持的 HTTP 方法
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @Controller
 * public class UserController {
 *     
 *     @RequestMapping(value = "/users", method = RequestMethod.GET)
 *     public String listUsers() {
 *         return "user-list";
 *     }
 *     
 *     @RequestMapping(value = "/users", method = RequestMethod.POST)
 *     public String createUser(User user) {
 *         return "redirect:/users";
 *     }
 *     
 *     @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
 *     public String updateUser(@PathVariable Long id, User user) {
 *         return "redirect:/users/" + id;
 *     }
 *     
 *     @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
 *     public String deleteUser(@PathVariable Long id) {
 *         return "redirect:/users";
 *     }
 * }
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public enum RequestMethod {
    
    /**
     * HTTP GET 方法
     * 
     * 用于请求获取指定资源。GET 请求应该只用于获取数据，不应该有副作用。
     * GET 请求是安全的和幂等的，可以被缓存。
     */
    GET,
    
    /**
     * HTTP POST 方法
     * 
     * 用于向服务器提交数据，通常用于创建新资源或提交表单数据。
     * POST 请求可能有副作用，不是幂等的。
     */
    POST,
    
    /**
     * HTTP PUT 方法
     * 
     * 用于更新指定资源，如果资源不存在则创建。
     * PUT 请求应该是幂等的，多次执行相同的 PUT 请求应该产生相同的结果。
     */
    PUT,
    
    /**
     * HTTP DELETE 方法
     * 
     * 用于删除指定资源。
     * DELETE 请求应该是幂等的，多次删除同一资源应该产生相同的结果。
     */
    DELETE,
    
    /**
     * HTTP PATCH 方法
     * 
     * 用于对资源进行部分更新。与 PUT 不同，PATCH 只更新资源的部分字段。
     * PATCH 请求不一定是幂等的，取决于具体的实现。
     */
    PATCH,
    
    /**
     * HTTP HEAD 方法
     * 
     * 类似于 GET 请求，但服务器只返回响应头，不返回响应体。
     * 通常用于检查资源是否存在或获取资源的元信息。
     */
    HEAD,
    
    /**
     * HTTP OPTIONS 方法
     * 
     * 用于获取目标资源支持的 HTTP 方法，通常用于 CORS 预检请求。
     * 服务器应该在响应头中返回支持的方法列表。
     */
    OPTIONS
}
