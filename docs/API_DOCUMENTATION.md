# Simple Spring Framework API 文档

## 概述

本文档详细介绍了简易 Spring 框架的核心 API，包括使用方法、参数说明和代码示例。

## 目录

- [IoC 容器 API](#ioc-容器-api)
- [依赖注入 API](#依赖注入-api)
- [AOP 切面 API](#aop-切面-api)
- [MVC 框架 API](#mvc-框架-api)
- [工具类 API](#工具类-api)
- [异常处理](#异常处理)

---

## IoC 容器 API

### ApplicationContext 接口

应用上下文是 Spring 框架的核心接口，提供了完整的容器功能。

#### 创建应用上下文

```java
// 基于包扫描创建上下文
ApplicationContext context = new AnnotationConfigApplicationContext("com.example");

// 基于配置类创建上下文
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

// 基于多个包路径创建上下文
ApplicationContext context = new AnnotationConfigApplicationContext("com.example.service", "com.example.dao");
```

#### 获取 Bean

```java
// 根据类型获取 Bean
UserService userService = context.getBean(UserService.class);

// 根据名称获取 Bean
Object userService = context.getBean("userService");

// 根据名称和类型获取 Bean
UserService userService = context.getBean("userService", UserService.class);

// 检查是否包含指定的 Bean
boolean hasBean = context.containsBean("userService");
```

#### 容器生命周期管理

```java
// 刷新容器（重新扫描和初始化）
context.refresh();

// 检查容器状态
boolean isActive = context.isActive();

// 获取容器启动时间
long startupTime = context.getStartupDate();

// 关闭容器
context.close();
```

### BeanFactory 接口

Bean 工厂提供了更底层的 Bean 管理功能。

#### Bean 定义管理

```java
// 创建 Bean 定义
BeanDefinition beanDefinition = new BeanDefinition(UserService.class, "userService");
beanDefinition.setScope(Scope.SINGLETON);

// 注册 Bean 定义
beanFactory.registerBeanDefinition("userService", beanDefinition);

// 获取 Bean 定义
BeanDefinition definition = beanFactory.getBeanDefinition("userService");

// 获取所有 Bean 定义名称
String[] beanNames = beanFactory.getBeanDefinitionNames();
```

#### Bean 创建和管理

```java
// 创建 Bean 实例
Object bean = beanFactory.createBean(beanDefinition);

// 检查 Bean 作用域
boolean isSingleton = beanFactory.isSingleton("userService");
boolean isPrototype = beanFactory.isPrototype("userService");

// 获取 Bean 类型
Class<?> beanType = beanFactory.getType("userService");
```

---

## 依赖注入 API

### @Component 注解

用于标识组件类，使其被容器自动扫描和管理。

```java
// 基本用法
@Component
public class UserService {
    // 服务实现
}

// 指定 Bean 名称
@Component("customUserService")
public class UserService {
    // 服务实现
}
```

### @Autowired 注解

用于自动装配依赖，支持字段、方法和构造函数注入。

#### 字段注入

```java
@Component
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired(required = false)  // 可选依赖
    private EmailService emailService;
}
```

#### 方法注入

```java
@Component
public class UserController {
    
    private UserService userService;
    private EmailService emailService;
    
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    @Autowired
    public void configureServices(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }
}
```

#### 构造函数注入

```java
@Component
public class UserController {
    
    private final UserService userService;
    private final EmailService emailService;
    
    @Autowired
    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }
}
```

### @Configuration 和 @Bean 注解

用于基于 Java 配置的 Bean 定义。

```java
@Configuration
public class AppConfig {
    
    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
    
    @Bean("customDataSource")
    public DataSource dataSource() {
        return new HikariDataSource();
    }
    
    @Bean
    public UserController userController(UserService userService) {
        return new UserController(userService);
    }
}
```

### 生命周期注解

用于 Bean 的初始化和销毁回调。

```java
@Component
public class DatabaseService {
    
    @PostConstruct
    public void init() {
        System.out.println("数据库服务初始化");
        // 初始化数据库连接
    }
    
    @PreDestroy
    public void destroy() {
        System.out.println("数据库服务销毁");
        // 关闭数据库连接
    }
}
```

---

## AOP 切面 API

### @Aspect 注解

用于标识切面类。

```java
@Aspect
@Component
public class LoggingAspect {
    // 切面实现
}
```

### 通知注解

#### @Before 前置通知

在目标方法执行前执行。

```java
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("执行方法: " + joinPoint.getSignature().getName());
        System.out.println("方法参数: " + Arrays.toString(joinPoint.getArgs()));
    }
    
    @Before("execution(* com.example.service.UserService.save(..))")
    public void logUserSave(JoinPoint joinPoint) {
        System.out.println("保存用户操作");
    }
}
```

#### @After 后置通知

在目标方法执行后执行（无论是否抛出异常）。

```java
@Aspect
@Component
public class LoggingAspect {
    
    @After("execution(* com.example.service.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("方法执行完成: " + joinPoint.getSignature().getName());
    }
}
```

#### @AfterReturning 返回后通知

在目标方法正常返回后执行。

```java
@Aspect
@Component
public class LoggingAspect {
    
    @AfterReturning(value = "execution(* com.example.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("方法返回值: " + result);
        System.out.println("执行时间: " + System.currentTimeMillis());
    }
}
```

### JoinPoint 接口

提供连接点信息的访问。

```java
@Before("execution(* com.example.service.*.*(..))")
public void logMethodInfo(JoinPoint joinPoint) {
    // 获取目标对象
    Object target = joinPoint.getTarget();
    
    // 获取目标方法
    Method method = joinPoint.getMethod();
    
    // 获取方法参数
    Object[] args = joinPoint.getArgs();
    
    // 获取方法签名
    String signature = joinPoint.getSignature();
    
    // 获取目标类
    Class<?> targetClass = joinPoint.getTargetClass();
    
    System.out.println("目标类: " + targetClass.getName());
    System.out.println("方法名: " + method.getName());
    System.out.println("参数个数: " + args.length);
}
```

### 切点表达式

支持简单的切点表达式语法：

```java
// 匹配所有方法
"execution(* *.*(..))"

// 匹配指定包下的所有方法
"execution(* com.example.service.*.*(..))"

// 匹配指定类的所有方法
"execution(* com.example.service.UserService.*(..))"

// 匹配指定方法名
"execution(* *.save(..))"

// 匹配指定返回类型
"execution(User com.example.service.*.*(..))"
```

---

## MVC 框架 API

### @Controller 注解

用于标识控制器类。

```java
@Controller
public class UserController {
    // 控制器实现
}

@Controller("customController")
public class UserController {
    // 自定义控制器名称
}
```

### @RequestMapping 注解

用于映射 HTTP 请求到控制器方法。

#### 基本用法

```java
@Controller
public class UserController {
    
    // 映射 GET 请求
    @RequestMapping("/users")
    public String listUsers() {
        return "用户列表";
    }
    
    // 指定 HTTP 方法
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser() {
        return "创建用户";
    }
    
    // 支持多种 HTTP 方法
    @RequestMapping(value = "/users/{id}", method = {RequestMethod.GET, RequestMethod.PUT})
    public String handleUser() {
        return "处理用户";
    }
}
```

#### 路径参数

```java
@Controller
public class UserController {
    
    @RequestMapping("/users/{id}")
    public String getUser(Long id) {
        return "用户ID: " + id;
    }
    
    @RequestMapping("/users/{id}/orders/{orderId}")
    public String getUserOrder(Long id, Long orderId) {
        return "用户" + id + "的订单" + orderId;
    }
}
```

#### 请求参数

```java
@Controller
public class UserController {
    
    @RequestMapping("/users")
    public String searchUsers(String name, Integer age) {
        return "搜索用户: name=" + name + ", age=" + age;
    }
    
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser(String name, String email, Integer age) {
        return "创建用户: " + name + " (" + email + ")";
    }
}
```

#### Servlet API 参数

```java
@Controller
public class UserController {
    
    @RequestMapping("/users/{id}")
    public String getUser(Long id, HttpServletRequest request, HttpServletResponse response) {
        // 访问原始的 Servlet API
        String userAgent = request.getHeader("User-Agent");
        response.setHeader("Custom-Header", "value");
        
        return "用户信息";
    }
}
```

### DispatcherServlet

前端控制器，处理所有 HTTP 请求。

#### 基本配置

```java
// 创建 DispatcherServlet
DispatcherServlet servlet = new DispatcherServlet();

// 注册控制器
UserController controller = new UserController();
servlet.registerController(UserController.class, controller);

// 配置到 Web 容器中
// 在 web.xml 或编程方式配置
```

#### 自定义组件

```java
// 自定义处理器映射
HandlerMapping handlerMapping = new RequestMappingHandlerMapping();

// 自定义参数解析器
ParameterResolverComposite parameterResolver = new ParameterResolverComposite();

// 自定义视图解析器
ViewResolver viewResolver = new JsonViewResolver();

// 创建自定义的 DispatcherServlet
DispatcherServlet servlet = new DispatcherServlet(handlerMapping, parameterResolver, viewResolver);
```

### 视图解析

#### 简单视图解析器

```java
@Controller
public class UserController {
    
    @RequestMapping("/users/{id}")
    public String getUser(Long id) {
        // 返回字符串，由 SimpleViewResolver 处理
        return "用户信息: ID=" + id;
    }
}
```

#### JSON 视图解析器

```java
@Controller
public class UserController {
    
    @RequestMapping("/api/users/{id}")
    public User getUser(Long id) {
        // 返回对象，由 JsonViewResolver 转换为 JSON
        return new User(id, "张三", "zhangsan@example.com");
    }
}
```

---

## 工具类 API

### StringUtils

字符串处理工具类。

```java
// 检查字符串是否有内容
boolean hasText = StringUtils.hasText("hello");  // true
boolean hasText2 = StringUtils.hasText("   ");   // false

// 检查字符串是否为空
boolean isEmpty = StringUtils.isEmpty("");       // true
boolean isEmpty2 = StringUtils.isEmpty(null);    // true

// 分割字符串
String[] tokens = StringUtils.tokenizeToStringArray("a,b,c", ",");  // ["a", "b", "c"]

// 首字母大小写转换
String capitalized = StringUtils.capitalize("hello");    // "Hello"
String uncapitalized = StringUtils.uncapitalize("Hello"); // "hello"

// 路径处理
String cleanPath = StringUtils.cleanPath("com\\example\\service");  // "com/example/service"
String shortName = StringUtils.getShortName("com/example/UserService.java");  // "UserService.java"
```

### ClassUtils

类操作工具类。

```java
// 加载类
Class<?> clazz = ClassUtils.forName("com.example.UserService", classLoader);

// 类型兼容性检查
boolean assignable = ClassUtils.isAssignable(String.class, Object.class);  // true
boolean assignable2 = ClassUtils.isAssignable(int.class, Integer.class);   // true

// 获取类的简短名称
String shortName = ClassUtils.getShortName(UserService.class);  // "UserService"
String shortName2 = ClassUtils.getShortName("com.example.UserService");  // "UserService"

// 获取默认类加载器
ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

// 基本类型检查
boolean isPrimitive = ClassUtils.isPrimitiveType(int.class);     // true
boolean isWrapper = ClassUtils.isPrimitiveWrapper(Integer.class); // true

// 类型转换
Class<?> wrapperType = ClassUtils.resolvePrimitiveWrapper(int.class);  // Integer.class
```

### ReflectionUtils

反射操作工具类。

```java
// 查找字段
Field field = ReflectionUtils.findField(UserService.class, "userRepository");
Field field2 = ReflectionUtils.findField(UserService.class, "userRepository", UserRepository.class);

// 查找方法
Method method = ReflectionUtils.findMethod(UserService.class, "findById");
Method method2 = ReflectionUtils.findMethod(UserService.class, "findById", Long.class);

// 设置可访问性
ReflectionUtils.makeAccessible(field);
ReflectionUtils.makeAccessible(method);

// 调用方法
Object result = ReflectionUtils.invokeMethod(method, userService, 1L);

// 字段操作
Object value = ReflectionUtils.getField(field, userService);
ReflectionUtils.setField(field, userService, newValue);

// 获取所有字段和方法
List<Field> allFields = ReflectionUtils.getAllFields(UserService.class);
List<Method> allMethods = ReflectionUtils.getAllMethods(UserService.class);
```

---

## 异常处理

### IoC 容器异常

```java
try {
    UserService userService = context.getBean(UserService.class);
} catch (NoSuchBeanDefinitionException e) {
    // Bean 不存在
    System.err.println("找不到 Bean: " + e.getMessage());
} catch (NoUniqueBeanDefinitionException e) {
    // 找到多个匹配的 Bean
    System.err.println("找到多个匹配的 Bean: " + e.getMessage());
} catch (BeanCreationException e) {
    // Bean 创建失败
    System.err.println("Bean 创建失败: " + e.getMessage());
} catch (CircularDependencyException e) {
    // 循环依赖
    System.err.println("检测到循环依赖: " + e.getMessage());
}
```

### AOP 异常

```java
try {
    // AOP 代理创建
    Object proxy = proxyFactory.createProxy(target, targetClass);
} catch (ProxyCreationException e) {
    // 代理创建失败
    System.err.println("代理创建失败: " + e.getMessage());
} catch (PointcutParsingException e) {
    // 切点表达式解析失败
    System.err.println("切点表达式解析失败: " + e.getMessage());
}
```

### MVC 异常

```java
// DispatcherServlet 会自动处理异常并返回 500 错误响应
// 异常信息会记录到控制台

// 自定义异常处理
@Controller
public class UserController {
    
    @RequestMapping("/users/{id}")
    public String getUser(Long id) {
        try {
            // 业务逻辑
            return "用户信息";
        } catch (Exception e) {
            // 记录异常
            System.err.println("处理用户请求失败: " + e.getMessage());
            return "错误: " + e.getMessage();
        }
    }
}
```

---

## 最佳实践

### 1. 依赖注入最佳实践

```java
// 推荐：构造函数注入（保证依赖不可变）
@Component
public class UserService {
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}

// 避免：字段注入（难以测试）
@Component
public class UserService {
    
    @Autowired
    private UserRepository userRepository;  // 不推荐
}
```

### 2. AOP 最佳实践

```java
// 推荐：明确的切点表达式
@Before("execution(* com.example.service.*Service.*(..))")
public void logServiceMethods(JoinPoint joinPoint) {
    // 只拦截服务层方法
}

// 避免：过于宽泛的切点表达式
@Before("execution(* *.*(..))")  // 不推荐，会拦截所有方法
public void logAllMethods(JoinPoint joinPoint) {
    // 性能影响大
}
```

### 3. MVC 最佳实践

```java
// 推荐：RESTful API 设计
@Controller
public class UserController {
    
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String listUsers() {
        return "用户列表";
    }
    
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public String getUser(Long id) {
        return "用户详情";
    }
    
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser(String name, String email) {
        return "创建用户";
    }
}
```

---

## 性能考虑

### 1. Bean 作用域选择

```java
// 单例模式（默认）- 适用于无状态的服务
@Component
public class UserService {
    // 无状态服务，可以安全地使用单例
}

// 原型模式 - 适用于有状态的对象
@Component
@Scope(Scope.PROTOTYPE)
public class UserSession {
    // 有状态对象，每次获取都创建新实例
}
```

### 2. AOP 性能优化

```java
// 精确的切点表达式减少不必要的拦截
@Before("execution(* com.example.service.*Service.save*(..))")
public void logSaveMethods(JoinPoint joinPoint) {
    // 只拦截保存相关的方法
}
```

### 3. 懒加载

```java
@Component
@Lazy
public class ExpensiveService {
    // 延迟初始化，只有在第一次使用时才创建
}
```

---

这份 API 文档涵盖了简易 Spring 框架的所有核心功能。通过这些 API，你可以构建完整的基于 IoC、AOP 和 MVC 的应用程序。
