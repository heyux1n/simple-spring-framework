package com.example.spring.core.annotation;

import com.simplespring.core.annotation.Bean;
import com.simplespring.core.annotation.Configuration;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * Bean 注解的单元测试
 * 
 * 测试 @Bean 注解的基本功能，包括：
 * 1. 注解的存在性验证
 * 2. 默认值的正确性
 * 3. 自定义值的设置和获取
 * 4. 方法级别的注解应用
 */
public class BeanTest {

    @Configuration
    static class TestConfig {
        
        @Bean
        public String defaultNameBean() {
            return "default";
        }
        
        @Bean("customBeanName")
        public String customNameBean() {
            return "custom";
        }
        
        @Bean("")
        public String emptyNameBean() {
            return "empty";
        }
        
        // 非 Bean 方法，用于对比测试
        public String regularMethod() {
            return "regular";
        }
    }

    @Test
    public void testBeanAnnotationExists() throws NoSuchMethodException {
        // 验证注解存在
        Method defaultMethod = TestConfig.class.getDeclaredMethod("defaultNameBean");
        assertTrue("@Bean 注解应该存在于 defaultNameBean 方法上", 
                   defaultMethod.isAnnotationPresent(Bean.class));
        
        Method customMethod = TestConfig.class.getDeclaredMethod("customNameBean");
        assertTrue("@Bean 注解应该存在于 customNameBean 方法上", 
                   customMethod.isAnnotationPresent(Bean.class));
        
        // 验证非 Bean 方法没有注解
        Method regularMethod = TestConfig.class.getDeclaredMethod("regularMethod");
        assertFalse("regularMethod 不应该有 @Bean 注解", 
                    regularMethod.isAnnotationPresent(Bean.class));
    }

    @Test
    public void testDefaultValue() throws NoSuchMethodException {
        // 测试默认值
        Method method = TestConfig.class.getDeclaredMethod("defaultNameBean");
        Bean annotation = method.getAnnotation(Bean.class);
        
        assertNotNull("@Bean 注解不应该为 null", annotation);
        assertEquals("默认值应该为空字符串", "", annotation.value());
    }

    @Test
    public void testCustomValue() throws NoSuchMethodException {
        // 测试自定义值
        Method method = TestConfig.class.getDeclaredMethod("customNameBean");
        Bean annotation = method.getAnnotation(Bean.class);
        
        assertNotNull("@Bean 注解不应该为 null", annotation);
        assertEquals("自定义值应该为 'customBeanName'", "customBeanName", annotation.value());
    }

    @Test
    public void testEmptyValue() throws NoSuchMethodException {
        // 测试显式设置的空值
        Method method = TestConfig.class.getDeclaredMethod("emptyNameBean");
        Bean annotation = method.getAnnotation(Bean.class);
        
        assertNotNull("@Bean 注解不应该为 null", annotation);
        assertEquals("显式设置的空值应该为空字符串", "", annotation.value());
    }

    @Test
    public void testBeanMethodReturnTypes() throws NoSuchMethodException {
        // 验证 Bean 方法可以有不同的返回类型
        Method stringMethod = TestConfig.class.getDeclaredMethod("defaultNameBean");
        assertTrue("String 返回类型的方法应该可以使用 @Bean", 
                   stringMethod.isAnnotationPresent(Bean.class));
        assertEquals("方法返回类型应该是 String", String.class, stringMethod.getReturnType());
    }
}
