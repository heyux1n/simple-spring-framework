package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * PreDestroy 注解
 * 标识在 Bean 销毁前需要调用的方法
 * 
 * 该注解用于标记在 Bean 销毁前需要执行的清理方法。
 * 被标记的方法将在 Bean 从容器中移除前自动调用。
 * 
 * 使用规则：
 * - 只能标注在方法上
 * - 方法不能有参数
 * - 方法不能是静态方法
 * - 方法可以有任意的访问修饰符
 * - 一个类中只能有一个方法被 @PreDestroy 标注
 * 
 * 示例：
 * 
 * <pre>
 * {
 *   &#64;code
 *   &#64;Component
 *   public class DatabaseService {
 * 
 *     private Connection connection;
 * 
 *     &#64;PostConstruct
 *     public void init() {
 *       // 初始化数据库连接
 *       connection = DriverManager.getConnection("...");
 *     }
 * 
 *     @PreDestroy
 *     public void cleanup() {
 *       // 清理资源，关闭数据库连接
 *       if (connection != null) {
 *         try {
 *           connection.close();
 *         } catch (SQLException e) {
 *           // 处理异常
 *         }
 *       }
 *     }
 *   }
 * }
 * </pre>
 * 
 * @author SimpleSpring Framework
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreDestroy {
}
