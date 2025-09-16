package com.example.spring.core.annotation;

import com.simplespring.core.annotation.After;
import com.simplespring.core.annotation.Aspect;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * After 注解的单元测试
 * 
 * 测试 @After 注解的基本功能，包括：
 * 1. 注解的存在性验证
 * 2. 切点表达式的设置和获取
 * 3. 方法级别的注解应用
 */
public class AfterTest {

    @Aspect
    static class TestAspect {
        
        @After("execution(* com.example.service.*.*(..))")
        public void afterAllServiceMethods() {
            // 匹配所有服务方法的后置通知
        }
        
        @After("execution(* com.example.service.DatabaseService.*(..))")
        public void afterDatabaseOperations() {
            // 匹配数据库操作的后置通知
        }
        
        @After("execution(* com.example.service.UserService.updateUser(..))")
        public void afterUserUpdate() {
            // 匹配用户更新的后置通知
        }
        
        // 非通知方法，用于对比测试
        public void regularMethod() {
            // 普通方法
        }
    }

    @Test
    public void testAfterAnnotationExists() throws NoSuchMethodException {
        // 验证注解存在
        Method serviceMethod = TestAspect.class.getDeclaredMethod("afterAllServiceMethods");
        assertTrue("@After 注解应该存在于 afterAllServiceMethods 方法上", 
                   serviceMethod.isAnnotationPresent(After.class));
        
        Method databaseMethod = TestAspect.class.getDeclaredMethod("afterDatabaseOperations");
        assertTrue("@After 注解应该存在于 afterDatabaseOperations 方法上", 
                   databaseMethod.isAnnotationPresent(After.class));
        
        // 验证非通知方法没有注解
        Method regularMethod = TestAspect.class.getDeclaredMethod("regularMethod");
        assertFalse("regularMethod 不应该有 @After 注解", 
                    regularMethod.isAnnotationPresent(After.class));
    }

    @Test
    public void testPointcutExpressions() throws NoSuchMethodException {
        // 测试不同的切点表达式
        Method serviceMethod = TestAspect.class.getDeclaredMethod("afterAllServiceMethods");
        After serviceAnnotation = serviceMethod.getAnnotation(After.class);
        assertEquals("服务方法的切点表达式应该正确", 
                     "execution(* com.example.service.*.*(..))", serviceAnnotation.value());
        
        Method databaseMethod = TestAspect.class.getDeclaredMethod("afterDatabaseOperations");
        After databaseAnnotation = databaseMethod.getAnnotation(After.class);
        assertEquals("数据库方法的切点表达式应该正确", 
                     "execution(* com.example.service.DatabaseService.*(..))", databaseAnnotation.value());
        
        Method userMethod = TestAspect.class.getDeclaredMethod("afterUserUpdate");
        After userAnnotation = userMethod.getAnnotation(After.class);
        assertEquals("用户更新方法的切点表达式应该正确", 
                     "execution(* com.example.service.UserService.updateUser(..))", userAnnotation.value());
    }

    @Test
    public void testAnnotationProperties() throws NoSuchMethodException {
        // 验证注解的基本属性
        Method method = TestAspect.class.getDeclaredMethod("afterAllServiceMethods");
        After annotation = method.getAnnotation(After.class);
        
        // 验证注解类型
        assertEquals("注解类型应该是 After", After.class, annotation.annotationType());
        
        // 验证 toString 方法不为空
        assertNotNull("toString() 方法不应该返回 null", annotation.toString());
        assertTrue("toString() 方法应该包含注解信息", 
                   annotation.toString().contains("After"));
    }

    @Test
    public void testDifferentPointcutExpressions() throws NoSuchMethodException {
        // 测试不同切点表达式的注解不相等
        Method serviceMethod = TestAspect.class.getDeclaredMethod("afterAllServiceMethods");
        Method databaseMethod = TestAspect.class.getDeclaredMethod("afterDatabaseOperations");
        
        After serviceAnnotation = serviceMethod.getAnnotation(After.class);
        After databaseAnnotation = databaseMethod.getAnnotation(After.class);
        
        assertNotEquals("不同切点表达式的注解应该不相等", serviceAnnotation, databaseAnnotation);
        assertNotEquals("不同切点表达式的注解应该有不同的 value", 
                        serviceAnnotation.value(), databaseAnnotation.value());
    }

    @Test
    public void testAnnotationEquality() throws NoSuchMethodException {
        // 测试相同注解的相等性
        Method method = TestAspect.class.getDeclaredMethod("afterAllServiceMethods");
        After annotation1 = method.getAnnotation(After.class);
        After annotation2 = method.getAnnotation(After.class);
        
        assertEquals("相同的注解实例应该相等", annotation1, annotation2);
        assertEquals("相同的注解实例应该有相同的 hashCode", 
                     annotation1.hashCode(), annotation2.hashCode());
    }

    @Test
    public void testComplexPointcutExpressions() {
        // 测试复杂切点表达式的处理
        class TestComplexPointcut {
            @After("execution(public * com.example.service.*.find*(..))")
            public void complexPointcut() {
            }
        }
        
        try {
            Method method = TestComplexPointcut.class.getDeclaredMethod("complexPointcut");
            After annotation = method.getAnnotation(After.class);
            assertEquals("复杂切点表达式应该正确保存", 
                         "execution(public * com.example.service.*.find*(..))", annotation.value());
        } catch (NoSuchMethodException e) {
            fail("应该能够找到 complexPointcut 方法");
        }
    }
}
