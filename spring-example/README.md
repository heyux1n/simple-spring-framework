# Spring Example

Spring 框架示例应用，演示了完整的 IoC、DI、AOP 和 Web MVC 功能。现已支持嵌入式 HTTP 服务器，类似于 Spring Boot 的运行方式。

## 功能特性

- **IoC 容器**: 基于注解的依赖注入
- **AOP 切面**: 日志记录和性能监控
- **Web MVC**: RESTful API 接口
- **嵌入式服务器**: 内置 Tomcat 服务器，独立运行
- **业务逻辑**: 用户管理和订单处理

## 项目结构

```
src/main/java/com/simplespring/example/
├── Application.java           # 应用主类
├── server/
│   └── EmbeddedTomcatServer.java # 嵌入式 Tomcat 服务器
├── config/
│   └── AppConfig.java        # 配置类
├── controller/
│   ├── UserController.java   # 用户控制器
│   └── OrderController.java  # 订单控制器
├── service/
│   ├── UserService.java      # 用户服务接口
│   ├── OrderService.java     # 订单服务接口
│   └── impl/
│       └── UserServiceImpl.java # 用户服务实现
├── entity/
│   ├── User.java             # 用户实体
│   ├── Order.java            # 订单实体
│   └── OrderStatus.java      # 订单状态枚举
└── aspect/
    ├── LoggingAspect.java    # 日志切面
    └── PerformanceAspect.java # 性能监控切面
```

## 运行方式

### 1. 启动 HTTP 服务器（推荐）

```bash
# 编译项目
mvn clean compile

# 启动 HTTP 服务器（默认端口 8080）
mvn exec:java -Dexec.mainClass="com.simplespring.example.Application"

# 指定端口启动
mvn exec:java -Dexec.mainClass="com.simplespring.example.Application" -Dexec.args="--port=9090"

# 打包并运行
mvn clean package
java -jar target/spring-example-1.0.0.jar

# 指定端口运行
java -jar target/spring-example-1.0.0.jar --port=9090
```

### 2. 仅运行功能演示

```bash
# 只运行演示，不启动服务器
mvn exec:java -Dexec.mainClass="com.simplespring.example.Application" -Dexec.args="--demo-only"
```

### 3. 启动服务器但跳过演示

```bash
# 启动服务器，跳过功能演示
mvn exec:java -Dexec.mainClass="com.simplespring.example.Application" -Dexec.args="--no-demo"
```

## HTTP API 接口

服务器启动后，可以通过以下接口进行访问：

### 用户接口

- `GET /users` - 获取所有用户
- `GET /users/{id}` - 根据ID获取用户
- `POST /users` - 创建用户
  - 参数: `username`, `email`, `password`
- `POST /users/login` - 用户登录
  - 参数: `username`, `password`
- `PUT /users/{id}` - 更新用户
  - 参数: `username`, `email`
- `DELETE /users/{id}` - 删除用户

### 订单接口

- `GET /orders` - 获取所有订单
- `GET /orders/{id}` - 根据ID获取订单
- `GET /orders/user/{userId}` - 根据用户ID获取订单
- `POST /orders` - 创建订单
  - 参数: `userId`, `productName`, `quantity`, `price`
- `PUT /orders/{id}/confirm` - 确认订单
- `PUT /orders/{id}/process` - 处理订单
- `PUT /orders/{id}/ship` - 发货订单
- `PUT /orders/{id}/complete` - 完成订单
- `PUT /orders/{id}/cancel` - 取消订单
- `GET /orders/statistics` - 获取订单统计

## API 测试

### 使用测试脚本

```bash
# Linux/Mac
chmod +x src/main/resources/test-api.sh
./src/main/resources/test-api.sh

# Windows
src\main\resources\test-api.bat
```

### 使用 curl 命令

```bash
# 创建用户
curl -X POST http://localhost:8080/users \
  -d "username=testuser" \
  -d "email=test@example.com" \
  -d "password=password123"

# 用户登录
curl -X POST http://localhost:8080/users/login \
  -d "username=testuser" \
  -d "password=password123"

# 创建订单
curl -X POST http://localhost:8080/orders \
  -d "userId=1" \
  -d "productName=测试商品" \
  -d "quantity=2" \
  -d "price=99.99"

# 获取订单统计
curl -X GET http://localhost:8080/orders/statistics
```

### 使用浏览器

直接在浏览器中访问 GET 接口：

- http://localhost:8080/users
- http://localhost:8080/orders
- http://localhost:8080/orders/statistics

## 功能演示

应用启动后会依次演示：

1. **IoC 和依赖注入**: 展示容器管理的 Bean 和自动装配
2. **AOP 功能**: 观察方法调用的日志输出和性能监控
3. **业务工作流程**: 完整的用户注册、登录、订单创建和处理流程
4. **HTTP 服务**: 提供 RESTful API 接口供外部访问

## 输出示例

```
=== 简易Spring框架示例应用启动 ===
正在启动应用...
初始化 ApplicationContext...
ApplicationContext 初始化完成
初始化 DispatcherServlet...
DispatcherServlet 初始化完成
应用启动完成
正在启动 HTTP 服务器...
正在启动嵌入式 Tomcat 服务器...
端口: 8080
上下文路径: 
已注册 DispatcherServlet，映射路径: /*
嵌入式 Tomcat 服务器启动成功!
访问地址: http://localhost:8080
API 文档:
  用户接口:
    GET    http://localhost:8080/users - 获取所有用户
    POST   http://localhost:8080/users - 创建用户
    ...
HTTP 服务器启动成功，端口: 8080
服务器正在运行，按 Ctrl+C 停止...
```

## 故障排除

### 端口冲突

如果遇到端口冲突错误，可以：

1. 使用不同端口启动：`--port=9090`
2. 停止占用端口的进程
3. 检查防火墙设置

### 接口无响应

1. 确认服务器已成功启动
2. 检查请求URL和参数格式
3. 查看应用日志输出
4. 使用测试脚本验证接口

### 启动错误

1. 确认 Java 版本兼容（推荐 Java 8+）
2. 检查 Maven 依赖是否正确下载
3. 查看详细错误日志
4. 确认端口未被占用
