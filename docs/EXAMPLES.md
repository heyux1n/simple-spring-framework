# Simple Spring Framework 代码示例

## 概述

本文档提供了简易 Spring 框架的详细代码示例，涵盖了从基础用法到高级特性的各种使用场景。

## 目录

- [基础示例](#基础示例)
- [IoC 容器示例](#ioc-容器示例)
- [依赖注入示例](#依赖注入示例)
- [AOP 切面示例](#aop-切面示例)
- [MVC 框架示例](#mvc-框架示例)
- [完整应用示例](#完整应用示例)
- [测试示例](#测试示例)

---

## 基础示例

### Hello World 示例

最简单的 Spring 应用示例：

```java
// 1. 定义服务类
@Component
public class HelloService {
    
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }
}

// 2. 定义控制器
@Controller
public class HelloController {
    
    @Autowired
    private HelloService helloService;
    
    @RequestMapping("/hello")
    public String hello(String name) {
        return helloService.sayHello(name != null ? name : "World");
    }
}

// 3. 应用启动类
public class HelloApplication {
    
    public static void main(String[] args) {
        // 创建应用上下文
        ApplicationContext context = new AnnotationConfigApplicationContext("com.example");
        
        // 获取服务
        HelloService helloService = context.getBean(HelloService.class);
        
        // 调用服务
        String message = helloService.sayHello("Spring");
        System.out.println(message);  // 输出: Hello, Spring!
        
        // 关闭上下文
        context.close();
    }
}
```

---

## IoC 容器示例

### 基本容器使用

```java
// 1. 定义实体类
public class User {
    private Long id;
    private String name;
    private String email;
    
    // 构造函数、getter 和 setter
    public User() {}
    
    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // getter 和 setter 方法...
}

// 2. 定义数据访问层
@Component
public class UserRepository {
    
    private List<User> users = new ArrayList<User>();
    
    public UserRepository() {
        // 初始化测试数据
        users.add(new User(1L, "张三", "zhangsan@example.com"));
        users.add(new User(2L, "李四", "lisi@example.com"));
    }
    
    public User findById(Long id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
    
    public List<User> findAll() {
        return new ArrayList<User>(users);
    }
    
    public void save(User user) {
        if (user.getId() == null) {
            user.setId((long) (users.size() + 1));
        }
        users.add(user);
    }
}

// 3. 定义服务层
@Component
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public void save(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        userRepository.save(user);
    }
}

// 4. 使用容器
public class ContainerExample {
    
    public static void main(String[] args) {
        // 创建容器
        ApplicationContext context = new AnnotationConfigApplicationContext("com.example");
        
        // 获取服务
        UserService userService = context.getBean(UserService.class);
        
        // 使用服务
        List<User> users = userService.findAll();
        System.out.println("用户总数: " + users.size());
        
        User user = userService.findById(1L);
        System.out.println("用户信息: " + user.getName());
        
        // 创建新用户
        User newUser = new User(null, "王五", "wangwu@example.com");
        userService.save(newUser);
        
        System.out.println("保存后用户总数: " + userService.findAll().size());
        
        context.close();
    }
}
```

### 配置类示例

```java
// 1. 配置类
@Configuration
public class AppConfig {
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }
    
    @Bean
    public UserService userService() {
        UserService service = new UserService();
        // 手动注入依赖（如果不使用 @Autowired）
        return service;
    }
    
    @Bean("customEmailService")
    public EmailService emailService() {
        return new EmailServiceImpl();
    }
}

// 2. 使用配置类
public class ConfigExample {
    
    public static void main(String[] args) {
        // 基于配置类创建容器
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        // 获取 Bean
        UserService userService = context.getBean(UserService.class);
        EmailService emailService = context.getBean("customEmailService", EmailService.class);
        
        // 使用服务
        User user = userService.findById(1L);
        emailService.sendEmail(user.getEmail(), "欢迎", "欢迎使用我们的服务！");
        
        context.close();
    }
}
```

---

## 依赖注入示例

### 字段注入示例

```java
@Component
public class OrderService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired(required = false)  // 可选依赖
    private SmsService smsService;
    
    public void createOrder(Long userId, String productName) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 创建订单逻辑
        System.out.println("为用户 " + user.getName() + " 创建订单: " + productName);
        
        // 发送邮件通知
        emailService.sendEmail(user.getEmail(), "订单确认", "您的订单已创建: " + productName);
        
        // 发送短信通知（如果服务可用）
        if (smsService != null) {
            smsService.sendSms(user.getPhone(), "订单创建成功");
        }
    }
}
```

### 构造函数注入示例

```java
@Component
public class PaymentService {
    
    private final UserService userService;
    private final OrderService orderService;
    private final EmailService emailService;
    
    @Autowired
    public PaymentService(UserService userService, OrderService orderService, EmailService emailService) {
        this.userService = userService;
        this.orderService = orderService;
        this.emailService = emailService;
    }
    
    public void processPayment(Long userId, Long orderId, double amount) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 处理支付逻辑
        System.out.println("处理用户 " + user.getName() + " 的支付: " + amount + " 元");
        
        // 发送支付确认邮件
        emailService.sendEmail(user.getEmail(), "支付确认", "支付成功，金额: " + amount + " 元");
    }
}
```

### 方法注入示例

```java
@Component
public class NotificationService {
    
    private UserService userService;
    private EmailService emailService;
    private SmsService smsService;
    
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    @Autowired
    public void configureServices(EmailService emailService, SmsService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }
    
    public void sendNotification(Long userId, String message) {
        User user = userService.findById(userId);
        if (user != null) {
            emailService.sendEmail(user.getEmail(), "通知", message);
            if (user.getPhone() != null) {
                smsService.sendSms(user.getPhone(), message);
            }
        }
    }
}
```

### 生命周期管理示例

```java
@Component
public class DatabaseService {
    
    private Connection connection;
    
    @PostConstruct
    public void init() {
        System.out.println("初始化数据库连接...");
        // 模拟数据库连接初始化
        this.connection = createConnection();
        System.out.println("数据库连接初始化完成");
    }
    
    @PreDestroy
    public void destroy() {
        System.out.println("关闭数据库连接...");
        if (connection != null) {
            closeConnection(connection);
        }
        System.out.println("数据库连接已关闭");
    }
    
    public void executeQuery(String sql) {
        if (connection == null) {
            throw new IllegalStateException("数据库连接未初始化");
        }
        System.out.println("执行 SQL: " + sql);
    }
    
    private Connection createConnection() {
        // 模拟创建数据库连接
        return new MockConnection();
    }
    
    private void closeConnection(Connection conn) {
        // 模拟关闭数据库连接
        System.out.println("连接已关闭");
    }
    
    // 模拟连接类
    private static class MockConnection implements Connection {
        // 实现省略...
    }
}
```

---

## AOP 切面示例

### 日志切面示例

```java
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getTargetClass().getSimpleName();
        String methodName = joinPoint.getMethod().getName();
        Object[] args = joinPoint.getArgs();
        
        System.out.println("[日志] 执行方法: " + className + "." + methodName);
        System.out.println("[日志] 参数: " + Arrays.toString(args));
        System.out.println("[日志] 执行时间: " + new Date());
    }
    
    @After("execution(* com.example.service.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        String className = joinPoint.getTargetClass().getSimpleName();
        String methodName = joinPoint.getMethod().getName();
        
        System.out.println("[日志] 方法执行完成: " + className + "." + methodName);
    }
    
    @AfterReturning(value = "execution(* com.example.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTargetClass().getSimpleName();
        String methodName = joinPoint.getMethod().getName();
        
        System.out.println("[日志] 方法返回: " + className + "." + methodName);
        System.out.println("[日志] 返回值: " + result);
    }
}
```

### 性能监控切面示例

```java
@Aspect
@Component
public class PerformanceAspect {
    
    private Map<String, Long> executionTimes = new ConcurrentHashMap<String, Long>();
    
    @Before("execution(* com.example.service.*.*(..))")
    public void startTimer(JoinPoint joinPoint) {
        String key = getMethodKey(joinPoint);
        executionTimes.put(key, System.currentTimeMillis());
    }
    
    @After("execution(* com.example.service.*.*(..))")
    public void endTimer(JoinPoint joinPoint) {
        String key = getMethodKey(joinPoint);
        Long startTime = executionTimes.remove(key);
        
        if (startTime != null) {
            long executionTime = System.currentTimeMillis() - startTime;
            System.out.println("[性能] " + key + " 执行时间: " + executionTime + "ms");
            
            // 如果执行时间超过阈值，记录警告
            if (executionTime > 1000) {
                System.out.println("[警告] " + key + " 执行时间过长: " + executionTime + "ms");
            }
        }
    }
    
    private String getMethodKey(JoinPoint joinPoint) {
        return joinPoint.getTargetClass().getSimpleName() + "." + joinPoint.getMethod().getName();
    }
}
```

### 安全检查切面示例

```java
@Aspect
@Component
public class SecurityAspect {
    
    @Before("execution(* com.example.service.UserService.delete*(..))")
    public void checkDeletePermission(JoinPoint joinPoint) {
        // 模拟安全检查
        String currentUser = getCurrentUser();
        if (!"admin".equals(currentUser)) {
            throw new SecurityException("没有删除权限，当前用户: " + currentUser);
        }
        System.out.println("[安全] 删除权限检查通过，用户: " + currentUser);
    }
    
    @Before("execution(* com.example.service.*Service.save*(..))")
    public void checkSavePermission(JoinPoint joinPoint) {
        String currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("用户未登录");
        }
        System.out.println("[安全] 保存权限检查通过，用户: " + currentUser);
    }
    
    private String getCurrentUser() {
        // 模拟获取当前用户
        // 实际应用中可能从 Session 或 ThreadLocal 中获取
        return "admin";  // 模拟当前用户
    }
}
```

### 缓存切面示例

```java
@Aspect
@Component
public class CacheAspect {
    
    private Map<String, Object> cache = new ConcurrentHashMap<String, Object>();
    
    @Before("execution(* com.example.service.UserService.findById(..))")
    public void checkCache(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            String cacheKey = "user:" + args[0];
            Object cachedResult = cache.get(cacheKey);
            if (cachedResult != null) {
                System.out.println("[缓存] 命中缓存: " + cacheKey);
                // 注意：这里只是演示，实际的缓存切面需要更复杂的实现
            }
        }
    }
    
    @AfterReturning(value = "execution(* com.example.service.UserService.findById(..))", returning = "result")
    public void updateCache(JoinPoint joinPoint, Object result) {
        if (result != null) {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                String cacheKey = "user:" + args[0];
                cache.put(cacheKey, result);
                System.out.println("[缓存] 更新缓存: " + cacheKey);
            }
        }
    }
}
```

---

## MVC 框架示例

### 基本控制器示例

```java
@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // 获取用户列表
    @RequestMapping("/users")
    public String listUsers() {
        List<User> users = userService.findAll();
        StringBuilder result = new StringBuilder();
        result.append("用户列表:\n");
        for (User user : users) {
            result.append("- ").append(user.getName()).append(" (").append(user.getEmail()).append(")\n");
        }
        return result.toString();
    }
    
    // 获取单个用户
    @RequestMapping("/users/{id}")
    public String getUser(Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return "用户不存在: ID=" + id;
        }
        return "用户信息: " + user.getName() + " (" + user.getEmail() + ")";
    }
    
    // 创建用户
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            return "错误: 用户名不能为空";
        }
        if (email == null || email.trim().isEmpty()) {
            return "错误: 邮箱不能为空";
        }
        
        User user = new User(null, name, email);
        userService.save(user);
        return "用户创建成功: " + name;
    }
}
```

### RESTful API 示例

```java
@Controller
public class ApiController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;
    
    // GET /api/users - 获取所有用户
    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public String getUsers() {
        List<User> users = userService.findAll();
        // 简单的 JSON 格式返回
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < users.size(); i++) {
            if (i > 0) json.append(",");
            User user = users.get(i);
            json.append("{\"id\":").append(user.getId())
                .append(",\"name\":\"").append(user.getName())
                .append("\",\"email\":\"").append(user.getEmail()).append("\"}");
        }
        json.append("]");
        return json.toString();
    }
    
    // GET /api/users/{id} - 获取指定用户
    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.GET)
    public String getUser(Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return "{\"error\":\"用户不存在\",\"id\":" + id + "}";
        }
        return "{\"id\":" + user.getId() + 
               ",\"name\":\"" + user.getName() + 
               "\",\"email\":\"" + user.getEmail() + "\"}";
    }
    
    // POST /api/users - 创建用户
    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public String createUser(String name, String email) {
        try {
            User user = new User(null, name, email);
            userService.save(user);
            return "{\"success\":true,\"message\":\"用户创建成功\",\"name\":\"" + name + "\"}";
        } catch (Exception e) {
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }
    
    // PUT /api/users/{id} - 更新用户
    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.PUT)
    public String updateUser(Long id, String name, String email) {
        User user = userService.findById(id);
        if (user == null) {
            return "{\"success\":false,\"error\":\"用户不存在\"}";
        }
        
        if (name != null) user.setName(name);
        if (email != null) user.setEmail(email);
        
        userService.save(user);
        return "{\"success\":true,\"message\":\"用户更新成功\"}";
    }
    
    // DELETE /api/users/{id} - 删除用户
    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.DELETE)
    public String deleteUser(Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return "{\"success\":false,\"error\":\"用户不存在\"}";
        }
        
        // 这里应该调用 userService.delete(id)，但示例中没有实现
        return "{\"success\":true,\"message\":\"用户删除成功\"}";
    }
}
```

### 参数绑定示例

```java
@Controller
public class ParameterController {
    
    // 基本类型参数
    @RequestMapping("/search")
    public String search(String keyword, Integer page, Integer size) {
        page = page != null ? page : 1;
        size = size != null ? size : 10;
        
        return "搜索关键词: " + keyword + ", 页码: " + page + ", 每页大小: " + size;
    }
    
    // 多个参数
    @RequestMapping("/register")
    public String register(String username, String password, String email, Integer age) {
        StringBuilder result = new StringBuilder();
        result.append("注册信息:\n");
        result.append("用户名: ").append(username).append("\n");
        result.append("密码: ").append("***").append("\n");  // 不显示密码
        result.append("邮箱: ").append(email).append("\n");
        result.append("年龄: ").append(age).append("\n");
        
        return result.toString();
    }
    
    // Servlet API 参数
    @RequestMapping("/info")
    public String getInfo(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder result = new StringBuilder();
        result.append("请求信息:\n");
        result.append("请求方法: ").append(request.getMethod()).append("\n");
        result.append("请求路径: ").append(request.getRequestURI()).append("\n");
        result.append("User-Agent: ").append(request.getHeader("User-Agent")).append("\n");
        result.append("远程地址: ").append(request.getRemoteAddr()).append("\n");
        
        // 设置响应头
        response.setHeader("Custom-Header", "Simple-Spring-Framework");
        
        return result.toString();
    }
    
    // 路径参数和查询参数组合
    @RequestMapping("/users/{userId}/orders")
    public String getUserOrders(Long userId, String status, String sortBy) {
        StringBuilder result = new StringBuilder();
        result.append("用户订单查询:\n");
        result.append("用户ID: ").append(userId).append("\n");
        result.append("订单状态: ").append(status != null ? status : "全部").append("\n");
        result.append("排序方式: ").append(sortBy != null ? sortBy : "默认").append("\n");
        
        return result.toString();
    }
}
```

---

## 完整应用示例

### 电商系统示例

```java
// 1. 实体类
public class Product {
    private Long id;
    private String name;
    private double price;
    private int stock;
    
    // 构造函数、getter 和 setter
    public Product() {}
    
    public Product(Long id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    
    // getter 和 setter 方法...
}

public class Order {
    private Long id;
    private Long userId;
    private Long productId;
    private int quantity;
    private double totalAmount;
    private String status;
    
    // 构造函数、getter 和 setter...
}

// 2. 数据访问层
@Component
public class ProductRepository {
    
    private List<Product> products = new ArrayList<Product>();
    
    public ProductRepository() {
        // 初始化商品数据
        products.add(new Product(1L, "iPhone 13", 5999.0, 100));
        products.add(new Product(2L, "MacBook Pro", 12999.0, 50));
        products.add(new Product(3L, "iPad Air", 3999.0, 80));
    }
    
    public Product findById(Long id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }
    
    public List<Product> findAll() {
        return new ArrayList<Product>(products);
    }
    
    public void updateStock(Long productId, int newStock) {
        Product product = findById(productId);
        if (product != null) {
            product.setStock(newStock);
        }
    }
}

@Component
public class OrderRepository {
    
    private List<Order> orders = new ArrayList<Order>();
    private Long nextId = 1L;
    
    public void save(Order order) {
        if (order.getId() == null) {
            order.setId(nextId++);
        }
        orders.add(order);
    }
    
    public List<Order> findByUserId(Long userId) {
        List<Order> userOrders = new ArrayList<Order>();
        for (Order order : orders) {
            if (order.getUserId().equals(userId)) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }
}

// 3. 服务层
@Component
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public boolean checkStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId);
        return product != null && product.getStock() >= quantity;
    }
    
    public void reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId);
        if (product != null && product.getStock() >= quantity) {
            productRepository.updateStock(productId, product.getStock() - quantity);
        } else {
            throw new IllegalStateException("库存不足");
        }
    }
}

@Component
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    public Order createOrder(Long userId, Long productId, int quantity) {
        // 检查用户是否存在
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 检查商品是否存在
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        
        // 检查库存
        if (!productService.checkStock(productId, quantity)) {
            throw new IllegalStateException("库存不足");
        }
        
        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalAmount(product.getPrice() * quantity);
        order.setStatus("CREATED");
        
        // 减少库存
        productService.reduceStock(productId, quantity);
        
        // 保存订单
        orderRepository.save(order);
        
        return order;
    }
    
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}

// 4. 控制器层
@Controller
public class ECommerceController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    // 商品列表
    @RequestMapping("/products")
    public String listProducts() {
        List<Product> products = productService.getAllProducts();
        StringBuilder result = new StringBuilder();
        result.append("商品列表:\n");
        for (Product product : products) {
            result.append("- ").append(product.getName())
                  .append(" ¥").append(product.getPrice())
                  .append(" (库存: ").append(product.getStock()).append(")\n");
        }
        return result.toString();
    }
    
    // 商品详情
    @RequestMapping("/products/{id}")
    public String getProduct(Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "商品不存在: ID=" + id;
        }
        
        return "商品详情:\n" +
               "名称: " + product.getName() + "\n" +
               "价格: ¥" + product.getPrice() + "\n" +
               "库存: " + product.getStock();
    }
    
    // 创建订单
    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public String createOrder(Long userId, Long productId, Integer quantity) {
        try {
            if (quantity == null || quantity <= 0) {
                return "错误: 数量必须大于0";
            }
            
            Order order = orderService.createOrder(userId, productId, quantity);
            return "订单创建成功:\n" +
                   "订单ID: " + order.getId() + "\n" +
                   "总金额: ¥" + order.getTotalAmount() + "\n" +
                   "状态: " + order.getStatus();
        } catch (Exception e) {
            return "订单创建失败: " + e.getMessage();
        }
    }
    
    // 用户订单列表
    @RequestMapping("/users/{userId}/orders")
    public String getUserOrders(Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        if (orders.isEmpty()) {
            return "用户暂无订单";
        }
        
        StringBuilder result = new StringBuilder();
        result.append("用户订单列表:\n");
        for (Order order : orders) {
            Product product = productService.getProductById(order.getProductId());
            result.append("- 订单").append(order.getId())
                  .append(": ").append(product != null ? product.getName() : "未知商品")
                  .append(" x").append(order.getQuantity())
                  .append(" ¥").append(order.getTotalAmount())
                  .append(" (").append(order.getStatus()).append(")\n");
        }
        return result.toString();
    }
}

// 5. AOP 切面
@Aspect
@Component
public class ECommerceAspect {
    
    @Before("execution(* com.example.service.OrderService.createOrder(..))")
    public void logOrderCreation(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        System.out.println("[订单] 开始创建订单 - 用户ID: " + args[0] + ", 商品ID: " + args[1] + ", 数量: " + args[2]);
    }
    
    @AfterReturning(value = "execution(* com.example.service.OrderService.createOrder(..))", returning = "order")
    public void logOrderSuccess(JoinPoint joinPoint, Object order) {
        if (order instanceof Order) {
            Order o = (Order) order;
            System.out.println("[订单] 订单创建成功 - 订单ID: " + o.getId() + ", 金额: ¥" + o.getTotalAmount());
        }
    }
    
    @Before("execution(* com.example.service.ProductService.reduceStock(..))")
    public void logStockReduction(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        System.out.println("[库存] 减少库存 - 商品ID: " + args[0] + ", 数量: " + args[1]);
    }
}

// 6. 应用启动类
public class ECommerceApplication {
    
    public static void main(String[] args) {
        // 创建应用上下文
        ApplicationContext context = new AnnotationConfigApplicationContext("com.example");
        
        // 获取服务
        UserService userService = context.getBean(UserService.class);
        ProductService productService = context.getBean(ProductService.class);
        OrderService orderService = context.getBean(OrderService.class);
        
        // 演示功能
        System.out.println("=== 电商系统演示 ===");
        
        // 显示商品列表
        System.out.println("\n商品列表:");
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            System.out.println("- " + product.getName() + " ¥" + product.getPrice() + " (库存: " + product.getStock() + ")");
        }
        
        // 创建订单
        System.out.println("\n创建订单:");
        try {
            Order order = orderService.createOrder(1L, 1L, 2);
            System.out.println("订单创建成功: " + order.getId() + ", 金额: ¥" + order.getTotalAmount());
        } catch (Exception e) {
            System.out.println("订单创建失败: " + e.getMessage());
        }
        
        // 查看用户订单
        System.out.println("\n用户订单:");
        List<Order> userOrders = orderService.getUserOrders(1L);
        for (Order order : userOrders) {
            System.out.println("- 订单" + order.getId() + ": ¥" + order.getTotalAmount() + " (" + order.getStatus() + ")");
        }
        
        context.close();
    }
}
```

---

## 测试示例

### 单元测试示例

```java
// 服务层测试
public class UserServiceTest {
    
    private UserService userService;
    private UserRepository userRepository;
    
    @Before
    public void setUp() {
        // 创建应用上下文进行测试
        ApplicationContext context = new AnnotationConfigApplicationContext("com.example");
        userService = context.getBean(UserService.class);
        userRepository = context.getBean(UserRepository.class);
    }
    
    @Test
    public void testFindById() {
        // 测试查找用户
        User user = userService.findById(1L);
        
        assertNotNull("用户不应该为空", user);
        assertEquals("用户ID应该匹配", Long.valueOf(1L), user.getId());
        assertEquals("用户名应该匹配", "张三", user.getName());
    }
    
    @Test
    public void testFindByIdNotFound() {
        // 测试查找不存在的用户
        User user = userService.findById(999L);
        
        assertNull("不存在的用户应该返回null", user);
    }
    
    @Test
    public void testSaveUser() {
        // 测试保存用户
        User newUser = new User(null, "测试用户", "test@example.com");
        userService.save(newUser);
        
        List<User> allUsers = userService.findAll();
        assertTrue("用户列表应该包含新用户", allUsers.size() > 2);
        
        // 查找新创建的用户
        boolean found = false;
        for (User user : allUsers) {
            if ("测试用户".equals(user.getName())) {
                found = true;
                break;
            }
        }
        assertTrue("应该能找到新创建的用户", found);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSaveUserWithEmptyName() {
        // 测试保存空名称用户应该抛出异常
        User invalidUser = new User(null, "", "test@example.com");
        userService.save(invalidUser);
    }
}
```

### 集成测试示例

```java
public class ApplicationIntegrationTest {
    
    private ApplicationContext context;
    
    @Before
    public void setUp() {
        context = new AnnotationConfigApplicationContext("com.example");
    }
    
    @After
    public void tearDown() {
        if (context != null) {
            context.close();
        }
    }
    
    @Test
    public void testCompleteWorkflow() {
        // 获取所有需要的服务
        UserService userService = context.getBean(UserService.class);
        ProductService productService = context.getBean(ProductService.class);
        OrderService orderService = context.getBean(OrderService.class);
        
        // 1. 验证初始数据
        List<User> users = userService.findAll();
        assertTrue("应该有初始用户数据", users.size() > 0);
        
        List<Product> products = productService.getAllProducts();
        assertTrue("应该有初始商品数据", products.size() > 0);
        
        // 2. 创建新用户
        User newUser = new User(null, "集成测试用户", "integration@test.com");
        userService.save(newUser);
        
        // 3. 为新用户创建订单
        Product firstProduct = products.get(0);
        int originalStock = firstProduct.getStock();
        
        Order order = orderService.createOrder(newUser.getId(), firstProduct.getId(), 1);
        
        // 4. 验证订单创建结果
        assertNotNull("订单应该创建成功", order);
        assertEquals("订单用户ID应该匹配", newUser.getId(), order.getUserId());
        assertEquals("订单商品ID应该匹配", firstProduct.getId(), order.getProductId());
        assertEquals("订单数量应该匹配", 1, order.getQuantity());
        assertEquals("订单金额应该匹配", firstProduct.getPrice(), order.getTotalAmount(), 0.01);
        
        // 5. 验证库存减少
        Product updatedProduct = productService.getProductById(firstProduct.getId());
        assertEquals("库存应该减少", originalStock - 1, updatedProduct.getStock());
        
        // 6. 验证用户订单列表
        List<Order> userOrders = orderService.getUserOrders(newUser.getId());
        assertEquals("用户应该有一个订单", 1, userOrders.size());
        assertEquals("订单ID应该匹配", order.getId(), userOrders.get(0).getId());
    }
    
    @Test
    public void testAopIntegration() {
        // 测试 AOP 功能
        UserService userService = context.getBean(UserService.class);
        
        // 这个调用应该触发 AOP 切面
        User user = userService.findById(1L);
        
        assertNotNull("用户应该存在", user);
        // AOP 日志应该在控制台输出，这里只能通过手动观察验证
    }
    
    @Test
    public void testDependencyInjection() {
        // 测试依赖注入是否正确工作
        OrderService orderService = context.getBean(OrderService.class);
        
        // OrderService 应该正确注入了所有依赖
        assertNotNull("OrderService 应该存在", orderService);
        
        // 通过调用方法来验证依赖注入是否正确
        List<Order> orders = orderService.getUserOrders(1L);
        assertNotNull("应该能正常调用方法", orders);
    }
}
```

### MVC 测试示例

```java
public class ControllerTest {
    
    private ApplicationContext context;
    private UserController userController;
    private ECommerceController eCommerceController;
    
    @Before
    public void setUp() {
        context = new AnnotationConfigApplicationContext("com.example");
        userController = context.getBean(UserController.class);
        eCommerceController = context.getBean(ECommerceController.class);
    }
    
    @After
    public void tearDown() {
        if (context != null) {
            context.close();
        }
    }
    
    @Test
    public void testUserController() {
        // 测试用户列表
        String userList = userController.listUsers();
        assertNotNull("用户列表不应该为空", userList);
        assertTrue("用户列表应该包含用户信息", userList.contains("用户列表"));
        
        // 测试获取单个用户
        String userInfo = userController.getUser(1L);
        assertNotNull("用户信息不应该为空", userInfo);
        assertTrue("用户信息应该包含用户名", userInfo.contains("张三"));
        
        // 测试获取不存在的用户
        String notFound = userController.getUser(999L);
        assertTrue("应该返回用户不存在信息", notFound.contains("用户不存在"));
        
        // 测试创建用户
        String createResult = userController.createUser("测试用户", "test@example.com");
        assertTrue("应该返回创建成功信息", createResult.contains("创建成功"));
    }
    
    @Test
    public void testECommerceController() {
        // 测试商品列表
        String productList = eCommerceController.listProducts();
        assertNotNull("商品列表不应该为空", productList);
        assertTrue("商品列表应该包含商品信息", productList.contains("商品列表"));
        
        // 测试商品详情
        String productInfo = eCommerceController.getProduct(1L);
        assertNotNull("商品信息不应该为空", productInfo);
        assertTrue("商品信息应该包含商品名称", productInfo.contains("iPhone"));
        
        // 测试创建订单
        String orderResult = eCommerceController.createOrder(1L, 1L, 1);
        assertTrue("应该返回订单创建成功信息", orderResult.contains("订单创建成功"));
        
        // 测试用户订单列表
        String userOrders = eCommerceController.getUserOrders(1L);
        assertNotNull("用户订单列表不应该为空", userOrders);
    }
    
    @Test
    public void testParameterBinding() {
        // 测试参数绑定
        String createResult = userController.createUser("参数测试", "param@test.com");
        assertTrue("参数应该正确绑定", createResult.contains("参数测试"));
        
        // 测试空参数处理
        String emptyNameResult = userController.createUser("", "empty@test.com");
        assertTrue("应该处理空参数", emptyNameResult.contains("错误"));
        
        String nullNameResult = userController.createUser(null, "null@test.com");
        assertTrue("应该处理null参数", nullNameResult.contains("错误"));
    }
}
```

---

这些示例涵盖了简易 Spring 框架的所有主要功能，从基础的 IoC 容器使用到复杂的企业级应用开发。通过这些示例，你可以：

1. **理解框架的基本用法**：从简单的 Hello World 开始
2. **掌握依赖注入**：学习三种注入方式的使用
3. **应用 AOP 切面**：实现日志、性能监控、安全检查等横切关注点
4. **开发 Web 应用**：构建 RESTful API 和传统的 Web 控制器
5. **构建完整应用**：通过电商系统示例了解企业级应用的架构
6. **编写测试用例**：确保代码质量和功能正确性

每个示例都包含了详细的注释和说明，帮助你理解代码的工作原理和设计思想。
