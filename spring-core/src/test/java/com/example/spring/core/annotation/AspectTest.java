package com.example.spring.core.annotation;

import com.simplespring.core.annotation.Aspect;
import com.simplespring.core.annotation.Before;
import com.simplespring.core.annotation.After;
import com.simplespring.core.annotation.AfterReturning;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Aspect 注解的单元测试
 * 
 * 测试 @Aspect 注解的基本功能，包括：
 * 1. 注解的存在性验证
 * 2. 切面类的识别
 * 3. 与其他 AOP 注解的配合使用
 */
public class AspectTest {

    @Aspect
    static class TestAspect {
        
        @Before("execution(* com.example.service.*.*(..))")
        public void beforeAdvice() {
            // 前置通知
        }
        
        @After("execution(* com.example.service.*.*(..))")
        public void afterAdvice() {
            // 后置通知
        }
        
        @AfterReturning(value = "execution(* com.example.service.*.*(..))", returning = "result")
        public void afterReturningAdvice(Object result) {
            // 返回后通知
        }
    }

    static class NonAspectClass {
        public void regularMethod() {
            // 普通方法
        }
    }

    @Test
    public void testAspectAnnotationExists() {
        // 验证注解存在
        assertTrue("@Aspect 注解应该存在于 TestAspect 类上", 
                   TestAspect.class.isAnnotationPresent(Aspect.class));
        
        // 验证非切面类没有注解
        assertFalse("NonAspectClass 不应该有 @Aspect 注解", 
                    NonAspectClass.class.isAnnotationPresent(Aspect.class));
    }

    @Test
    public void testAnnotationRetrieval() {
        // 测试注解获取
        Aspect annotation = TestAspect.class.getAnnotation(Aspect.class);
        assertNotNull("@Aspect 注解不应该为 null", annotation);
    }

    @Test
    public void testAspectWithAdviceMethods() throws NoSuchMethodException {
        // 验证切面类中的通知方法有正确的注解
        assertTrue("切面类应该有 @Aspect 注解", 
                   TestAspect.class.isAnnotationPresent(Aspect.class));
        
        // 验证前置通知方法
        assertTrue("beforeAdvice 方法应该有 @Before 注解", 
                   TestAspect.class.getDeclaredMethod("beforeAdvice").isAnnotationPresent(Before.class));
        
        // 验证后置通知方法
        assertTrue("afterAdvice 方法应该有 @After 注解", 
                   TestAspect.class.getDeclaredMethod("afterAdvice").isAnnotationPresent(After.class));
        
        // 验证返回后通知方法
        assertTrue("afterReturningAdvice 方法应该有 @AfterReturning 注解", 
                   TestAspect.class.getDeclaredMethod("afterReturningAdvice", Object.class)
                           .isAnnotationPresent(AfterReturning.class));
    }

    @Test
    public void testMultipleAspectClasses() {
        @Aspect
        class AnotherAspect {
        }
        
        // 验证多个切面类都可以正确标注
        assertTrue("TestAspect 应该有 @Aspect 注解", 
                   TestAspect.class.isAnnotationPresent(Aspect.class));
        assertTrue("AnotherAspect 应该有 @Aspect 注解", 
                   AnotherAspect.class.isAnnotationPresent(Aspect.class));
        
        // 验证它们是独立的注解实例
        Aspect aspect1 = TestAspect.class.getAnnotation(Aspect.class);
        Aspect aspect2 = AnotherAspect.class.getAnnotation(Aspect.class);
        
        assertNotNull("TestAspect 的注解不应该为 null", aspect1);
        assertNotNull("AnotherAspect 的注解不应该为 null", aspect2);
    }

    @Test
    public void testAnnotationEquality() {
        // 测试相同注解的相等性
        Aspect annotation1 = TestAspect.class.getAnnotation(Aspect.class);
        Aspect annotation2 = TestAspect.class.getAnnotation(Aspect.class);
        
        assertEquals("相同的注解实例应该相等", annotation1, annotation2);
        assertEquals("相同的注解实例应该有相同的 hashCode", 
                     annotation1.hashCode(), annotation2.hashCode());
    }
}
