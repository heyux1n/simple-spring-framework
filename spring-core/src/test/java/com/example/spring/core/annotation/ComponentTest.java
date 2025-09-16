package com.example.spring.core.annotation;

import com.simplespring.core.annotation.Component;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

/**
 * Component 注解的单元测试
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class ComponentTest {

    @Component
    static class DefaultComponent {
    }

    @Component("customName")
    static class NamedComponent {
    }

    @Test
    public void testComponentAnnotationPresent() {
        // 验证注解是否存在
        assertTrue("DefaultComponent 应该有 @Component 注解", 
                   DefaultComponent.class.isAnnotationPresent(Component.class));
        assertTrue("NamedComponent 应该有 @Component 注解", 
                   NamedComponent.class.isAnnotationPresent(Component.class));
    }

    @Test
    public void testDefaultValue() {
        // 验证默认值
        Component annotation = DefaultComponent.class.getAnnotation(Component.class);
        assertNotNull("注解不应该为 null", annotation);
        assertEquals("默认值应该为空字符串", "", annotation.value());
    }

    @Test
    public void testCustomValue() {
        // 验证自定义值
        Component annotation = NamedComponent.class.getAnnotation(Component.class);
        assertNotNull("注解不应该为 null", annotation);
        assertEquals("自定义值应该为 'customName'", "customName", annotation.value());
    }

    @Test
    public void testAnnotationRetention() {
        // 验证注解在运行时可用
        Annotation[] annotations = DefaultComponent.class.getAnnotations();
        boolean found = false;
        for (Annotation annotation : annotations) {
            if (annotation instanceof Component) {
                found = true;
                break;
            }
        }
        assertTrue("@Component 注解应该在运行时可用", found);
    }
}
