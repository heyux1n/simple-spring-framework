# Spring Context 模块

## 模块概述

Spring Context 模块是应用上下文的实现，它在 Spring Beans 模块的基础上提供了更高级的容器功能。该模块实现了完整的 IoC 容器，包括组件自动扫描、注解驱动的配置、AOP 集成等功能，是连接各个模块的核心枢纽。

### 主要功能
- **应用上下文管理**：提供完整的应用程序运行环境
- **组件自动扫描**：自动发现和注册带注解的组件
- **注解配置支持**：基于注解的配置和依赖注入
- **AOP 集成**：自动应用切面到匹配的 Bean
- **生命周期管理**：管理应用程序和 Bean 的完整生命周期

## 项目结构

```
spring-context/
├── pom.xml                                    # Maven 配置文件
├── README.md                                  # 模块说明文档
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/simplespring/context/
    │   │       ├── ApplicationContext.java           # 应用上下文接口
    │   │       ├── AnnotationConfigApplicationContext.java # 注解配置应用上下文
    │   │       ├── ClassPathScanner.java             # 类路径扫描器
    │   │       └── AspectProcessor.java              # 切面处理器
    │   └── resources/
    │       └── .gitkeep
    └── test/
        └── java/
            └── com/simplespring/context/      # 单元测试
```

## 技术栈与依赖

### 核心技术
- **Java 1.7+**：基础运行环境
- **注解处理**：运行时注解扫描和处理
- **反射机制**：动态类加载和实例化
- **包扫描**：递归扫描指定包下的类文件
- **AOP 集成**：自动代理和切面应用

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
        <artifactId>spring-aop</artifactId>
        <version>1.0.0</version>
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
- spring-core、spring-beans、spring-aop 模块（自动依赖）

### 2. 编译模块
```bash
# 进入模块目录
cd spring-context

# 编译源代码
mvn clean compile

# 运行测试
mvn test

# 打包 JAR 文件
mvn package
```

### 3. 在其他项目中使用
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>spring-context</artifactId>
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
spring-aop
    ↑
spring-context (集成模块)
    ↑
    ├── spring-webmvc (使用应用上下文)
    └── spring-example (使用完整功能)
```

### 核心组件交互
```
ApplicationContext
    ├── 使用 ClassPathScanner 扫描组件
    ├── 使用 BeanFactory 管理 Bean
    ├── 使用 AspectProcessor 处理切面
    └── 协调各模块协同工作
```

## 示例与使用说明

### 1. 创建应用上下文
```java
// 基于包扫描创建上下文
ApplicationContext context = new AnnotationConfigApplicationContext("com.example.service");

// 基于配置类创建上下文
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

// 扫描多个包
ApplicationContext context = new AnnotationConfigApplicationContext(
    "com.example.service", "com.example.repository"
);
```

### 2. 获取和使用 Bean
```java
// 通过类型获取 Bean
UserService userService = context.getBean(UserService.class);

// 通过名称获取 Bean
UserService userService = context.getBean("userService", UserService.class);

// 检查 Bean 是否存在
if (context.containsBean("userService")) {
    UserService userService = context.getBean(UserService.class);
}

// 获取所有指定类型的 Bean
Map<String, UserService> userServices = context.getBeansOfType(UserService.class);
```

### 3. 组件定义和扫描
```java
// 定义服务组件
@Component
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
}

// 定义仓库组件
@Component
public class UserRepository {
    
    public User findById(Long id) {
        // 模拟数据库查询
        return new User(id, "用户" + id);
    }
}

// 定义控制器组件
@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @RequestMapping("/users/{id}")
    public String getUser(Long id) {
        User user = userService.findById(id);
        return "用户信息: " + user.getName();
    }
}
```

### 4. 配置类使用
```java
@Configuration
public class AppConfig {
    
    @Bean
    public DataSource dataSource() {
        // 创建数据源
        return new HikariDataSource();
    }
    
    @Bean
    public UserService userService() {
        return new UserService();
    }
}

// 使用配置类
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
DataSource dataSource = context.getBean(DataSource.class);
```

### 5. AOP 自动集成
```java
// 定义切面
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("执行方法: " + joinPoint.getSignature().getName());
    }
    
    @AfterReturning(value = "execution(* com.example.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("方法返回: " + result);
    }
}

// 创建上下文时，切面会自动应用
ApplicationContext context = new AnnotationConfigApplicationContext("com.example");
UserService userService = context.getBean(UserService.class);
userService.findById(1L); // 会触发切面逻辑
```

## 核心功能详解

### 1. 应用上下文生命周期
```
1. 创建 BeanFactory
2. 扫描指定包路径
3. 注册 Bean 定义
4. 处理配置类
5. 创建和初始化 Bean
6. 应用 AOP 切面
7. 调用初始化回调
8. 上下文就绪
```

### 2. 组件扫描流程
```java
public class ClassPathScanner {
    
    public Set<Class<?>> scan(String... basePackages) {
        Set<Class<?>> candidates = new HashSet<Class<?>>();
        
        for (String basePackage : basePackages) {
            // 1. 扫描包路径下的所有类
            Set<Class<?>> classes = findCandidateClasses(basePackage);
            
            // 2. 过滤带有组件注解的类
            for (Class<?> clazz : classes) {
                if (isComponent(clazz)) {
                    candidates.add(clazz);
                }
            }
        }
        
        return candidates;
    }
    
    private boolean isComponent(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) ||
               clazz.isAnnotationPresent(Controller.class) ||
               clazz.isAnnotationPresent(Configuration.class);
    }
}
```

### 3. Bean 定义注册
```java
public void registerBeanDefinitions(Set<Class<?>> componentClasses) {
    for (Class<?> clazz : componentClasses) {
        // 创建 Bean 定义
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClass(clazz);
        
        // 确定 Bean 名称
        String beanName = determineBeanName(clazz);
        
        // 注册到 BeanFactory
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }
}
```

### 4. AOP 集成处理
```java
public class AspectProcessor {
    
    public void processAspects(ApplicationContext context) {
        // 1. 查找所有切面 Bean
        Map<String, Object> aspectBeans = context.getBeansWithAnnotation(Aspect.class);
        
        // 2. 为每个切面创建代理
        for (Object aspectBean : aspectBeans.values()) {
            processAspect(aspectBean, context);
        }
    }
    
    private void processAspect(Object aspectBean, ApplicationContext context) {
        // 解析切面定义
        AspectDefinition aspectDef = parseAspectDefinition(aspectBean);
        
        // 应用到匹配的 Bean
        applyAspectToBeans(aspectDef, context);
    }
}
```

## 维护与扩展建议

### 1. 扩展组件扫描功能
```java
// 支持自定义过滤器
public interface ComponentFilter {
    boolean matches(Class<?> clazz);
}

// 支持排除特定类
@ComponentScan(basePackages = "com.example", 
               excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, 
                                       classes = TestConfiguration.class))
```

### 2. 添加环境配置支持
```java
public class Environment {
    private Properties properties;
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}

// 在应用上下文中集成
@Component
public class ConfigService {
    @Autowired
    private Environment environment;
    
    public String getDatabaseUrl() {
        return environment.getProperty("database.url", "jdbc:h2:mem:test");
    }
}
```

### 3. 支持条件化 Bean 创建
```java
@Conditional(DatabaseCondition.class)
@Component
public class DatabaseService {
    // 只有在满足数据库条件时才创建
}

public class DatabaseCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context) {
        return context.getEnvironment().getProperty("database.enabled", "false").equals("true");
    }
}
```

### 4. 性能优化建议
- **并行扫描**：使用多线程并行扫描大型包结构
- **缓存机制**：缓存扫描结果和 Bean 定义
- **延迟初始化**：支持 `@Lazy` 注解延迟创建 Bean
- **启动优化**：优化应用上下文的启动时间

### 5. 监控和诊断
```java
public class ContextDiagnostics {
    
    public void printBeanInfo(ApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        System.out.println("注册的 Bean 数量: " + beanNames.length);
        
        for (String beanName : beanNames) {
            Class<?> beanType = context.getType(beanName);
            boolean singleton = context.isSingleton(beanName);
            System.out.printf("Bean: %s, 类型: %s, 单例: %s%n", 
                            beanName, beanType.getSimpleName(), singleton);
        }
    }
}
```

### 6. 测试支持
```java
// 测试专用的应用上下文
public class TestApplicationContext extends AnnotationConfigApplicationContext {
    
    public TestApplicationContext(String... basePackages) {
        super(basePackages);
    }
    
    public <T> void registerMockBean(Class<T> beanClass, T mockInstance) {
        // 注册模拟对象
        getBeanFactory().registerSingleton(
            determineBeanName(beanClass), mockInstance);
    }
}
```

## 常见问题

### Q: 组件扫描不到类怎么办？
A: 
1. 检查包路径是否正确
2. 确认类上有 `@Component` 等注解
3. 检查类是否为 public
4. 确认类在类路径中

### Q: 循环依赖如何处理？
A: 
1. 使用字段注入而不是构造函数注入
2. 使用 `@Lazy` 注解延迟初始化
3. 重新设计类结构消除循环依赖

### Q: AOP 不生效怎么调试？
A: 
1. 确认切面类有 `@Aspect` 和 `@Component` 注解
2. 检查切点表达式是否正确
3. 确认目标方法是 public 的
4. 检查是否通过容器获取的 Bean

### Q: 应用启动慢如何优化？
A: 
1. 减少扫描的包范围
2. 使用 `@Lazy` 延迟初始化非关键 Bean
3. 优化 Bean 的依赖关系
4. 使用并行初始化

### Q: 内存使用过多怎么办？
A: 
1. 检查是否有内存泄漏
2. 合理使用原型作用域
3. 及时清理不需要的 Bean
4. 监控 Bean 的创建数量

---

**注意**：Spring Context 模块是整个框架的协调中心，修改时需要考虑对所有其他模块的影响。建议在生产环境使用前进行充分的集成测试。
