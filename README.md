# 简易 Spring 框架学习项目

## 项目介绍

本项目是一个简化版的 Spring 框架实现，旨在帮助开发者深入理解 Spring 框架的核心设计思想和实现原理。通过实现 Spring 的核心功能模块，学习者可以更好地掌握以下概念：

- **IoC 容器和依赖注入**：理解控制反转的设计理念，掌握依赖注入的实现机制
- **面向切面编程（AOP）**：学习横切关注点的处理方式，理解代理模式的应用
- **MVC 框架**：了解 Web 请求的处理流程，掌握前端控制器模式
- **注解驱动开发**：体验现代 Spring 开发的便捷性

### 功能特性

✅ **IoC 容器**
- 组件自动扫描和注册
- 基于注解的依赖注入（字段、方法、构造函数）
- Bean 生命周期管理
- 单例和原型作用域支持
- 循环依赖检测

✅ **AOP 面向切面编程**
- 基于注解的切面定义
- 前置、后置、返回后通知支持
- JDK 动态代理和 CGLIB 代理
- 简单的切点表达式解析

✅ **MVC Web 框架**
- 基于注解的请求映射
- 自动参数绑定和类型转换
- 视图解析和响应处理
- RESTful API 支持

✅ **完整示例应用**
- 业务服务层演示
- AOP 切面应用示例
- Web 控制器实现
- 集成测试用例

## 项目结构

```
simple-spring-framework/
├── pom.xml                    # 父 POM 文件
├── README.md                  # 项目说明文档
├── spring-core/               # 核心工具和基础设施
│   ├── src/main/java/
│   │   └── com/simplespring/core/
│   │       ├── annotation/    # 核心注解定义
│   │       ├── convert/       # 类型转换系统
│   │       ├── io/           # 资源访问系统
│   │       └── util/         # 工具类
│   └── src/test/java/        # 单元测试
├── spring-beans/              # Bean 定义和工厂
│   ├── src/main/java/
│   │   └── com/simplespring/beans/
│   │       └── factory/      # Bean 工厂实现
│   └── src/test/java/        # 单元测试
├── spring-context/            # 应用上下文
│   ├── src/main/java/
│   │   └── com/simplespring/context/
│   │       ├── ApplicationContext.java
│   │       ├── AnnotationConfigApplicationContext.java
│   │       ├── ClassPathScanner.java
│   │       └── AspectProcessor.java
│   └── src/test/java/        # 单元测试
├── spring-aop/                # 面向切面编程
│   ├── src/main/java/
│   │   └── com/simplespring/aop/
│   │       ├── AspectDefinition.java
│   │       ├── ProxyFactory.java
│   │       ├── JoinPoint.java
│   │       └── MethodInterceptor.java
│   └── src/test/java/        # 单元测试
├── spring-webmvc/             # Web MVC 框架
│   ├── src/main/java/
│   │   └── com/simplespring/webmvc/
│   │       ├── DispatcherServlet.java
│   │       ├── HandlerMapping.java
│   │       ├── ViewResolver.java
│   │       └── ParameterResolver.java
│   └── src/test/java/        # 单元测试
└── spring-example/            # 示例应用
    ├── src/main/java/
    │   └── com/simplespring/example/
    │       ├── Application.java      # 应用启动类
    │       ├── config/              # 配置类
    │       ├── controller/          # Web 控制器
    │       ├── service/             # 业务服务
    │       ├── aspect/              # AOP 切面
    │       └── entity/              # 实体类
    └── src/test/java/              # 集成测试
```

## 环境要求

### 必需环境
- **JDK 1.7 或更高版本**
- **Apache Maven 3.x**

### 推荐环境
- **JDK 1.8**：更好的开发体验
- **IDE**：IntelliJ IDEA 或 Eclipse
- **Maven 3.6+**：最新的构建工具版本

### 依赖说明
项目使用 JDK 1.7 兼容的依赖版本：
- JUnit 4.12（测试框架）
- SLF4J 1.7.25（日志接口）
- Logback 1.2.3（日志实现）
- CGLIB 3.2.5（动态代理）
- Servlet API 3.1.0（Web 支持）

## 快速开始

### 1. 克隆项目
```bash
git clone <repository-url>
cd simple-spring-framework
```

### 2. 编译项目
```bash
# 编译所有模块
mvn clean compile

# 运行测试
mvn test

# 打包项目
mvn package
```

### 3. 运行示例应用
```bash
# 进入示例模块
cd spring-example

# 运行测试查看效果
mvn test

# 运行主类（如果配置了 exec 插件）
mvn exec:java -Dexec.mainClass="com.simplespring.example.Application"
```

## 使用指南

### IoC 容器使用

#### 1. 定义组件
```java
@Component
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
}
```

#### 2. 创建应用上下文
```java
// 创建应用上下文，扫描指定包
ApplicationContext context = new AnnotationConfigApplicationContext("com.example");

// 获取 Bean
UserService userService = context.getBean(UserService.class);

// 使用 Bean
User user = userService.findById(1L);
```

### AOP 切面使用

#### 1. 定义切面
```java
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
```

#### 2. 切面自动应用
```java
// 创建上下文时，切面会自动应用到匹配的 Bean 上
ApplicationContext context = new AnnotationConfigApplicationContext("com.example");
UserService userService = context.getBean(UserService.class);

// 调用方法时会触发切面逻辑
userService.findById(1L); // 会打印日志
```

### MVC 控制器使用

#### 1. 定义控制器
```java
@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public String getUser(Long id, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.findById(id);
        return "用户信息: " + user.getName();
    }
    
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser(String name, String email) {
        User user = new User(name, email);
        userService.save(user);
        return "用户创建成功";
    }
}
```

#### 2. 配置 DispatcherServlet
```java
// 创建应用上下文
ApplicationContext context = new AnnotationConfigApplicationContext("com.example");

// 创建 DispatcherServlet
DispatcherServlet servlet = new DispatcherServlet();

// 注册控制器
UserController controller = context.getBean(UserController.class);
servlet.registerController(UserController.class, controller);
```

## 测试指南

### 运行所有测试
```bash
# 运行所有模块的测试
mvn test

# 运行特定模块的测试
cd spring-core
mvn test
```

### 测试覆盖范围

#### 单元测试
- **spring-core**：工具类、类型转换、资源访问
- **spring-beans**：Bean 工厂、依赖注入、生命周期
- **spring-context**：应用上下文、组件扫描
- **spring-aop**：切面处理、代理生成、通知执行
- **spring-webmvc**：请求映射、参数解析、视图渲染

#### 集成测试
- **spring-example**：完整的应用功能测试
- 端到端的请求处理流程测试
- 多模块协作测试

### 查看测试结果
```bash
# 查看测试报告
find . -name "surefire-reports" -type d
```

## 学习路径

### 第一阶段：理解基础概念
1. **阅读项目结构**：了解各模块的职责和依赖关系
2. **学习核心注解**：掌握 @Component、@Autowired、@Controller 等注解的作用
3. **运行示例测试**：通过测试用例理解框架的基本功能

### 第二阶段：深入核心实现
1. **IoC 容器实现**：
   - 阅读 `ApplicationContext` 和 `BeanFactory` 的实现
   - 理解组件扫描和 Bean 注册的过程
   - 学习依赖注入的三种方式

2. **AOP 实现原理**：
   - 了解切面定义和通知类型
   - 学习 JDK 动态代理和 CGLIB 代理的使用
   - 理解切点表达式的解析和匹配

3. **MVC 请求处理**：
   - 掌握 DispatcherServlet 的工作流程
   - 学习请求映射和参数绑定的实现
   - 了解视图解析和响应生成的过程

### 第三阶段：实践和扩展
1. **修改示例应用**：添加新的服务类和控制器
2. **编写自定义切面**：实现性能监控、安全检查等功能
3. **扩展框架功能**：添加新的注解、增强类型转换等

## 设计思想

### 控制反转（IoC）
- **依赖查找 → 依赖注入**：从主动查找依赖转变为被动接收依赖
- **配置外部化**：通过注解和配置将依赖关系从代码中分离
- **生命周期管理**：容器负责对象的创建、初始化和销毁

### 面向切面编程（AOP）
- **关注点分离**：将横切关注点从业务逻辑中分离
- **代理模式**：通过代理对象实现方法拦截和增强
- **声明式编程**：通过注解声明切面逻辑，减少代码侵入

### 约定优于配置
- **默认命名规则**：类名首字母小写作为 Bean 名称
- **自动扫描机制**：按包路径自动发现和注册组件
- **注解驱动**：通过注解简化配置，提高开发效率

## 与 Spring 框架的对比

| 功能特性 | 简易版本 | Spring 框架 |
|---------|---------|------------|
| IoC 容器 | 基本的依赖注入和生命周期管理 | 完整的容器功能，支持复杂的配置和扩展 |
| AOP 支持 | 简单的方法拦截和通知 | 完整的 AspectJ 集成，支持复杂切点表达式 |
| MVC 框架 | 基本的请求映射和参数绑定 | 完整的 Web 框架，支持各种视图技术 |
| 配置方式 | 注解配置 | 注解、XML、Java 配置多种方式 |
| 性能优化 | 基础实现 | 高度优化，支持缓存、懒加载等 |
| 生态系统 | 独立实现 | 庞大的生态系统和第三方集成 |

## 常见问题

### Q: 为什么选择 JDK 1.7 兼容性？
A: 为了确保项目能在较老的环境中运行，同时避免使用过于现代的 Java 特性，让学习者专注于 Spring 的核心概念而不是 Java 语言特性。

### Q: 如何添加新的注解支持？
A: 
1. 在 `spring-core/annotation` 包中定义新注解
2. 在相应的处理器中添加注解识别逻辑
3. 编写测试用例验证功能

### Q: 如何扩展 AOP 功能？
A: 
1. 在 `AdviceType` 枚举中添加新的通知类型
2. 在 `AdviceExecutor` 中实现新通知的执行逻辑
3. 创建对应的注解和处理器

### Q: 如何添加新的参数解析器？
A: 
1. 实现 `ParameterResolver` 接口
2. 在 `ParameterResolverComposite` 中注册新的解析器
3. 编写测试用例验证解析逻辑

### Q: 项目的性能如何？
A: 这是一个学习项目，主要关注功能实现和代码可读性，性能方面没有进行深度优化。在生产环境中建议使用官方的 Spring 框架。

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进这个学习项目！

### 提交 Issue
- 描述遇到的问题或建议
- 提供复现步骤和环境信息
- 标明是 bug 报告还是功能请求

### 提交 Pull Request
- Fork 项目并创建特性分支
- 确保代码符合项目的编码规范
- 添加必要的测试用例
- 更新相关文档

## 许可证

本项目采用 MIT 许可证，详见 LICENSE 文件。

## 致谢

感谢 Spring 框架团队提供的优秀设计思想和实现参考。本项目仅用于学习目的，不用于商业用途。

---

**Happy Learning! 🎉**

如果这个项目对你的学习有帮助，请给个 ⭐️ 支持一下！
