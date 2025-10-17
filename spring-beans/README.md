# Spring Beans 模块

## 模块概述

Spring Beans 模块是 IoC（控制反转）容器的核心实现，负责 Bean 的定义、创建、管理和依赖注入。该模块实现了 Spring 框架最重要的特性之一：依赖注入（DI），通过容器管理对象的生命周期和依赖关系，实现了松耦合的组件设计。

### 主要功能
- **Bean 工厂实现**：提供 Bean 的创建和管理机制
- **依赖注入**：支持字段注入、方法注入和构造函数注入
- **生命周期管理**：管理 Bean 的初始化和销毁过程
- **作用域支持**：支持单例和原型作用域
- **循环依赖检测**：检测和处理循环依赖问题

## 项目结构

```
spring-beans/
├── pom.xml                                    # Maven 配置文件
├── README.md                                  # 模块说明文档
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/simplespring/beans/
    │   │       └── factory/                   # Bean 工厂实现
    │   │           ├── config/                # Bean 配置相关
    │   │           │   ├── BeanDefinition.java        # Bean 定义
    │   │           │   ├── BeanPostProcessor.java     # Bean 后处理器
    │   │           │   ├── ConfigurableBeanFactory.java # 可配置 Bean 工厂
    │   │           │   ├── DependencyDescriptor.java   # 依赖描述符
    │   │           │   └── Scope.java                  # 作用域枚举
    │   │           ├── support/               # Bean 工厂支持类
    │   │           │   ├── AbstractBeanFactory.java    # 抽象 Bean 工厂
    │   │           │   ├── DefaultListableBeanFactory.java # 默认 Bean 工厂
    │   │           │   ├── BeanDefinitionRegistry.java  # Bean 定义注册表
    │   │           │   ├── InstantiationStrategy.java   # 实例化策略
    │   │           │   └── SimpleInstantiationStrategy.java # 简单实例化策略
    │   │           ├── BeanFactory.java               # Bean 工厂接口
    │   │           ├── BeanCreationException.java     # Bean 创建异常
    │   │           ├── BeanNotOfRequiredTypeException.java # 类型不匹配异常
    │   │           ├── CircularDependencyException.java    # 循环依赖异常
    │   │           ├── NoSuchBeanDefinitionException.java  # Bean 不存在异常
    │   │           └── NoUniqueBeanDefinitionException.java # Bean 不唯一异常
    │   └── resources/
    │       └── .gitkeep
    └── test/
        └── java/
            └── com/simplespring/beans/        # 单元测试
```

## 技术栈与依赖

### 核心技术
- **Java 1.7+**：基础运行环境
- **反射机制**：动态创建对象和注入依赖
- **泛型支持**：类型安全的 Bean 获取
- **注解处理**：基于注解的依赖注入

### 主要依赖
```xml
<dependencies>
    <!-- 内部依赖 -->
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>spring-core</artifactId>
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
- spring-core 模块（自动依赖）

### 2. 编译模块
```bash
# 进入模块目录
cd spring-beans

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
    <artifactId>spring-beans</artifactId>
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
    ├── spring-context (使用 BeanFactory)
    ├── spring-aop (使用 Bean 创建机制)
    ├── spring-webmvc (使用依赖注入)
    └── spring-example (使用完整功能)
```

### 核心接口与实现

#### 1. BeanFactory 接口
```java
public interface BeanFactory {
    Object getBean(String name);
    <T> T getBean(String name, Class<T> requiredType);
    <T> T getBean(Class<T> requiredType);
    boolean containsBean(String name);
    boolean isSingleton(String name);
    Class<?> getType(String name);
}
```

#### 2. BeanDefinition 类
```java
public class BeanDefinition {
    private Class<?> beanClass;
    private Scope scope;
    private String initMethodName;
    private String destroyMethodName;
    private List<DependencyDescriptor> dependencies;
}
```

## 示例与使用说明

### 1. 基本 Bean 工厂使用
```java
// 创建 Bean 工厂
DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

// 注册 Bean 定义
BeanDefinition userServiceDef = new BeanDefinition();
userServiceDef.setBeanClass(UserService.class);
userServiceDef.setScope(Scope.SINGLETON);
beanFactory.registerBeanDefinition("userService", userServiceDef);

// 获取 Bean 实例
UserService userService = beanFactory.getBean("userService", UserService.class);
UserService sameInstance = beanFactory.getBean(UserService.class);
```

### 2. 依赖注入示例
```java
// 定义带依赖的服务类
@Component
public class OrderService {
    
    @Autowired
    private UserService userService;  // 字段注入
    
    private PaymentService paymentService;
    
    @Autowired
    public void setPaymentService(PaymentService paymentService) {  // 方法注入
        this.paymentService = paymentService;
    }
    
    @Autowired
    public OrderService(NotificationService notificationService) {  // 构造函数注入
        this.notificationService = notificationService;
    }
}
```

### 3. Bean 生命周期管理
```java
@Component
public class DatabaseService {
    
    private Connection connection;
    
    @PostConstruct
    public void init() {
        // 初始化数据库连接
        connection = DriverManager.getConnection("jdbc:h2:mem:test");
        System.out.println("数据库连接已建立");
    }
    
    @PreDestroy
    public void destroy() {
        // 关闭数据库连接
        if (connection != null) {
            connection.close();
            System.out.println("数据库连接已关闭");
        }
    }
}
```

### 4. 作用域配置
```java
// 单例作用域（默认）
@Component
public class ConfigService {
    // 整个应用只有一个实例
}

// 原型作用域
@Component
@Scope("prototype")
public class TaskProcessor {
    // 每次获取都创建新实例
}
```

### 5. 循环依赖处理
```java
// 服务 A
@Component
public class ServiceA {
    @Autowired
    private ServiceB serviceB;  // 依赖服务 B
}

// 服务 B
@Component
public class ServiceB {
    @Autowired
    private ServiceA serviceA;  // 依赖服务 A
}

// Bean 工厂会检测并抛出 CircularDependencyException
```

## 核心功能详解

### 1. Bean 创建流程
```
1. 检查 Bean 定义是否存在
2. 检查是否为单例且已创建
3. 创建 Bean 实例（通过反射或工厂方法）
4. 解析和注入依赖
5. 调用初始化方法（@PostConstruct）
6. 应用 Bean 后处理器
7. 返回完整的 Bean 实例
```

### 2. 依赖注入类型

#### 字段注入
```java
@Component
public class UserController {
    @Autowired
    private UserService userService;  // 直接注入到字段
}
```

#### 方法注入
```java
@Component
public class UserController {
    private UserService userService;
    
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
```

#### 构造函数注入
```java
@Component
public class UserController {
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
}
```

### 3. Bean 后处理器
```java
public class CustomBeanPostProcessor implements BeanPostProcessor {
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        // 初始化前处理
        System.out.println("初始化前处理: " + beanName);
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // 初始化后处理
        System.out.println("初始化后处理: " + beanName);
        return bean;
    }
}
```

## 维护与扩展建议

### 1. 添加新的作用域
```java
// 扩展 Scope 枚举
public enum Scope {
    SINGLETON,
    PROTOTYPE,
    REQUEST,    // 新增：请求作用域
    SESSION     // 新增：会话作用域
}

// 在 BeanFactory 中实现相应逻辑
```

### 2. 自定义实例化策略
```java
public class CustomInstantiationStrategy implements InstantiationStrategy {
    
    @Override
    public Object instantiate(BeanDefinition beanDefinition) {
        // 实现自定义的实例化逻辑
        // 例如：使用工厂方法、代理对象等
        return null;
    }
}
```

### 3. 扩展依赖注入功能
- 支持 `@Qualifier` 注解进行精确匹配
- 支持 `@Value` 注解注入配置值
- 支持集合类型的依赖注入

### 4. 性能优化建议
- **Bean 定义缓存**：缓存解析后的 Bean 定义
- **依赖关系缓存**：缓存依赖注入的字段和方法
- **延迟初始化**：支持 `@Lazy` 注解延迟创建 Bean
- **并发优化**：使用 `ConcurrentHashMap` 提高并发性能

### 5. 错误处理改进
```java
// 提供更详细的错误信息
public class BeanCreationException extends RuntimeException {
    private final String beanName;
    private final Class<?> beanClass;
    
    public BeanCreationException(String beanName, Class<?> beanClass, Throwable cause) {
        super(String.format("创建 Bean '%s' (类型: %s) 时发生错误", 
              beanName, beanClass.getName()), cause);
        this.beanName = beanName;
        this.beanClass = beanClass;
    }
}
```

### 6. 测试建议
- 测试各种依赖注入场景
- 测试 Bean 生命周期回调
- 测试循环依赖检测
- 测试异常情况处理
- 性能测试和内存泄漏检测

## 常见问题

### Q: 如何解决循环依赖问题？
A: 
1. 使用字段注入而不是构造函数注入
2. 使用 `@Lazy` 注解延迟初始化
3. 重新设计类结构，消除循环依赖

### Q: Bean 创建失败怎么调试？
A: 
1. 检查类是否有无参构造函数
2. 确认依赖的 Bean 是否已注册
3. 查看详细的异常堆栈信息
4. 启用 DEBUG 日志查看创建过程

### Q: 如何实现条件化的 Bean 创建？
A: 
```java
// 可以扩展 BeanDefinition 添加条件判断
public class ConditionalBeanDefinition extends BeanDefinition {
    private Condition condition;
    
    public boolean shouldCreate() {
        return condition == null || condition.matches();
    }
}
```

### Q: 内存泄漏如何预防？
A: 
1. 正确实现 `@PreDestroy` 方法清理资源
2. 避免在单例 Bean 中持有原型 Bean 的引用
3. 及时清理事件监听器和回调函数
4. 使用弱引用处理缓存

---

**注意**：Spring Beans 模块是 IoC 容器的核心，任何修改都可能影响整个应用的 Bean 管理。建议在修改前充分测试，确保向后兼容性。
