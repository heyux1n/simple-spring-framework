package com.simplespring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组件注解，用于标识一个类为 Spring 管理的组件
 * 
 * 当类被 @Component 注解标记时，Spring IoC 容器会自动扫描并创建该类的实例，
 * 将其注册为一个 Bean 并管理其生命周期。
 * 
 * 该注解支持可选的 Bean 名称指定，如果不指定则使用类名的首字母小写形式作为默认名称。
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @Component
 * public class UserService {
 *     // 服务实现
 * }
 * 
 * @Component("customUserService")
 * public class CustomUserService {
 *     // 自定义服务实现
 * }
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    
    /**
     * 指定 Bean 的名称
     * 
     * @return Bean 名称，如果为空字符串则使用默认命名规则（类名首字母小写）
     */
    String value() default "";
}
