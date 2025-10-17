# Spring Example 模块

## 模块概述

Spring Example 模块是一个完整的示例应用程序，展示了如何使用简易 Spring 框架的所有核心功能。该模块集成了 IoC 容器、依赖注入、AOP 切面编程、Web MVC 等功能，提供了实际的业务场景和最佳实践示例，是学习和理解整个框架的最佳入口。

### 主要功能
- **完整的业务示例**：用户管理和订单处理业务场景
- **IoC 容器演示**：展示依赖注入和 Bean 管理
- **AOP 切面应用**：日志记录、性能监控等横切关注点
- **Web MVC 实现**：RESTful API 和传统 Web 页面
- **集成测试用例**：端到端的功能测试

## 项目结构

```
spring-example/
├── pom.xml                                    # Maven 配置文件
├── README.md                                  # 模块说明文档
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/simplespring/example/
    │   │       ├── Application.java                  # 应用启动类
    │   │       ├── config/                           # 配置类
    │   │       │   └── AppConfig.java               # 应用配置
    │   │       ├── controller/                       # Web 控制器
    │   │       │   ├── OrderController.java         # 订单控制器
    │   │       │   └── UserController.java          # 用户控制器
    │   │       ├── service/                          # 业务服务层
    │   │       │   ├── impl/                        # 服务实现
    │   │       │   │   ├── OrderServiceImpl.java    # 订单服务实现
    │   │       │   │   └── UserServiceImpl.java     # 用户服务实现
    │   │       │   ├── OrderService.java            # 订单服务接口
    │   │       │   └── UserService.java             # 用户服务接口
    │   │       ├── aspect/                           # AOP 切面
    │   │       │   ├── LoggingAspect.java           # 日志切面
    │   │       │   └── PerformanceAspect.java       # 性能监控切面
    │   │       └── entity/                           # 实体类
    │   │           ├── Order.java                   # 订单实体
    │   │           ├── OrderStatus.java             # 订单状态枚举
    │   │           └── User.java                    # 用户实体
    │   ├── resources/
    │   │   ├── application.properties               # 应用配置文件
    │   │   └── logback.xml                         # 日志配置
    │   └── webapp/
    │       ├── WEB-INF/
    │       │   ├── web.xml                         # Web 应用配置
    │       │   └── views/                          # 视图模板
    │       │       ├── user/
    │       │       │   ├── list.jsp               # 用户列表页面
    │       │       │   └── detail.jsp             # 用户详情页面
    │       │       └── order/
    │       │           ├── list.jsp               # 订单列表页面
    │       │           └── detail.jsp             # 订单详情页面
    │       └── static/                             # 静态资源
    │           ├── css/
    │           ├── js/
    │           └── images/
    └── test/
        └── java/
            └── com/simplespring/example/      # 集成测试
                ├── ApplicationTest.java              # 应用测试
                ├── controller/                       # 控制器测试
                ├── service/                          # 服务测试
                └── integration/                      # 集成测试
```

## 技术栈与依赖

### 核心技术
- **Java 1.7+**：基础运行环境
- **Simple Spring Framework**：自研的 Spring 框架实现
- **Servlet API 3.1**：Web 应用标准
- **JSP/JSTL**：视图模板技术
- **JUnit 4**：单元测试框架
- **Mockito**：模拟测试框架

### 主要依赖
```xml
<dependencies>
    <!-- Simple Spring Framework 所有模块 -->
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
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>spring-aop</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- Web 相关依赖 -->
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
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.3</version>
    </dependency>
    
    <!-- 测试框架 -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>1.10.19</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 环境配置与运行步骤

### 1. 环境要求
- JDK 1.7 或更高版本
- Apache Maven 3.x
- Servlet 容器（Tomcat 8.0+、Jetty 9.0+ 等）
- 所有 Simple Spring Framework 模块

### 2. 编译和打包
```bash
# 进入模块目录
cd spring-example

# 编译源代码
mvn clean compile

# 运行测试
mvn test

# 打包 WAR 文件
mvn package
```

### 3. 部署和运行

#### 方式一：使用 Maven Tomcat 插件
```bash
# 添加 Tomcat 插件到 pom.xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <port>8080</port>
        <path>/spring-example</path>
    </configuration>
</plugin>

# 启动应用
mvn tomcat7:run
```

#### 方式二：部署到外部容器
```bash
# 将生成的 WAR 文件部署到 Tomcat
cp target/spring-example-1.0.0.war $TOMCAT_HOME/webapps/

# 启动 Tomcat
$TOMCAT_HOME/bin/startup.sh
```

### 4. 访问应用
- 应用首页：http://localhost:8080/spring-example/
- 用户管理：http://localhost:8080/spring-example/users
- 订单管理：http://localhost:8080/spring-example/orders
- API 接口：http://localhost:8080/spring-example/api/

## 模块交互与依赖关系

### 完整依赖关系图
```
spring-core (基础工具)
    ↑
spring-beans (IoC 容器)
    ↑
spring-context (应用上下文)
    ↑
spring-aop (切面编程)
    ↑
spring-webmvc (Web 框架)
    ↑
spring-example (示例应用)
```

### 应用架构层次
```
Web Layer (Controller)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Entity Layer (Domain Model)
```

## 示例与使用说明

### 1. 应用启动类
```java
public class Application {
    
    public static void main(String[] args) {
        // 创建应用上下文
        ApplicationContext context = new AnnotationConfigApplicationContext(
            "com.simplespring.example"
        );
        
        System.out.println("Simple Spring 应用启动成功！");
        
        // 演示 IoC 容器功能
        demonstrateIoC(context);
        
        // 演示 AOP 功能
        demonstrateAOP(context);
    }
    
    private static void demonstrateIoC(ApplicationContext context) {
        System.out.println("\n=== IoC 容器演示 ===");
        
        // 获取用户服务
        UserService userService = context.getBean(UserService.class);
        System.out.println("获取到用户服务: " + userService.getClass().getName());
        
        // 测试依赖注入
        User user = userService.findById(1L);
        System.out.println("查询用户: " + user);
    }
    
    private static void demonstrateAOP(ApplicationContext context) {
        System.out.println("\n=== AOP 切面演示 ===");
        
        // 获取订单服务（会被 AOP 代理）
        OrderService orderService = context.getBean(OrderService.class);
        
        // 调用方法会触发切面逻辑
        Order order = orderService.findById(1L);
        System.out.println("查询订单: " + order);
    }
}
```

### 2. 用户管理示例
```java
// 用户实体
public class User {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    
    // 构造函数、getter、setter 方法
    public User() {}
    
    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // toString 方法
    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', age=%d}", 
                           id, name, email, age);
    }
}

// 用户服务接口
public interface UserService {
    User findById(Long id);
    List<User> findAll();
    User save(User user);
    User update(User user);
    boolean deleteById(Long id);
}

// 用户服务实现
@Component
public class UserServiceImpl implements UserService {
    
    // 模拟数据存储
    private Map<Long, User> users = new HashMap<Long, User>();
    
    @PostConstruct
    public void init() {
        // 初始化测试数据
        users.put(1L, new User(1L, "张三", "zhangsan@example.com"));
        users.put(2L, new User(2L, "李四", "lisi@example.com"));
        users.put(3L, new User(3L, "王五", "wangwu@example.com"));
    }
    
    @Override
    public User findById(Long id) {
        return users.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<User>(users.values());
    }
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(System.currentTimeMillis());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }
        throw new RuntimeException("用户不存在: " + user.getId());
    }
    
    @Override
    public boolean deleteById(Long id) {
        return users.remove(id) != null;
    }
}
```

### 3. Web 控制器示例
```java
@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // 用户列表页面
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String listUsers(HttpServletRequest request) {
        List<User> users = userService.findAll();
        request.setAttribute("users", users);
        return "user/list";
    }
    
    // 用户详情页面
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public String getUserDetail(Long id, HttpServletRequest request) {
        User user = userService.findById(id);
        if (user == null) {
            request.setAttribute("error", "用户不存在");
            return "error/404";
        }
        
        request.setAttribute("user", user);
        return "user/detail";
    }
    
    // 创建用户 API
    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public String createUser(String name, String email, Integer age, 
                           HttpServletResponse response) {
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setAge(age);
            
            User savedUser = userService.save(user);
            
            response.setContentType("application/json;charset=UTF-8");
            return String.format("{\"success\":true,\"id\":%d}", savedUser.getId());
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }
    
    // 获取用户 API
    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.GET)
    public String getUserApi(Long id, HttpServletResponse response) {
        User user = userService.findById(id);
        
        response.setContentType("application/json;charset=UTF-8");
        
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "{\"error\":\"用户不存在\"}";
        }
        
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"age\":%d}",
            user.getId(), user.getName(), user.getEmail(), 
            user.getAge() != null ? user.getAge() : 0
        );
    }
}
```

### 4. AOP 切面示例
```java
// 日志记录切面
@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    
    @Before("execution(* com.simplespring.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        logger.info("执行方法: {}, 参数: {}", methodName, Arrays.toString(args));
    }
    
    @AfterReturning(value = "execution(* com.simplespring.example.service.*.*(..))", 
                    returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("方法 {} 执行成功, 返回值: {}", methodName, result);
    }
    
    @After("execution(* com.simplespring.example.service.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("方法 {} 执行完成", methodName);
    }
}

// 性能监控切面
@Aspect
@Component
public class PerformanceAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);
    private ThreadLocal<Long> startTime = new ThreadLocal<Long>();
    
    @Before("execution(* com.simplespring.example.service.*.*(..))")
    public void startTimer(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        logger.debug("开始执行方法: {}", joinPoint.getSignature().getName());
    }
    
    @After("execution(* com.simplespring.example.service.*.*(..))")
    public void endTimer(JoinPoint joinPoint) {
        long duration = System.currentTimeMillis() - startTime.get();
        startTime.remove();
        
        String methodName = joinPoint.getSignature().getName();
        logger.info("方法 {} 执行时间: {}ms", methodName, duration);
        
        // 性能告警
        if (duration > 1000) {
            logger.warn("方法 {} 执行时间过长: {}ms", methodName, duration);
        }
    }
}
```

### 5. 配置类示例
```java
@Configuration
public class AppConfig {
    
    @Bean
    public DataSource dataSource() {
        // 模拟数据源配置
        System.out.println("配置数据源...");
        return new MockDataSource();
    }
    
    @Bean
    public TransactionManager transactionManager() {
        // 模拟事务管理器配置
        System.out.println("配置事务管理器...");
        return new MockTransactionManager();
    }
}
```

## 核心功能演示

### 1. IoC 容器功能测试
```java
@Test
public void testIoCContainer() {
    // 创建应用上下文
    ApplicationContext context = new AnnotationConfigApplicationContext(
        "com.simplespring.example"
    );
    
    // 测试 Bean 获取
    UserService userService = context.getBean(UserService.class);
    assertNotNull(userService);
    
    // 测试依赖注入
    UserController userController = context.getBean(UserController.class);
    assertNotNull(userController);
    
    // 测试单例模式
    UserService userService2 = context.getBean(UserService.class);
    assertSame(userService, userService2);
}
```

### 2. AOP 功能测试
```java
@Test
public void testAOPFunctionality() {
    ApplicationContext context = new AnnotationConfigApplicationContext(
        "com.simplespring.example"
    );
    
    // 获取被代理的服务
    UserService userService = context.getBean(UserService.class);
    
    // 调用方法会触发切面
    User user = userService.findById(1L);
    assertNotNull(user);
    assertEquals("张三", user.getName());
    
    // 检查是否为代理对象
    assertTrue(isAopProxy(userService));
}
```

### 3. Web MVC 功能测试
```java
@Test
public void testWebMvcFunctionality() {
    // 模拟 HTTP 请求
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/users/1");
    
    MockHttpServletResponse response = new MockHttpServletResponse();
    
    // 创建 DispatcherServlet
    DispatcherServlet servlet = new DispatcherServlet();
    servlet.init();
    
    // 处理请求
    servlet.service(request, response);
    
    // 验证响应
    assertEquals(200, response.getStatus());
}
```

## 维护与扩展建议

### 1. 添加新的业务模块
```java
// 添加商品管理模块
@Component
public class ProductService {
    
    @Autowired
    private UserService userService;  // 可以依赖其他服务
    
    public Product findById(Long id) {
        // 业务逻辑实现
        return null;
    }
}

@Controller
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @RequestMapping("/products/{id}")
    public String getProduct(Long id, HttpServletRequest request) {
        Product product = productService.findById(id);
        request.setAttribute("product", product);
        return "product/detail";
    }
}
```

### 2. 扩展 AOP 功能
```java
// 添加安全检查切面
@Aspect
@Component
public class SecurityAspect {
    
    @Before("execution(* com.simplespring.example.controller.*.*(..))")
    public void checkSecurity(JoinPoint joinPoint) {
        // 实现安全检查逻辑
        System.out.println("执行安全检查...");
    }
}

// 添加缓存切面
@Aspect
@Component
public class CacheAspect {
    
    private Map<String, Object> cache = new ConcurrentHashMap<String, Object>();
    
    @Around("execution(* com.simplespring.example.service.*.find*(..))")
    public Object cacheResult(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = generateCacheKey(joinPoint);
        
        if (cache.containsKey(key)) {
            System.out.println("缓存命中: " + key);
            return cache.get(key);
        }
        
        Object result = joinPoint.proceed();
        cache.put(key, result);
        System.out.println("缓存存储: " + key);
        
        return result;
    }
}
```

### 3. 改进错误处理
```java
@Controller
public class ErrorController {
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        
        request.setAttribute("statusCode", statusCode);
        request.setAttribute("errorMessage", errorMessage);
        
        return "error/general";
    }
}
```

### 4. 性能监控和调优
```java
// 添加性能监控端点
@Controller
public class MonitorController {
    
    @Autowired
    private ApplicationContext context;
    
    @RequestMapping("/monitor/beans")
    public String listBeans(HttpServletRequest request) {
        String[] beanNames = context.getBeanDefinitionNames();
        request.setAttribute("beanNames", beanNames);
        request.setAttribute("beanCount", beanNames.length);
        return "monitor/beans";
    }
    
    @RequestMapping("/monitor/memory")
    public String memoryInfo(HttpServletRequest request) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        request.setAttribute("totalMemory", totalMemory / 1024 / 1024);
        request.setAttribute("usedMemory", usedMemory / 1024 / 1024);
        request.setAttribute("freeMemory", freeMemory / 1024 / 1024);
        
        return "monitor/memory";
    }
}
```

## 常见问题

### Q: 应用启动失败怎么办？
A: 
1. 检查所有依赖模块是否正确编译
2. 确认 web.xml 配置是否正确
3. 检查包扫描路径是否包含所有组件
4. 查看启动日志中的错误信息

### Q: 依赖注入不生效怎么调试？
A: 
1. 确认类上有正确的注解（@Component、@Controller 等）
2. 检查包扫描路径是否包含目标类
3. 确认字段或方法上有 @Autowired 注解
4. 检查是否存在循环依赖

### Q: AOP 切面不执行怎么办？
A: 
1. 确认切面类有 @Aspect 和 @Component 注解
2. 检查切点表达式是否正确
3. 确认目标方法是通过 Spring 容器获取的 Bean 调用的
4. 检查方法是否为 public

### Q: Web 请求 404 如何解决？
A: 
1. 检查 URL 映射是否正确
2. 确认 DispatcherServlet 的 URL 模式配置
3. 检查控制器方法的 @RequestMapping 注解
4. 确认静态资源路径配置

### Q: 如何进行集成测试？
A: 
```java
@Test
public void integrationTest() {
    // 创建完整的应用上下文
    ApplicationContext context = new AnnotationConfigApplicationContext(
        "com.simplespring.example"
    );
    
    // 测试完整的业务流程
    UserService userService = context.getBean(UserService.class);
    OrderService orderService = context.getBean(OrderService.class);
    
    // 创建用户
    User user = new User(null, "测试用户", "test@example.com");
    user = userService.save(user);
    
    // 创建订单
    Order order = new Order(null, user.getId(), "测试订单");
    order = orderService.save(order);
    
    // 验证结果
    assertNotNull(order.getId());
    assertEquals(user.getId(), order.getUserId());
}
```

---

**注意**：Spring Example 模块展示了完整的框架使用方式，建议按照示例代码的模式来组织自己的项目结构。在实际开发中，可以根据业务需求扩展和修改示例代码。
