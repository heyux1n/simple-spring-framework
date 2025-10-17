# Spring Core 模块

## 模块概述

Spring Core 是整个简易 Spring 框架的基础模块，提供了框架运行所需的核心工具类、注解定义、类型转换系统和资源访问机制。该模块是其他所有模块的基础依赖，为 IoC 容器、AOP 和 Web MVC 等功能提供底层支持。

### 主要功能
- **核心注解定义**：提供 @Component、@Autowired、@Controller 等基础注解
- **类型转换系统**：支持基本数据类型之间的自动转换
- **资源访问机制**：统一的资源加载和访问接口
- **反射工具类**：简化反射操作的工具方法
- **字符串工具类**：常用的字符串处理方法

## 项目结构

```
spring-core/
├── pom.xml                                    # Maven 配置文件
├── README.md                                  # 模块说明文档
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/simplespring/core/
    │   │       ├── annotation/                # 核心注解定义
    │   │       │   ├── After.java            # AOP 后置通知注解
    │   │       │   ├── AfterReturning.java   # AOP 返回后通知注解
    │   │       │   ├── Aspect.java           # AOP 切面注解
    │   │       │   ├── Autowired.java        # 依赖注入注解
    │   │       │   ├── Bean.java             # Bean 定义注解
    │   │       │   ├── Before.java           # AOP 前置通知注解
    │   │       │   ├── Component.java        # 组件注解
    │   │       │   ├── Configuration.java    # 配置类注解
    │   │       │   ├── Controller.java       # 控制器注解
    │   │       │   ├── PostConstruct.java    # 初始化方法注解
    │   │       │   ├── PreDestroy.java       # 销毁方法注解
    │   │       │   ├── RequestMapping.java   # 请求映射注解
    │   │       │   └── RequestMethod.java    # HTTP 方法枚举
    │   │       ├── convert/                   # 类型转换系统
    │   │       │   ├── ConversionService.java      # 转换服务接口
    │   │       │   ├── DefaultTypeConverter.java   # 默认类型转换器
    │   │       │   ├── TypeConverter.java          # 类型转换器接口
    │   │       │   ├── TypeDescriptor.java         # 类型描述符
    │   │       │   └── TypeMismatchException.java  # 类型不匹配异常
    │   │       ├── io/                        # 资源访问系统
    │   │       │   ├── ClassPathResource.java      # 类路径资源
    │   │       │   ├── DefaultResourceLoader.java  # 默认资源加载器
    │   │       │   ├── Resource.java               # 资源接口
    │   │       │   └── ResourceLoader.java         # 资源加载器接口
    │   │       └── util/                      # 工具类
    │   │           ├── ClassUtils.java             # 类操作工具
    │   │           ├── ReflectionUtils.java        # 反射工具
    │   │           └── StringUtils.java            # 字符串工具
    │   └── resources/
    │       └── .gitkeep
    └── test/
        └── java/
            └── com/simplespring/core/         # 单元测试
```

## 技术栈与依赖

### 核心技术
- **Java 1.7+**：基础运行环境
- **反射机制**：动态类操作和方法调用
- **注解处理**：运行时注解解析和处理

### 主要依赖
```xml
<dependencies>
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
</dependencies>
```

## 环境配置与运行步骤

### 1. 环境要求
- JDK 1.7 或更高版本
- Apache Maven 3.x

### 2. 编译模块
```bash
# 进入模块目录
cd spring-core

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
    <artifactId>spring-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 模块交互与依赖关系

### 依赖关系图
```
spring-core (基础模块)
    ↑
    ├── spring-beans (依赖 core)
    ├── spring-context (依赖 core)
    ├── spring-aop (依赖 core)
    ├── spring-webmvc (依赖 core)
    └── spring-example (依赖 core)
```

### 对外提供的核心接口

#### 1. 注解系统
- **@Component**：标记组件类，用于自动扫描和注册
- **@Autowired**：标记需要依赖注入的字段、方法或构造函数
- **@Controller**：标记 Web 控制器类
- **@RequestMapping**：标记请求映射方法

#### 2. 类型转换服务
```java
// 使用类型转换器
TypeConverter converter = new DefaultTypeConverter();
Integer result = converter.convert("123", Integer.class);
```

#### 3. 资源访问服务
```java
// 加载类路径资源
ResourceLoader loader = new DefaultResourceLoader();
Resource resource = loader.getResource("classpath:config.properties");
InputStream inputStream = resource.getInputStream();
```

#### 4. 工具类服务
```java
// 反射工具使用
Method method = ReflectionUtils.findMethod(UserService.class, "findById", Long.class);
Object result = ReflectionUtils.invokeMethod(method, userService, 1L);

// 字符串工具使用
boolean hasText = StringUtils.hasText("hello");
String[] tokens = StringUtils.tokenizeToStringArray("a,b,c", ",");
```

## 示例与使用说明

### 1. 定义自定义注解
```java
// 创建自定义组件注解
@Component
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
}
```

### 2. 使用类型转换
```java
// 创建类型转换器
DefaultTypeConverter converter = new DefaultTypeConverter();

// 字符串转数字
Integer age = converter.convert("25", Integer.class);
Long id = converter.convert("123", Long.class);
Boolean active = converter.convert("true", Boolean.class);

// 处理转换异常
try {
    Integer invalid = converter.convert("abc", Integer.class);
} catch (TypeMismatchException e) {
    System.out.println("类型转换失败: " + e.getMessage());
}
```

### 3. 资源加载示例
```java
// 加载配置文件
ResourceLoader loader = new DefaultResourceLoader();
Resource configResource = loader.getResource("classpath:application.properties");

if (configResource.exists()) {
    Properties props = new Properties();
    props.load(configResource.getInputStream());
    String value = props.getProperty("app.name");
}
```

### 4. 反射工具使用
```java
// 查找和调用方法
Class<?> clazz = UserService.class;
Method method = ReflectionUtils.findMethod(clazz, "findById", Long.class);

if (method != null) {
    Object result = ReflectionUtils.invokeMethod(method, userServiceInstance, 1L);
}

// 设置字段值
Field field = ReflectionUtils.findField(clazz, "userRepository");
ReflectionUtils.makeAccessible(field);
ReflectionUtils.setField(field, userServiceInstance, repositoryInstance);
```

## 维护与扩展建议

### 1. 添加新注解
```java
// 在 annotation 包中创建新注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String value() default "";
}
```

### 2. 扩展类型转换器
```java
// 实现自定义类型转换器
public class CustomTypeConverter implements TypeConverter {
    @Override
    public <T> T convert(Object source, Class<T> targetType) {
        // 实现自定义转换逻辑
        return null;
    }
}
```

### 3. 添加新工具类
- 在 `util` 包中添加新的工具类
- 保持工具方法的静态性和无状态性
- 提供完整的 JavaDoc 文档

### 4. 性能优化建议
- **缓存反射结果**：避免重复的反射操作
- **延迟初始化**：只在需要时创建对象
- **异常处理**：合理处理和包装底层异常

### 5. 测试建议
- 为每个工具类编写完整的单元测试
- 测试边界条件和异常情况
- 保持测试覆盖率在 80% 以上

### 6. 调试技巧
- 启用 DEBUG 级别日志查看详细信息
- 使用 IDE 的调试功能跟踪反射调用
- 检查类路径和资源加载路径

## 常见问题

### Q: 如何添加对新数据类型的转换支持？
A: 在 `DefaultTypeConverter` 中添加新的转换逻辑，或者实现自定义的 `TypeConverter`。

### Q: 资源加载失败怎么办？
A: 检查资源路径是否正确，确保资源文件在类路径中，使用 `Resource.exists()` 方法验证。

### Q: 反射操作性能如何优化？
A: 缓存 `Method` 和 `Field` 对象，避免重复查找；使用 `MethodHandle` 替代反射调用。

### Q: 如何处理类加载问题？
A: 使用 `ClassUtils.getDefaultClassLoader()` 获取合适的类加载器，注意线程上下文类加载器的使用。

---

**注意**：Spring Core 模块是整个框架的基础，修改时需要考虑对其他模块的影响。建议在修改前运行完整的测试套件。
