package com.example.spring.core.annotation;

import com.simplespring.core.annotation.Before;
import com.simplespring.core.annotation.Aspect;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * Before 注解的单元测试
 * 
 * 测试 @Before 注解的基本功能，包括：
 * 1. 注解的存在性验证
 * 2. 切点表达式的设置和获取
 * 3. 方法级别的注解应用
 */
public class BeforeTest {

    @Aspect
    static class TestAspect {
        
        @Before("execution(* com.example.service.*.*(..))")
        public void beforeAllServiceMethods() {
            // 匹配所有服务方法的前置通知
        }
        
        @Before("execution(* com.example.service.UserService.findUser(..))")
        public void beforeFindUser() {
            // 匹配特定方法的前置通知
        }
        
        @Before("execution(* com.example.controller.*.*(..))")
        public void beforeControllerMethods() {
            // 匹配控制器方法的前置通知
        }
        
        // 非通知方法，用于对比测试
        public void regularMethod() {
            // 普通方法
        }
    }

    @Test
    public void testBeforeAnnotationExists() throws NoSuchMethodException {
        // 验证注解存在
        Method serviceMethod = TestAspect.class.getDeclaredMethod("beforeAllServiceMethods");
        assertTrue("@Before 注解应该存在于 beforeAllServiceMethods 方法上", 
                   serviceMethod.isAnnotationPresent(Before.class));
        
        Method userMethod = TestAspect.class.getDeclaredMethod("beforeFindUser");
        assertTrue("@Before 注解应该存在于 beforeFindUser 方法上", 
                   userMethod.isAnnotationPresent(Before.class));
        
        // 验证非通知方法没有注解
        Method regularMethod = TestAspect.class.getDeclaredMethod("regularMethod");
        assertFalse("regularMethod 不应该有 @Before 注解", 
                    regularMethod.isAnnotationPresent(Before.class));
    }

    @Test
    public void testPointcutExpressions() throws NoSuchMethodException {
        // 测试不同的切点表达式
        Method serviceMethod = TestAspect.class.getDeclaredMethod("beforeAllServiceMethods");
        Before serviceAnnotation = serviceMethod.getAnnotation(Before.class);
        assertEquals("服务方法的切点表达式应该正确", 
                     "execution(* com.example.service.*.*(..))", serviceAnnotation.value());
        
        Method userMethod = TestAspect.class.getDeclaredMethod("beforeFindUser");
        Before userAnnotation = userMethod.getAnnotation(Before.class);
        assertEquals("用户方法的切点表达式应该正确", 
                     "execution(* com.example.service.UserService.findUser(..))", userAnnotation.value());
        
        Method controllerMethod = TestAspect.class.getDeclaredMethod("beforeControllerMethods");
        Before controllerAnnotation = controllerMethod.getAnnotation(Before.class);
        assertEquals("控制器方法的切点表达式应该正确", 
                     "execution(* com.example.controller.*.*(..))", controllerAnnotation.value());
    }

    @Test
    public void testAnnotationProperties() throws NoSuchMethodException {
        // 验证注解的基本属性
        Method method = TestAspect.class.getDeclaredMethod("beforeAllServiceMethods");
        Before annotation = method.getAnnotation(Before.class);
        
        // 验证注解类型
        assertEquals("注解类型应该是 Before", Before.class, annotation.annotationType());
        
        // 验证 toString 方法不为空
        assertNotNull("toString() 方法不应该返回 null", annotation.toString());
        assertTrue("toString() 方法应该包含注解信息", 
                   annotation.toString().contains("Before"));
    }

    @Test
    public void testDifferentPointcutExpressions() throws NoSuchMethodException {
        // 测试不同切点表达式的注解不相等
        Method serviceMethod = TestAspect.class.getDeclaredMethod("beforeAllServiceMethods");
        Method userMethod = TestAspect.class.getDeclaredMethod("beforeFindUser");
        
        Before serviceAnnotation = serviceMethod.getAnnotation(Before.class);
        Before userAnnotation = userMethod.getAnnotation(Before.class);
        
        assertNotEquals("不同切点表达式的注解应该不相等", serviceAnnotation, userAnnotation);
        assertNotEquals("不同切点表达式的注解应该有不同的 value", 
                        serviceAnnotation.value(), userAnnotation.value());
    }

    @Test
    public void testEmptyPointcutExpression() {
        // 测试空切点表达式的处理
        class TestEmptyPointcut {
            @Before("")
            public void emptyPointcut() {
            }
        }
        
        try {
            Method method = TestEmptyPointcut.class.getDeclaredMethod("emptyPointcut");
            Before annotation = method.getAnnotation(Before.class);
            assertEquals("空切点表达式应该为空字符串", "", annotation.value());
        } catch (NoSuchMethodException e) {
            fail("应该能够找到 emptyPointcut 方法");
        }
    }
}
