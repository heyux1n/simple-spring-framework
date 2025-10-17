# Spring WebMVC 模块

## 模块概述

Spring WebMVC 模块实现了基于 MVC（Model-View-Controller）设计模式的 Web 框架，提供了完整的 Web 请求处理机制。该模块支持基于注解的控制器定义、自动参数绑定、灵活的视图解析等功能，是构建 Web 应用程序和 RESTful API 的核心基础。

### 主要功能
- **前端控制器**：DispatcherServlet 统一处理所有 Web 请求
- **请求映射**：基于注解的 URL 路径和 HTTP 方法映射
- **参数绑定**：自动解析和转换请求参数
- **视图解析**：支持多种视图解析策略
- **RESTful 支持**：完整的 REST API 开发支持

## 项目结构

```
spring-webmvc/
├── pom.xml                                    # Maven 配置文件
├── README.md                                  # 模块说明文档
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/simplespring/webmvc/
    │   │       ├── BasicTypeParameterResolver.java    # 基本类型参数解析器
    │   │       ├── CompositeViewResolver.java         # 组合视图解析器
    │   │       ├── DispatcherServlet.java             # 前端控制器
    │   │       ├── HandlerExecutionChain.java         # 处理器执行链
    │   │       ├── HandlerMapping.java                # 处理器映射接口
    │   │       ├── HandlerMethod.java                 # 处理器方法封装
    │   │       ├── JsonViewResolver.java              # JSON 视图解析器
    │   │       ├── ParameterResolver.java             # 参数解析器接口
    │   │       ├── ParameterResolverComposite.java    # 参数解析器组合
    │   │       ├── RequestMappingHandlerMapping.java  # 请求映射处理器映射
    │   │       ├── RequestMappingInfo.java            # 请求映射信息
    │   │       ├── ServletParameterResolver.java      # Servlet 参数解析器
    │   │       ├── SimpleViewResolver.java            # 简单视图解析器
    │   │       └── ViewResolver.java                  # 视图解析器接口
    │   └── resources/
    │       └── .gitkeep
    └── test/
        └── java/
            └── com/simplespring/webmvc/       # 单元测试
```

## 技术栈与依赖

### 核心技术
- **Java 1.7+**：基础运行环境
- **Servlet API 3.1**：Web 容器标准接口
- **HTTP 协议**：Web 请求和响应处理
- **JSON 处理**：RESTful API 数据交换
- **反射机制**：动态方法调用和参数绑定

### 主要依赖
```xml
<dependencies>
    <!-- 内部依赖 -->
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>spring-core</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>spring-beans</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>spring-context</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- Servlet API -->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>3.1.0</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- 日志框架 -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.25</version>
    </dependency>
    
    <!-- 测试框架 -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 环境配置与运行步骤

### 1. 环境要求
- JDK 1.7 或更高版本
- Apache Maven 3.x
- Servlet 容器（Tomcat 8.0+、Jetty 9.0+ 等）
- spring-core、spring-beans、spring-context 模块（自动依赖）

### 2. 编译模块
```bash
# 进入模块目录
cd spring-webmvc

# 编译源代码
mvn clean compile

# 运行测试
mvn test

# 打包 JAR 文件
mvn package
```

### 3. Web 应用配置

#### web.xml 配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
         http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0">

    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>com.simplespring.webmvc.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>com.example.config</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

</web-app>
```

### 4. 在其他项目中使用
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 模块交互与依赖关系

### 依赖关系图
```
spring-core
    ↑
spring-beans
    ↑
spring-context
    ↑
spring-webmvc
    ↑
spring-example (Web 应用示例)
```

### 请求处理流程
```
HTTP Request
    ↓
DispatcherServlet (前端控制器)
    ↓
HandlerMapping (查找处理器)
    ↓
HandlerMethod (执行处理器方法)
    ↓
ParameterResolver (解析参数)
    ↓
Controller Method (业务逻辑)
    ↓
ViewResolver (解析视图)
    ↓
HTTP Response
```

## 示例与使用说明

### 1. 定义控制器
```java
@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // GET 请求 - 获取用户信息
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public String getUser(Long id, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.findById(id);
        request.setAttribute("user", user);
        return "user/detail";  // 返回视图名称
    }
    
    // POST 请求 - 创建用户
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser(String name, String email, Integer age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        
        userService.save(user);
        return "redirect:/users/" + user.getId();
    }
    
    // PUT 请求 - 更新用户
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public String updateUser(Long id, String name, String email) {
        User user = userService.findById(id);
        user.setName(name);
        user.setEmail(email);
        
        userService.update(user);
        return "user/success";
    }
    
    // DELETE 请求 - 删除用户
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public String deleteUser(Long id) {
        userService.deleteById(id);
        return "user/deleted";
    }
}
```

### 2. RESTful API 控制器
```java
@Controller
public class ApiController {
    
    @Autowired
    private UserService userService;
    
    // 返回 JSON 数据
    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.GET)
    public String getUserJson(Long id, HttpServletResponse response) {
        User user = userService.findById(id);
        
        // 设置响应类型为 JSON
        response.setContentType("application/json;charset=UTF-8");
        
        // 手动构建 JSON 响应
        String json = String.format(
            "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\"}", 
            user.getId(), user.getName(), user.getEmail()
        );
        
        return json;
    }
    
    // 处理 JSON 请求体
    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public String createUserJson(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 读取请求体中的 JSON 数据
            BufferedReader reader = request.getReader();
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            
            // 解析 JSON（简单实现）
            // 实际项目中建议使用 Jackson 或 Gson
            User user = parseUserFromJson(json.toString());
            userService.save(user);
            
            response.setContentType("application/json;charset=UTF-8");
            return "{\"success\":true,\"id\":" + user.getId() + "}";
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}
```

### 3. 参数绑定示例
```java
@Controller
public class OrderController {
    
    // 基本类型参数
    @RequestMapping("/orders/search")
    public String searchOrders(String keyword, Integer page, Integer size) {
        // keyword, page, size 会自动从请求参数中绑定
        List<Order> orders = orderService.search(keyword, page, size);
        return "order/list";
    }
    
    // 路径变量参数
    @RequestMapping("/orders/{orderId}/items/{itemId}")
    public String getOrderItem(Long orderId, Long itemId) {
        // orderId 和 itemId 从 URL 路径中提取
        OrderItem item = orderService.getOrderItem(orderId, itemId);
        return "order/item";
    }
    
    // Servlet API 参数
    @RequestMapping("/orders/export")
    public String exportOrders(HttpServletRequest request, HttpServletResponse response) {
        // 直接注入 Servlet API 对象
        String format = request.getParameter("format");
        
        if ("excel".equals(format)) {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=orders.xlsx");
        }
        
        return "order/export";
    }
}
```

### 4. 视图解析配置
```java
@Configuration
public class WebConfig {
    
    @Bean
    public ViewResolver viewResolver() {
        CompositeViewResolver composite = new CompositeViewResolver();
        
        // 添加 JSON 视图解析器
        composite.addViewResolver(new JsonViewResolver());
        
        // 添加简单视图解析器
        SimpleViewResolver simple = new SimpleViewResolver();
        simple.setPrefix("/WEB-INF/views/");
        simple.setSuffix(".jsp");
        composite.addViewResolver(simple);
        
        return composite;
    }
}
```

## 核心功能详解

### 1. DispatcherServlet 工作流程
```java
public class DispatcherServlet extends HttpServlet {
    
    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1. 查找处理器
            HandlerExecutionChain chain = getHandler(request);
            if (chain == null) {
                noHandlerFound(request, response);
                return;
            }
            
            // 2. 获取处理器方法
            HandlerMethod handlerMethod = chain.getHandler();
            
            // 3. 解析方法参数
            Object[] args = resolveArguments(handlerMethod, request, response);
            
            // 4. 执行处理器方法
            Object result = handlerMethod.invoke(args);
            
            // 5. 处理返回值
            processReturnValue(result, request, response);
            
        } catch (Exception e) {
            handleException(e, request, response);
        }
    }
}
```

### 2. 请求映射匹配
```java
public class RequestMappingHandlerMapping implements HandlerMapping {
    
    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) {
        String requestPath = getRequestPath(request);
        String requestMethod = request.getMethod();
        
        // 遍历所有注册的处理器方法
        for (RequestMappingInfo mappingInfo : handlerMethods.keySet()) {
            if (mappingInfo.matches(requestPath, requestMethod)) {
                HandlerMethod handlerMethod = handlerMethods.get(mappingInfo);
                return new HandlerExecutionChain(handlerMethod);
            }
        }
        
        return null;
    }
}
```

### 3. 参数解析机制
```java
public class ParameterResolverComposite implements ParameterResolver {
    
    private List<ParameterResolver> resolvers = Arrays.asList(
        new BasicTypeParameterResolver(),
        new ServletParameterResolver()
    );
    
    @Override
    public Object resolveParameter(Parameter parameter, HttpServletRequest request, 
                                 HttpServletResponse response) {
        for (ParameterResolver resolver : resolvers) {
            if (resolver.supportsParameter(parameter)) {
                return resolver.resolveParameter(parameter, request, response);
            }
        }
        
        throw new IllegalArgumentException("不支持的参数类型: " + parameter.getType());
    }
}
```

### 4. 视图解析流程
```java
public class CompositeViewResolver implements ViewResolver {
    
    @Override
    public String resolveView(String viewName, HttpServletRequest request, 
                            HttpServletResponse response) {
        for (ViewResolver resolver : viewResolvers) {
            String result = resolver.resolveView(viewName, request, response);
            if (result != null) {
                return result;
            }
        }
        
        // 默认返回视图名称本身
        return viewName;
    }
}
```

## 维护与扩展建议

### 1. 添加新的参数解析器
```java
// 自定义参数解析器
public class JsonParameterResolver implements ParameterResolver {
    
    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }
    
    @Override
    public Object resolveParameter(Parameter parameter, HttpServletRequest request, 
                                 HttpServletResponse response) {
        try {
            // 读取请求体
            String json = readRequestBody(request);
            
            // 解析 JSON 到对象
            return parseJsonToObject(json, parameter.getType());
            
        } catch (Exception e) {
            throw new RuntimeException("JSON 参数解析失败", e);
        }
    }
}
```

### 2. 扩展视图解析功能
```java
// 模板引擎视图解析器
public class TemplateViewResolver implements ViewResolver {
    
    private TemplateEngine templateEngine;
    
    @Override
    public String resolveView(String viewName, HttpServletRequest request, 
                            HttpServletResponse response) {
        try {
            // 获取模板
            Template template = templateEngine.getTemplate(viewName);
            
            // 准备数据模型
            Map<String, Object> model = extractModel(request);
            
            // 渲染模板
            return template.render(model);
            
        } catch (Exception e) {
            return null;
        }
    }
}
```

### 3. 添加拦截器支持
```java
public interface HandlerInterceptor {
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler);
    void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, Object result);
    void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);
}

// 使用拦截器
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("请求开始: " + request.getRequestURI());
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) {
        System.out.println("请求完成: " + request.getRequestURI());
    }
}
```

### 4. 性能优化建议
- **处理器缓存**：缓存请求映射和处理器方法
- **参数解析优化**：缓存参数解析结果
- **视图缓存**：缓存编译后的视图模板
- **异步处理**：支持异步请求处理

### 5. 错误处理增强
```java
@Controller
public class ErrorController {
    
    @RequestMapping("/error/404")
    public String handle404(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "error/404";
    }
    
    @RequestMapping("/error/500")
    public String handle500(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return "error/500";
    }
}
```

### 6. 测试支持
```java
// Web MVC 测试工具
public class MockMvcTest {
    
    @Test
    public void testUserController() {
        // 模拟 HTTP 请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/users/1");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 执行请求
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.service(request, response);
        
        // 验证响应
        assertEquals(200, response.getStatus());
    }
}
```

## 常见问题

### Q: 404 错误如何调试？
A: 
1. 检查 URL 映射是否正确
2. 确认控制器类有 `@Controller` 注解
3. 检查方法上的 `@RequestMapping` 注解
4. 确认 DispatcherServlet 的 URL 模式配置

### Q: 参数绑定失败怎么办？
A: 
1. 检查参数名称是否与请求参数匹配
2. 确认参数类型转换是否支持
3. 检查请求的 Content-Type 是否正确
4. 使用调试日志查看参数解析过程

### Q: 如何处理中文乱码？
A: 
```java
// 在 web.xml 中配置字符编码过滤器
<filter>
    <filter-name>encodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
</filter>
```

### Q: 如何实现文件上传？
A: 
```java
@RequestMapping(value = "/upload", method = RequestMethod.POST)
public String handleFileUpload(HttpServletRequest request) {
    // 处理 multipart/form-data 请求
    // 需要配置 MultipartResolver
}
```

### Q: 性能问题如何优化？
A: 
1. 使用适当的视图缓存策略
2. 优化数据库查询和业务逻辑
3. 启用 HTTP 缓存头
4. 考虑使用 CDN 加速静态资源

---

**注意**：Spring WebMVC 模块需要在 Servlet 容器中运行，建议在开发时使用嵌入式容器（如 Tomcat）进行快速测试和调试。
