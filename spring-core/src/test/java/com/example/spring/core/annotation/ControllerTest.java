package com.example.spring.core.annotation;

import com.simplespring.core.annotation.Controller;
import com.simplespring.core.annotation.RequestMapping;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Controller 注解的单元测试
 * 
 * 测试 @Controller 注解的基本功能，包括：
 * 1. 注解的存在性验证
 * 2. 默认值的正确性
 * 3. 自定义值的设置和获取
 * 4. 与 @RequestMapping 注解的配合使用
 */
public class ControllerTest {

    @Controller
    static class DefaultController {
        
        @RequestMapping("/test")
        public String testMethod() {
            return "test";
        }
    }

    @Controller("customUserController")
    static class CustomNamedController {
        
        @RequestMapping("/users")
        public String listUsers() {
            return "user-list";
        }
    }

    @Controller("")
    static class EmptyNameController {
    }

    static class NonControllerClass {
        public void regularMethod() {
        }
    }

    @Test
    public void testControllerAnnotationExists() {
        // 验证注解存在
        assertTrue("@Controller 注解应该存在于 DefaultController 类上", 
                   DefaultController.class.isAnnotationPresent(Controller.class));
        assertTrue("@Controller 注解应该存在于 CustomNamedController 类上", 
                   CustomNamedController.class.isAnnotationPresent(Controller.class));
        
        // 验证非控制器类没有注解
        assertFalse("NonControllerClass 不应该有 @Controller 注解", 
                    NonControllerClass.class.isAnnotationPresent(Controller.class));
    }

    @Test
    public void testDefaultValue() {
        // 测试默认值
        Controller annotation = DefaultController.class.getAnnotation(Controller.class);
        assertNotNull("@Controller 注解不应该为 null", annotation);
        assertEquals("默认值应该为空字符串", "", annotation.value());
    }

    @Test
    public void testCustomValue() {
        // 测试自定义值
        Controller annotation = CustomNamedController.class.getAnnotation(Controller.class);
        assertNotNull("@Controller 注解不应该为 null", annotation);
        assertEquals("自定义值应该为 'customUserController'", "customUserController", annotation.value());
    }

    @Test
    public void testEmptyValue() {
        // 测试显式设置的空值
        Controller annotation = EmptyNameController.class.getAnnotation(Controller.class);
        assertNotNull("@Controller 注解不应该为 null", annotation);
        assertEquals("显式设置的空值应该为空字符串", "", annotation.value());
    }

    @Test
    public void testAnnotationProperties() {
        // 验证注解的基本属性
        Controller annotation = DefaultController.class.getAnnotation(Controller.class);
        
        // 验证注解类型
        assertEquals("注解类型应该是 Controller", Controller.class, annotation.annotationType());
        
        // 验证 toString 方法不为空
        assertNotNull("toString() 方法不应该返回 null", annotation.toString());
        assertTrue("toString() 方法应该包含注解信息", 
                   annotation.toString().contains("Controller"));
    }

    @Test
    public void testControllerWithRequestMapping() throws NoSuchMethodException {
        // 验证控制器类中的请求映射方法有正确的注解
        assertTrue("控制器类应该有 @Controller 注解", 
                   DefaultController.class.isAnnotationPresent(Controller.class));
        
        // 验证请求映射方法有 @RequestMapping 注解
        assertTrue("testMethod 方法应该有 @RequestMapping 注解", 
                   DefaultController.class.getDeclaredMethod("testMethod")
                           .isAnnotationPresent(RequestMapping.class));
        
        assertTrue("listUsers 方法应该有 @RequestMapping 注解", 
                   CustomNamedController.class.getDeclaredMethod("listUsers")
                           .isAnnotationPresent(RequestMapping.class));
    }

    @Test
    public void testAnnotationEquality() {
        // 测试相同注解的相等性
        Controller annotation1 = DefaultController.class.getAnnotation(Controller.class);
        Controller annotation2 = DefaultController.class.getAnnotation(Controller.class);
        
        assertEquals("相同的注解实例应该相等", annotation1, annotation2);
        assertEquals("相同的注解实例应该有相同的 hashCode", 
                     annotation1.hashCode(), annotation2.hashCode());
    }

    @Test
    public void testDifferentAnnotationValues() {
        // 测试不同值的注解不相等
        Controller defaultAnnotation = DefaultController.class.getAnnotation(Controller.class);
        Controller customAnnotation = CustomNamedController.class.getAnnotation(Controller.class);
        
        assertNotEquals("不同值的注解应该不相等", defaultAnnotation, customAnnotation);
        assertNotEquals("不同值的注解应该有不同的 value", 
                        defaultAnnotation.value(), customAnnotation.value());
    }

    @Test
    public void testMultipleControllerClasses() {
        @Controller
        class AnotherController {
        }
        
        // 验证多个控制器类都可以正确标注
        assertTrue("DefaultController 应该有 @Controller 注解", 
                   DefaultController.class.isAnnotationPresent(Controller.class));
        assertTrue("AnotherController 应该有 @Controller 注解", 
                   AnotherController.class.isAnnotationPresent(Controller.class));
        
        // 验证它们是独立的注解实例
        Controller controller1 = DefaultController.class.getAnnotation(Controller.class);
        Controller controller2 = AnotherController.class.getAnnotation(Controller.class);
        
        assertNotNull("DefaultController 的注解不应该为 null", controller1);
        assertNotNull("AnotherController 的注解不应该为 null", controller2);
    }
}
