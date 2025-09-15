# 设计文档

## 概述

本项目将实现一个简易的 Spring 框架，包含 IoC 容器、依赖注入、AOP 和 MVC 的核心功能。设计遵循 Spring 框架的核心设计理念，采用注解驱动的方式，提供清晰的架构和易于理解的实现。

## 架构

### 整体架构图

```
简易 Spring 框架
├── spring-core (核心工具和基础设施)
│   ├── 工具类 (StringUtils, ClassUtils, ReflectionUtils)
│   ├── 类型转换 (TypeConverter, ConversionService)
│   ├── 资源访问 (Resource, ResourceLoader)
│   └── 环境抽象 (Environment, PropertySource)
├── spring-beans (Bean 定义和工厂)
│   ├── BeanDefinition
│   ├── BeanFactory
│   └── Bean 注册表
├── spring-context (应用上下文)
│   ├── ApplicationContext
│   ├── 注解处理
│   └── 组件扫描
├── spring-aop (面向切面编程)
│   ├── 切面处理
│   ├── 代理生成
│   └── 通知执行
├── spring-webmvc (Web MVC 框架)
│   ├── DispatcherServlet
│   ├── 请求映射
│   └── 视图解析
└── spring-example (示例应用)
    ├── 业务服务
    ├── 控制器
    └── 配置类
```

### 模块依赖关系

- spring-beans 依赖 spring-core
- spring-context 依赖 spring-core 和 spring-beans
- spring-aop 依赖 spring-core、spring-beans 和 spring-context
- spring-webmvc 依赖 spring-core、spring-beans 和 spring-context
- spring-example 依赖所有核心模块

## 组件和接口

### 1. spring-core 核心工具组件

#### 工具类
```java
public class StringUtils {
    public static boolean hasText(String str);
    public static boolean isEmpty(String str);
    public static String[] tokenizeToStringArray(String str, String delimiters);
}

public class ClassUtils {
    public static Class<?> forName(String name, ClassLoader classLoader);
    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType);
    public static String getShortName(Class<?> clazz);
}

public class ReflectionUtils {
    public static Field findField(Class<?> clazz, String name);
    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes);
    public static void makeAccessible(Field field);
    public static void makeAccessible(Method method);
}
```

#### 类型转换接口
```java
public interface TypeConverter {
    <T> T convertIfNecessary(Object value, Class<T> requiredType);
    boolean canConvert(Class<?> sourceType, Class<?> targetType);
}

public interface ConversionService extends TypeConverter {
    boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);
    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
}
```

#### 资源访问接口
```java
public interface Resource {
    boolean exists();
    InputStream getInputStream() throws IOException;
    String getFilename();
    String getDescription();
}

public interface ResourceLoader {
    Resource getResource(String location);
    ClassLoader getClassLoader();
}
```

### 2. spring-beans 核心组件

#### ApplicationContext 接口
```java
public interface ApplicationContext {
    <T> T getBean(Class<T> clazz);
    <T> T getBean(String name, Class<T> clazz);
    Object getBean(String name);
    boolean containsBean(String name);
    void refresh();
}
```

#### BeanDefinition 类
```java
public class BeanDefinition {
    private Class<?> beanClass;
    private String beanName;
    private Scope scope;
    private boolean isSingleton;
    private Constructor<?> constructor;
    private List<Field> autowiredFields;
    private List<Method> autowiredMethods;
}
```

#### BeanFactory 接口
```java
public interface BeanFactory {
    Object createBean(BeanDefinition beanDefinition);
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
    BeanDefinition getBeanDefinition(String beanName);
}
```

### 3. 注解定义

#### 核心注解
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default "";
}

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    boolean required() default true;
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    String value() default "";
}
```

#### AOP 注解
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
    String value();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface After {
    String value();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterReturning {
    String value();
    String returning() default "";
}
```

#### MVC 注解
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String value() default "";
}

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value() default "";
    RequestMethod method() default RequestMethod.GET;
}
```

### 4. AOP 组件设计

#### AspectProcessor 类
```java
public class AspectProcessor {
    private List<AspectDefinition> aspects;
    
    public Object createProxy(Object target, Class<?> targetClass);
    public void registerAspect(Object aspectInstance);
    private boolean shouldProxy(Class<?> targetClass);
    private List<MethodInterceptor> getInterceptors(Method method);
}
```

#### JoinPoint 接口
```java
public interface JoinPoint {
    Object getTarget();
    Method getMethod();
    Object[] getArgs();
    String getSignature();
}
```

### 5. MVC 组件设计

#### DispatcherServlet 类
```java
public class DispatcherServlet {
    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;
    private ViewResolver viewResolver;
    
    public void doDispatch(HttpServletRequest request, HttpServletResponse response);
}
```

#### HandlerMapping 接口
```java
public interface HandlerMapping {
    HandlerExecutionChain getHandler(HttpServletRequest request);
}
```

## 数据模型

### Bean 注册表
```java
public class BeanRegistry {
    // 单例 Bean 缓存
    private Map<String, Object> singletonBeans = new ConcurrentHashMap<>();
    
    // Bean 定义缓存
    private Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();
    
    // Bean 名称到类型的映射
    private Map<String, Class<?>> beanNameToType = new ConcurrentHashMap<>();
    
    // 类型到 Bean 名称的映射
    private Map<Class<?>, Set<String>> typeToBeanNames = new ConcurrentHashMap<>();
}
```

### 切面定义模型
```java
public class AspectDefinition {
    private Object aspectInstance;
    private Class<?> aspectClass;
    private List<AdviceDefinition> advices;
    
    public static class AdviceDefinition {
        private Method adviceMethod;
        private AdviceType type;
        private String pointcutExpression;
        private String returningParameter;
    }
}
```

### 请求映射模型
```java
public class RequestMappingInfo {
    private String path;
    private RequestMethod method;
    private Method handlerMethod;
    private Object controller;
    private Class<?>[] parameterTypes;
}
```

## 错误处理

### 1. IoC 容器错误处理
- **循环依赖检测**: 在 Bean 创建过程中检测循环依赖，抛出 `CircularDependencyException`
- **Bean 未找到**: 当请求的 Bean 不存在时，抛出 `NoSuchBeanDefinitionException`
- **依赖注入失败**: 当无法注入依赖时，抛出 `DependencyInjectionException`

### 2. AOP 错误处理
- **切点表达式解析错误**: 抛出 `PointcutParsingException`
- **代理创建失败**: 抛出 `ProxyCreationException`
- **通知执行异常**: 捕获并记录通知方法中的异常

### 3. MVC 错误处理
- **请求映射冲突**: 检测重复的请求映射，抛出 `DuplicateMappingException`
- **参数绑定失败**: 抛出 `ParameterBindingException`
- **视图解析失败**: 抛出 `ViewResolutionException`

## 测试策略

### 1. 单元测试
- **IoC 容器测试**: 测试 Bean 的创建、依赖注入、生命周期管理
- **AOP 功能测试**: 测试切面的创建、通知的执行、代理的生成
- **MVC 组件测试**: 测试请求路由、参数绑定、响应处理

### 2. 集成测试
- **端到端测试**: 测试完整的请求处理流程
- **多模块协作测试**: 测试 IoC、AOP、MVC 模块之间的协作
- **示例应用测试**: 验证示例应用的各项功能

### 3. 性能测试
- **Bean 创建性能**: 测试大量 Bean 创建的性能
- **AOP 代理性能**: 测试代理方法调用的性能开销
- **请求处理性能**: 测试 MVC 请求处理的性能

## 技术栈和环境要求

### 开发环境
- **JDK 版本**: JDK 1.7 (兼容性要求)
- **构建工具**: Apache Maven 3.x
- **测试框架**: JUnit 4.x (JDK 1.7 兼容)
- **日志框架**: SLF4J + Logback

### Maven 项目结构
```
simple-spring-framework/
├── pom.xml (父 POM)
├── spring-core/
│   └── pom.xml
├── spring-beans/
│   └── pom.xml
├── spring-context/
│   └── pom.xml
├── spring-aop/
│   └── pom.xml
├── spring-webmvc/
│   └── pom.xml
└── spring-example/
    └── pom.xml
```

### 核心依赖 (JDK 1.7 兼容版本)
```xml
<properties>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <junit.version>4.12</junit.version>
    <slf4j.version>1.7.25</slf4j.version>
    <logback.version>1.2.3</logback.version>
    <cglib.version>3.2.5</cglib.version>
    <servlet.api.version>3.1.0</servlet.api.version>
</properties>
```

## 实现细节

### 1. JDK 1.7 兼容性考虑
- **泛型使用**: 避免使用 JDK 1.8+ 的泛型推断特性
- **集合操作**: 使用传统的 for 循环和迭代器，避免 Stream API
- **字符串处理**: 使用 StringBuilder 而非 String.join()
- **异常处理**: 使用传统的 try-catch，避免 try-with-resources 的多资源语法

### 2. 包扫描机制 (JDK 1.7 实现)
```java
// 使用 JDK 1.7 兼容的方式进行包扫描
public class ClassPathScanner {
    public Set<Class<?>> scanPackage(String basePackage) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        String packagePath = basePackage.replace('.', '/');
        try {
            Enumeration<URL> resources = Thread.currentThread()
                .getContextClassLoader().getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                classes.addAll(findClasses(new File(resource.getFile()), basePackage));
            }
        } catch (IOException e) {
            throw new RuntimeException("包扫描失败", e);
        }
        return classes;
    }
}
```

### 3. 依赖注入实现
- **构造函数注入**: 通过反射获取构造函数参数类型，从容器中获取对应的 Bean 实例
- **字段注入**: 扫描带有 `@Autowired` 注解的字段，使用反射设置字段值
- **方法注入**: 扫描带有 `@Autowired` 注解的方法，调用方法进行依赖注入

### 4. AOP 代理实现 (JDK 1.7 兼容)
- **JDK 动态代理**: 对实现接口的类使用 JDK 动态代理
- **CGLIB 代理**: 使用 CGLIB 3.2.5 版本 (JDK 1.7 兼容) 生成子类代理
- **切点匹配**: 实现简单的切点表达式解析，支持方法名匹配和包路径匹配

### 5. MVC 请求处理流程
1. **请求接收**: DispatcherServlet 接收 HTTP 请求
2. **处理器映射**: 根据请求路径和方法找到对应的控制器方法
3. **参数解析**: 解析请求参数并绑定到方法参数
4. **方法调用**: 调用控制器方法处理业务逻辑
5. **视图渲染**: 根据返回值渲染视图并返回响应

### 6. 配置支持
- **注解配置**: 支持 `@Configuration` 和 `@Bean` 注解进行配置
- **属性文件**: 支持从 properties 文件读取配置信息
- **环境变量**: 支持从环境变量获取配置值

### 7. JDK 1.7 代码风格示例
```java
// 使用传统的泛型声明
Map<String, Object> beanMap = new HashMap<String, Object>();

// 使用传统的 for 循环
for (BeanDefinition definition : beanDefinitions) {
    // 处理逻辑
}

// 使用 StringBuilder 进行字符串拼接
StringBuilder sb = new StringBuilder();
sb.append("Bean name: ").append(beanName);
String result = sb.toString();
```

这个设计确保了框架在 JDK 1.7 环境下的完全兼容性，使用 Maven 进行依赖管理，同时保持了与 Spring 框架核心概念的一致性。
