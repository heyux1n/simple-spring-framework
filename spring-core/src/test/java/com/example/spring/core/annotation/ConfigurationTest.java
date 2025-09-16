package com.example.spring.core.annotation;

import com.simplespring.core.annotation.Configuration;
import com.simplespring.core.annotation.Bean;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Configuration 注解的单元测试
 * 
 * 测试 @Configuration 注解的基本功能，包括：
 * 1. 注解的存在性验证
 * 2. 注解的目标和保留策略
 * 3. 配置类的识别
 */
public class ConfigurationTest {

    @Configuration
    static class TestConfig {
        
        @Bean
        public String testBean() {
            return "test";
        }
    }

    static class NonConfigClass {
    }

    @Test
    public void testConfigurationAnnotationExists() {
        // 验证注解存在
        assertTrue("@Configuration 注解应该存在于 TestConfig 类上", 
                   TestConfig.class.isAnnotationPresent(Configuration.class));
        
        // 验证非配置类没有注解
        assertFalse("NonConfigClass 不应该有 @Configuration 注解", 
                    NonConfigClass.class.isAnnotationPresent(Configuration.class));
    }

    @Test
    public void testAnnotationRetrieval() {
        // 测试注解获取
        Configuration annotation = TestConfig.class.getAnnotation(Configuration.class);
        assertNotNull("@Configuration 注解不应该为 null", annotation);
    }

    @Test
    public void testConfigurationWithBeanMethods() throws NoSuchMethodException {
        // 验证配置类中的 Bean 方法也有正确的注解
        assertTrue("配置类应该有 @Configuration 注解", 
                   TestConfig.class.isAnnotationPresent(Configuration.class));
        
        // 验证 Bean 方法有 @Bean 注解
        assertTrue("testBean 方法应该有 @Bean 注解", 
                   TestConfig.class.getDeclaredMethod("testBean").isAnnotationPresent(Bean.class));
    }

    @Test
    public void testMultipleConfigurationClasses() {
        @Configuration
        class AnotherConfig {
        }
        
        // 验证多个配置类都可以正确标注
        assertTrue("TestConfig 应该有 @Configuration 注解", 
                   TestConfig.class.isAnnotationPresent(Configuration.class));
        assertTrue("AnotherConfig 应该有 @Configuration 注解", 
                   AnotherConfig.class.isAnnotationPresent(Configuration.class));
        
        // 验证它们是独立的注解实例
        Configuration config1 = TestConfig.class.getAnnotation(Configuration.class);
        Configuration config2 = AnotherConfig.class.getAnnotation(Configuration.class);
        
        assertNotNull("TestConfig 的注解不应该为 null", config1);
        assertNotNull("AnotherConfig 的注解不应该为 null", config2);
    }
}
