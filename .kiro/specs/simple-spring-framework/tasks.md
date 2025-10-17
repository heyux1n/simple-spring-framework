# 实现计划

- [x] 1. 创建 Maven 多模块项目结构









  - 创建父 POM 文件，配置 JDK 1.7 兼容性和依赖版本管理
  - 创建 spring-core 子模块的 POM 文件和目录结构
  - 创建 spring-beans 子模块的 POM 文件和目录结构
  - 创建 spring-context 子模块的 POM 文件和目录结构
  - 创建 spring-aop 子模块的 POM 文件和目录结构
  - 创建 spring-webmvc 子模块的 POM 文件和目录结构
  - 创建 spring-example 子模块的 POM 文件和目录结构
  - _需求: 5.1, 5.2_

- [x] 2. 实现核心注解定义







  - [x] 2.1 创建 IoC 相关注解




    - 实现 @Component 注解，支持组件标识和可选的 Bean 名称
    - 实现 @Autowired 注解，支持字段、方法和构造函数注入
    - 实现 @Configuration 注解，标识配置类
    - 实现 @Bean 注解，支持方法级别的 Bean 定义
    - 编写注解的单元测试，验证注解的正确性
    - _需求: 1.1, 1.2, 6.1_



  - [x] 2.2 创建 AOP 相关注解





    - 实现 @Aspect 注解，标识切面类
    - 实现 @Before 注解，支持前置通知和切点表达式
    - 实现 @After 注解，支持后置通知和切点表达式
    - 实现 @AfterReturning 注解，支持返回后通知和返回值绑定
    - 编写注解的单元测试，验证注解属性的正确解析


    - _需求: 2.1, 2.2, 2.3, 2.5_

  - [x] 2.3 创建 MVC 相关注解





    - 实现 @Controller 注解，标识控制器类
    - 实现 @RequestMapping 注解，支持路径映射和 HTTP 方法指定
    - 创建 RequestMethod 枚举，定义支持的 HTTP 方法类型
    - 编写注解的单元测试，验证注解在类和方法上的使用
    - _需求: 3.1, 3.2_

- [x] 3. 实现 spring-core 模块基础功能




  - [x] 3.1 创建核心工具类


    - 实现 StringUtils 类，提供字符串处理的常用方法
    - 实现 ClassUtils 类，提供类操作的工具方法
    - 实现 ReflectionUtils 类，提供反射操作的便捷方法
    - 编写工具类的单元测试，验证各种边界条件
    - _需求: 5.4_

  - [x] 3.2 实现类型转换系统


    - 创建 TypeConverter 接口，定义类型转换的基本规范
    - 实现 DefaultTypeConverter 类，支持基本类型之间的转换
    - 创建 ConversionService 接口，扩展类型转换功能
    - 编写类型转换的单元测试，验证各种类型转换场景
    - _需求: 6.1_

  - [x] 3.3 实现资源访问系统


    - 创建 Resource 接口，抽象资源访问操作
    - 实现 ClassPathResource 类，支持类路径资源访问
    - 创建 ResourceLoader 接口，定义资源加载规范
    - 编写资源访问的单元测试，验证不同类型资源的加载
    - _需求: 6.1_

- [x] 4. 实现 spring-beans 模块核心功能





  - [x] 4.1 创建 Bean 定义和注册表


    - 实现 BeanDefinition 类，存储 Bean 的元数据信息
    - 实现 BeanRegistry 类，管理 Bean 定义和单例实例的缓存
    - 实现 Scope 枚举，定义 Bean 的作用域（单例、原型）
    - 编写 Bean 定义创建和注册的单元测试
    - _需求: 1.1, 6.2_



  - [x] 4.2 实现 BeanFactory 核心逻辑


    - 创建 BeanFactory 接口，定义 Bean 创建和管理的核心方法
    - 实现 DefaultBeanFactory 类，提供 Bean 的创建、缓存和生命周期管理
    - 实现构造函数依赖解析和注入逻辑，使用 spring-core 的工具类
    - 编写 Bean 创建和依赖注入的单元测试
    - _需求: 1.2, 1.4, 1.5_

- [x] 5. 实现 spring-context 模块核心功能






  - [x] 5.1 实现包扫描机制




    - 创建 ClassPathScanner 类，扫描指定包路径下的类文件
    - 实现注解识别逻辑，检测带有 @Component、@Controller 等注解的类
    - 实现 JDK 1.7 兼容的文件系统遍历和类加载机制，使用 spring-core 的工具类
    - 编写包扫描功能的单元测试，验证不同包结构的扫描结果
    - _需求: 1.1, 6.1_

  - [x] 5.2 实现 ApplicationContext


    - 创建 ApplicationContext 接口，定义应用上下文的核心功能
    - 实现 AnnotationConfigApplicationContext 类，支持注解驱动的配置
    - 集成包扫描、Bean 定义注册和 Bean 创建功能
    - 实现容器的刷新和初始化流程，集成 spring-core 的资源加载功能
    - 编写 ApplicationContext 的集成测试，验证完整的容器功能
    - _需求: 1.1, 1.2, 1.4_


- [x] 6. 实现依赖注入功能










  - [x] 6.1 实现字段注入

    - 创建 FieldInjector 类，处理带有 @Autowired 注解的字段注入
    - 实现按类型查找依赖 Bean 的逻辑
    - 实现字段访问权限处理和反射注入
    - 编写字段注入的单元测试，包括成功和失败场景
    - _需求: 1.3_



  - [x] 6.2 实现方法注入


    - 创建 MethodInjector 类，处理带有 @Autowired 注解的方法注入
    - 实现方法参数类型解析和依赖查找
    - 支持 setter 方法和普通方法的依赖注入
    - 编写方法注入的单元测试，验证不同方法签名的注入


    - _需求: 1.3_

  - [x] 6.3 实现构造函数注入



    - 扩展 BeanFactory，支持构造函数参数的依赖解析
    - 实现构造函数选择逻辑，优先选择带有 @Autowired 的构造函数
    - 处理构造函数参数的类型匹配和依赖查找
    - 编写构造函数注入的单元测试，包括多构造函数场景
    - _需求: 1.5_

- [x] 7. 实现 Bean 生命周期管理











  - [x] 7.1 实现生命周期注解支持



    - 创建 @PostConstruct 和 @PreDestroy 注解
    - 实现 BeanPostProcessor 接口，处理 Bean 初始化前后的回调
    - 在 Bean 创建流程中集成生命周期方法的调用
    - 编写生命周期方法的单元测试，验证调用时机和顺序
    - _需求: 6.3, 6.4_

  - [x] 7.2 实现循环依赖检测





    - 创建 CircularDependencyDetector 类，检测 Bean 之间的循环依赖
    - 实现依赖图构建和环路检测算法
    - 在 Bean 创建过程中集成循环依赖检测
    - 编写循环依赖检测的单元测试，验证各种循环依赖场景
    - _需求: 1.2_

- [x] 8. 实现 spring-aop 模块核心功能








  - [x] 8.1 创建切面定义模型




    - 实现 AspectDefinition 类，存储切面的元数据信息
    - 实现 AdviceDefinition 类，存储通知方法的详细信息
    - 创建 AdviceType 枚举，定义通知类型（前置、后置、返回后）
    - 编写切面定义模型的单元测试
    - _需求: 2.1_



  - [x] 8.2 实现切点表达式解析



    - 创建 PointcutExpressionParser 类，解析简单的切点表达式
    - 支持方法名匹配、包路径匹配和通配符
    - 实现 PointcutMatcher 接口，判断方法是否匹配切点
    - 编写切点表达式解析和匹配的单元测试


    - _需求: 2.2_

  - [x] 8.3 实现 JoinPoint 和通知执行




    - 创建 JoinPoint 接口和实现类，提供连接点信息
    - 实现 MethodInvocation 类，封装方法调用的上下文
    
    - 创建 AdviceExecutor 类，执行不同类型的通知方法
    - 编写通知执行的单元测试，验证通知的正确调用
    - _需求: 2.3, 2.4, 2.5_

  - [x] 8.4 实现 AOP 代理生成



    - 创建 ProxyFactory 类，根据目标类选择合适的代理方式
    - 实现 JDK 动态代理，处理实现接口的类
    - 集成 CGLIB 代理，处理没有实现接口的类
    - 实现 MethodInterceptor，拦截方法调用并执行通知
    - 编写代理生成的单元测试，验证不同类型的代理创建
    - _需求: 2.1, 2.2_

  - [x] 8.5 集成 AOP 到 spring-context 容器




    - 创建 AspectProcessor 类，扫描和注册切面 Bean
    - 在 Bean 创建过程中集成 AOP 代理生成
    - 实现切面的自动发现和应用逻辑
    - 编写 AOP 和 spring-context 集成的测试，验证切面对 Bean 的增强
    - _需求: 2.1, 2.2_

- [x] 9. 实现 spring-webmvc 模块核心功能





  - [x] 9.1 创建请求映射模型


    - 实现 RequestMappingInfo 类，存储请求映射的详细信息
    - 创建 HandlerMethod 类，封装控制器方法的元数据
    - 实现请求路径和 HTTP 方法的匹配逻辑
    - 编写请求映射模型的单元测试
    - _需求: 3.2_

  - [x] 9.2 实现 HandlerMapping


    - 创建 HandlerMapping 接口，定义处理器映射的核心方法
    - 实现 RequestMappingHandlerMapping 类，基于注解的处理器映射
    - 扫描控制器类和方法，构建请求映射表
    - 编写处理器映射的单元测试，验证不同请求的路由
    - _需求: 3.1, 3.2_

  - [x] 9.3 实现参数解析和绑定


    - 创建 ParameterResolver 接口，定义参数解析的规范
    - 实现基本类型参数解析器（String、int、boolean 等）
    - 实现 HttpServletRequest 和 HttpServletResponse 参数注入
    - 编写参数解析的单元测试，验证不同参数类型的绑定
    - _需求: 3.5_

  - [x] 9.4 实现 DispatcherServlet


    - 创建 DispatcherServlet 类，继承 HttpServlet 处理 HTTP 请求
    - 实现请求分发逻辑，集成 HandlerMapping 和参数解析
    - 实现控制器方法的调用和异常处理
    - 编写 DispatcherServlet 的集成测试，验证完整的请求处理流程
    - _需求: 3.3_

  - [x] 9.5 实现视图解析和渲染


    - 创建 ViewResolver 接口，定义视图解析的规范
    - 实现简单的字符串视图解析器，支持 JSON 和文本响应
    - 实现视图渲染逻辑，将控制器返回值转换为 HTTP 响应
    - 编写视图解析和渲染的单元测试
    - _需求: 3.4_

- [x] 10. 创建 spring-example 示例应用模块





  - [x] 10.1 创建业务服务层


    - 实现 UserService 接口和实现类，演示依赖注入功能
    - 实现 OrderService 类，演示 AOP 切面的应用
    - 创建业务实体类（User、Order），提供基本的业务逻辑
    - 编写业务服务的单元测试，验证业务逻辑的正确性
    - _需求: 4.1_

  - [x] 10.2 创建 AOP 切面示例


    - 实现 LoggingAspect 类，演示方法执行日志记录
    - 实现 PerformanceAspect 类，演示方法执行时间统计
    - 配置切面的切点表达式，应用到业务服务方法
    - 编写切面功能的集成测试，验证切面的执行效果
    - _需求: 4.2_

  - [x] 10.3 创建 MVC 控制器


    - 实现 UserController 类，提供用户相关的 HTTP 接口
    - 实现 OrderController 类，演示请求参数绑定和响应处理
    - 配置请求映射，支持不同的 HTTP 方法和路径
    - 编写控制器的集成测试，验证 HTTP 请求的处理
    - _需求: 4.3_

  - [x] 10.4 创建应用配置和启动类


    - 实现 AppConfig 配置类，使用 @Configuration 和 @Bean 注解
    - 创建 Application 主类，初始化 ApplicationContext 和 DispatcherServlet
    - 配置包扫描路径和组件自动发现
    - 编写应用启动的集成测试，验证完整的应用功能
    - _需求: 4.4, 4.5_

- [x] 11. 添加详细的中文注释和文档





  - [x] 11.1 为核心组件添加注释


    - 为 spring-core 相关类添加详细的中文注释，解释工具类和基础设施的作用
    - 为 spring-beans 和 spring-context 相关类添加详细的中文注释，解释设计思想和实现原理
    - 为 spring-aop 相关类添加中文注释，说明切面编程的概念和实现
    - 为 spring-webmvc 相关类添加中文注释，解释请求处理流程和组件职责
    - _需求: 5.4_

  - [x] 11.2 创建 README 文档


    - 编写项目介绍，说明简易 Spring 框架的学习目标和功能特性
    - 提供详细的项目搭建步骤，包括环境要求和依赖安装
    - 编写运行和测试指南，包括示例应用的使用说明
    - 创建学习指南，解释如何通过项目理解 Spring 框架的设计思想
    - _需求: 5.3, 5.5_

  - [x] 11.3 创建 API 文档和示例


    - 为核心 API 创建 Javadoc 文档，提供详细的使用说明
    - 编写代码示例，演示框架各个功能的使用方法
    - 创建常见问题解答，帮助学习者解决使用中的问题
    - _需求: 5.4, 5.5_

- [ ] 12. 编写测试用例和验证
  - [ ] 12.1 完善单元测试覆盖
    - 为所有核心类编写全面的单元测试，确保代码质量
    - 使用 JUnit 4.x 编写测试用例，兼容 JDK 1.7 环境
    - 添加边界条件和异常场景的测试，提高测试覆盖率
    - _需求: 5.5_

  - [ ] 12.2 编写集成测试
    - 创建端到端的集成测试，验证各模块之间的协作
    - 测试完整的 HTTP 请求处理流程，从请求到响应
    - 验证 spring-core、spring-beans、spring-context、spring-aop、spring-webmvc 五个模块的集成效果
    - _需求: 4.4, 4.5_

  - [ ] 12.3 性能测试和优化
    - 编写性能测试用例，测试 Bean 创建和依赖注入的性能
    - 测试 AOP 代理的性能开销，确保在可接受范围内
    - 优化关键路径的性能，提供性能测试报告
    - _需求: 5.5_
