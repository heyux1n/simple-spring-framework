package com.example.spring.core.annotation;

import com.simplespring.core.annotation.Autowired;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Autowired 注解的单元测试
 * 
 * 测试 @Autowired 注解的基本功能，包括：
 * 1. 字段注入的注解验证
 * 2. 方法注入的注解验证
 * 3. 构造函数注入的注解验证
 * 4. required 属性的默认值和自定义值
 */
public class AutowiredTest {

    static class TestService {
    }

    static class TestController {
        
        @Autowired
        private TestService defaultRequiredService;
        
        @Autowired(required = false)
        private TestService optionalService;
        
        private TestService methodInjectedService;
        private TestService constructorInjectedService;
        
        public TestController() {
        }
        
        @Autowired
        public TestController(TestService service) {
            this.constructorInjectedService = service;
        }
        
        @Autowired
        public void setMethodInjectedService(TestService service) {
            this.methodInjectedService = service;
        }
        
        @Autowired(required = false)
        public void setOptionalMethodService(TestService service) {
            // 可选的方法注入
        }
    }

    @Test
    public void testFieldAutowired() throws NoSuchFieldException {
        // 测试字段上的 @Autowired 注解
        Field defaultField = TestController.class.getDeclaredField("defaultRequiredService");
        assertTrue("字段应该有 @Autowired 注解", 
                   defaultField.isAnnotationPresent(Autowired.class));
        
        Autowired annotation = defaultField.getAnnotation(Autowired.class);
        assertTrue("默认情况下 required 应该为 true", annotation.required());
        
        // 测试 required = false 的字段
        Field optionalField = TestController.class.getDeclaredField("optionalService");
        assertTrue("可选字段应该有 @Autowired 注解", 
                   optionalField.isAnnotationPresent(Autowired.class));
        
        Autowired optionalAnnotation = optionalField.getAnnotation(Autowired.class);
        assertFalse("可选字段的 required 应该为 false", optionalAnnotation.required());
    }

    @Test
    public void testMethodAutowired() throws NoSuchMethodException {
        // 测试方法上的 @Autowired 注解
        Method setterMethod = TestController.class.getDeclaredMethod("setMethodInjectedService", TestService.class);
        assertTrue("方法应该有 @Autowired 注解", 
                   setterMethod.isAnnotationPresent(Autowired.class));
        
        Autowired annotation = setterMethod.getAnnotation(Autowired.class);
        assertTrue("默认情况下 required 应该为 true", annotation.required());
        
        // 测试 required = false 的方法
        Method optionalMethod = TestController.class.getDeclaredMethod("setOptionalMethodService", TestService.class);
        assertTrue("可选方法应该有 @Autowired 注解", 
                   optionalMethod.isAnnotationPresent(Autowired.class));
        
        Autowired optionalAnnotation = optionalMethod.getAnnotation(Autowired.class);
        assertFalse("可选方法的 required 应该为 false", optionalAnnotation.required());
    }

    @Test
    public void testConstructorAutowired() throws NoSuchMethodException {
        // 测试构造函数上的 @Autowired 注解
        Constructor<TestController> constructor = TestController.class.getDeclaredConstructor(TestService.class);
        assertTrue("构造函数应该有 @Autowired 注解", 
                   constructor.isAnnotationPresent(Autowired.class));
        
        Autowired annotation = constructor.getAnnotation(Autowired.class);
        assertTrue("默认情况下 required 应该为 true", annotation.required());
    }

    @Test
    public void testDefaultRequiredValue() throws NoSuchFieldException {
        // 测试默认的 required 值
        Field field = TestController.class.getDeclaredField("defaultRequiredService");
        Autowired annotation = field.getAnnotation(Autowired.class);
        
        assertTrue("默认的 required 值应该为 true", annotation.required());
    }

    @Test
    public void testCustomRequiredValue() throws NoSuchFieldException {
        // 测试自定义的 required 值
        Field field = TestController.class.getDeclaredField("optionalService");
        Autowired annotation = field.getAnnotation(Autowired.class);
        
        assertFalse("自定义的 required 值应该为 false", annotation.required());
    }
}
