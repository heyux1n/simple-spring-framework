package com.example.spring.core.annotation;

import com.simplespring.core.annotation.AfterReturning;
import com.simplespring.core.annotation.Aspect;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * AfterReturning 注解的单元测试
 * 
 * 测试 @AfterReturning 注解的基本功能，包括：
 * 1. 注解的存在性验证
 * 2. 切点表达式的设置和获取
 * 3. returning 属性的设置和获取
 * 4. 方法级别的注解应用
 */
public class AfterReturningTest {

    @Aspect
    static class TestAspect {
        
        @AfterReturning("execution(* com.example.service.*.*(..))")
        public void afterReturningAllServiceMethods() {
            // 匹配所有服务方法的返回后通知（不接收返回值）
        }
        
        @AfterReturning(value = "execution(* com.example.service.UserService.findUser(..))", 
                        returning = "user")
        public void afterReturningFindUser(Object user) {
            // 匹配用户查询方法的返回后通知（接收返回值）
        }
        
        @AfterReturning(value = "execution(* com.example.service.OrderService.createOrder(..))", 
                        returning = "orderId")
        public void afterReturningCreateOrder(Long orderId) {
            // 匹配订单创建方法的返回后通知（接收特定类型返回值）
        }
        
        @AfterReturning(value = "execution(* com.example.service.*.save*(..))", 
                        returning = "")
        public void afterReturningSaveMethods() {
            // 匹配保存方法的返回后通知（显式设置空 returning）
        }
        
        // 非通知方法，用于对比测试
        public void regularMethod() {
            // 普通方法
        }
    }

    @Test
    public void testAfterReturningAnnotationExists() throws NoSuchMethodException {
        // 验证注解存在
        Method serviceMethod = TestAspect.class.getDeclaredMethod("afterReturningAllServiceMethods");
        assertTrue("@AfterReturning 注解应该存在于 afterReturningAllServiceMethods 方法上", 
                   serviceMethod.isAnnotationPresent(AfterReturning.class));
        
        Method userMethod = TestAspect.class.getDeclaredMethod("afterReturningFindUser", Object.class);
        assertTrue("@AfterReturning 注解应该存在于 afterReturningFindUser 方法上", 
                   userMethod.isAnnotationPresent(AfterReturning.class));
        
        // 验证非通知方法没有注解
        Method regularMethod = TestAspect.class.getDeclaredMethod("regularMethod");
        assertFalse("regularMethod 不应该有 @AfterReturning 注解", 
                    regularMethod.isAnnotationPresent(AfterReturning.class));
    }

    @Test
    public void testPointcutExpressions() throws NoSuchMethodException {
        // 测试不同的切点表达式
        Method serviceMethod = TestAspect.class.getDeclaredMethod("afterReturningAllServiceMethods");
        AfterReturning serviceAnnotation = serviceMethod.getAnnotation(AfterReturning.class);
        assertEquals("服务方法的切点表达式应该正确", 
                     "execution(* com.example.service.*.*(..))", serviceAnnotation.value());
        
        Method userMethod = TestAspect.class.getDeclaredMethod("afterReturningFindUser", Object.class);
        AfterReturning userAnnotation = userMethod.getAnnotation(AfterReturning.class);
        assertEquals("用户方法的切点表达式应该正确", 
                     "execution(* com.example.service.UserService.findUser(..))", userAnnotation.value());
        
        Method orderMethod = TestAspect.class.getDeclaredMethod("afterReturningCreateOrder", Long.class);
        AfterReturning orderAnnotation = orderMethod.getAnnotation(AfterReturning.class);
        assertEquals("订单方法的切点表达式应该正确", 
                     "execution(* com.example.service.OrderService.createOrder(..))", orderAnnotation.value());
    }

    @Test
    public void testReturningAttribute() throws NoSuchMethodException {
        // 测试 returning 属性
        Method serviceMethod = TestAspect.class.getDeclaredMethod("afterReturningAllServiceMethods");
        AfterReturning serviceAnnotation = serviceMethod.getAnnotation(AfterReturning.class);
        assertEquals("默认 returning 应该为空字符串", "", serviceAnnotation.returning());
        
        Method userMethod = TestAspect.class.getDeclaredMethod("afterReturningFindUser", Object.class);
        AfterReturning userAnnotation = userMethod.getAnnotation(AfterReturning.class);
        assertEquals("用户方法的 returning 应该为 'user'", "user", userAnnotation.returning());
        
        Method orderMethod = TestAspect.class.getDeclaredMethod("afterReturningCreateOrder", Long.class);
        AfterReturning orderAnnotation = orderMethod.getAnnotation(AfterReturning.class);
        assertEquals("订单方法的 returning 应该为 'orderId'", "orderId", orderAnnotation.returning());
        
        Method saveMethod = TestAspect.class.getDeclaredMethod("afterReturningSaveMethods");
        AfterReturning saveAnnotation = saveMethod.getAnnotation(AfterReturning.class);
        assertEquals("显式设置的空 returning 应该为空字符串", "", saveAnnotation.returning());
    }

    @Test
    public void testAnnotationProperties() throws NoSuchMethodException {
        // 验证注解的基本属性
        Method method = TestAspect.class.getDeclaredMethod("afterReturningFindUser", Object.class);
        AfterReturning annotation = method.getAnnotation(AfterReturning.class);
        
        // 验证注解类型
        assertEquals("注解类型应该是 AfterReturning", AfterReturning.class, annotation.annotationType());
        
        // 验证 toString 方法不为空
        assertNotNull("toString() 方法不应该返回 null", annotation.toString());
        assertTrue("toString() 方法应该包含注解信息", 
                   annotation.toString().contains("AfterReturning"));
    }

    @Test
    public void testDifferentAnnotationValues() throws NoSuchMethodException {
        // 测试不同值的注解不相等
        Method serviceMethod = TestAspect.class.getDeclaredMethod("afterReturningAllServiceMethods");
        Method userMethod = TestAspect.class.getDeclaredMethod("afterReturningFindUser", Object.class);
        
        AfterReturning serviceAnnotation = serviceMethod.getAnnotation(AfterReturning.class);
        AfterReturning userAnnotation = userMethod.getAnnotation(AfterReturning.class);
        
        assertNotEquals("不同值的注解应该不相等", serviceAnnotation, userAnnotation);
        assertNotEquals("不同切点表达式的注解应该有不同的 value", 
                        serviceAnnotation.value(), userAnnotation.value());
        assertNotEquals("不同 returning 的注解应该有不同的 returning", 
                        serviceAnnotation.returning(), userAnnotation.returning());
    }

    @Test
    public void testAnnotationEquality() throws NoSuchMethodException {
        // 测试相同注解的相等性
        Method method = TestAspect.class.getDeclaredMethod("afterReturningAllServiceMethods");
        AfterReturning annotation1 = method.getAnnotation(AfterReturning.class);
        AfterReturning annotation2 = method.getAnnotation(AfterReturning.class);
        
        assertEquals("相同的注解实例应该相等", annotation1, annotation2);
        assertEquals("相同的注解实例应该有相同的 hashCode", 
                     annotation1.hashCode(), annotation2.hashCode());
    }

    @Test
    public void testComplexReturningScenarios() {
        // 测试复杂的 returning 场景
        class TestComplexReturning {
            @AfterReturning(value = "execution(* com.example.service.*.find*(..))", 
                            returning = "result")
            public void complexReturning(Object result) {
            }
            
            @AfterReturning(value = "execution(* com.example.service.*.count*(..))", 
                            returning = "count")
            public void countReturning(Integer count) {
            }
        }
        
        try {
            Method complexMethod = TestComplexReturning.class.getDeclaredMethod("complexReturning", Object.class);
            AfterReturning complexAnnotation = complexMethod.getAnnotation(AfterReturning.class);
            assertEquals("复杂 returning 应该正确", "result", complexAnnotation.returning());
            
            Method countMethod = TestComplexReturning.class.getDeclaredMethod("countReturning", Integer.class);
            AfterReturning countAnnotation = countMethod.getAnnotation(AfterReturning.class);
            assertEquals("计数 returning 应该正确", "count", countAnnotation.returning());
        } catch (NoSuchMethodException e) {
            fail("应该能够找到测试方法");
        }
    }
}
