package com.example.spring.core.annotation;

import com.simplespring.core.annotation.RequestMapping;
import com.simplespring.core.annotation.RequestMethod;
import com.simplespring.core.annotation.Controller;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * RequestMapping 注解的单元测试
 * 
 * 测试 @RequestMapping 注解的基本功能，包括：
 * 1. 注解的存在性验证
 * 2. 路径值的设置和获取
 * 3. HTTP 方法的设置和获取
 * 4. 类级别和方法级别的注解应用
 */
public class RequestMappingTest {

    @Controller
    @RequestMapping("/api/v1")
    static class TestController {
        
        @RequestMapping("/users")
        public String defaultGetUsers() {
            return "user-list";
        }
        
        @RequestMapping(value = "/users", method = RequestMethod.POST)
        public String createUser() {
            return "user-created";
        }
        
        @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
        public String getUser() {
            return "user-detail";
        }
        
        @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
        public String updateUser() {
            return "user-updated";
        }
        
        @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
        public String deleteUser() {
            return "user-deleted";
        }
        
        @RequestMapping("")
        public String emptyPath() {
            return "empty";
        }
        
        // 非映射方法，用于对比测试
        public String regularMethod() {
            return "regular";
        }
    }

    @Controller
    static class SimpleController {
        
        @RequestMapping("/")
        public String home() {
            return "home";
        }
        
        @RequestMapping("/about")
        public String about() {
            return "about";
        }
    }

    @Test
    public void testClassLevelRequestMapping() {
        // 验证类级别的注解存在
        assertTrue("@RequestMapping 注解应该存在于 TestController 类上", 
                   TestController.class.isAnnotationPresent(RequestMapping.class));
        
        RequestMapping classAnnotation = TestController.class.getAnnotation(RequestMapping.class);
        assertEquals("类级别的路径应该为 '/api/v1'", "/api/v1", classAnnotation.value());
        assertEquals("类级别的默认方法应该为 GET", RequestMethod.GET, classAnnotation.method());
        
        // 验证没有类级别注解的控制器
        assertFalse("SimpleController 不应该有类级别的 @RequestMapping 注解", 
                    SimpleController.class.isAnnotationPresent(RequestMapping.class));
    }

    @Test
    public void testMethodLevelRequestMapping() throws NoSuchMethodException {
        // 验证方法级别的注解存在
        Method getUsersMethod = TestController.class.getDeclaredMethod("defaultGetUsers");
        assertTrue("@RequestMapping 注解应该存在于 defaultGetUsers 方法上", 
                   getUsersMethod.isAnnotationPresent(RequestMapping.class));
        
        Method createUserMethod = TestController.class.getDeclaredMethod("createUser");
        assertTrue("@RequestMapping 注解应该存在于 createUser 方法上", 
                   createUserMethod.isAnnotationPresent(RequestMapping.class));
        
        // 验证非映射方法没有注解
        Method regularMethod = TestController.class.getDeclaredMethod("regularMethod");
        assertFalse("regularMethod 不应该有 @RequestMapping 注解", 
                    regularMethod.isAnnotationPresent(RequestMapping.class));
    }

    @Test
    public void testDefaultValues() throws NoSuchMethodException {
        // 测试默认值
        Method method = TestController.class.getDeclaredMethod("defaultGetUsers");
        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
        
        assertEquals("路径值应该为 '/users'", "/users", annotation.value());
        assertEquals("默认 HTTP 方法应该为 GET", RequestMethod.GET, annotation.method());
    }

    @Test
    public void testCustomValues() throws NoSuchMethodException {
        // 测试自定义值
        Method method = TestController.class.getDeclaredMethod("createUser");
        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
        
        assertEquals("路径值应该为 '/users'", "/users", annotation.value());
        assertEquals("HTTP 方法应该为 POST", RequestMethod.POST, annotation.method());
    }

    @Test
    public void testDifferentHttpMethods() throws NoSuchMethodException {
        // 测试不同的 HTTP 方法
        Method getMethod = TestController.class.getDeclaredMethod("getUser");
        RequestMapping getAnnotation = getMethod.getAnnotation(RequestMapping.class);
        assertEquals("GET 方法应该正确", RequestMethod.GET, getAnnotation.method());
        
        Method putMethod = TestController.class.getDeclaredMethod("updateUser");
        RequestMapping putAnnotation = putMethod.getAnnotation(RequestMapping.class);
        assertEquals("PUT 方法应该正确", RequestMethod.PUT, putAnnotation.method());
        
        Method deleteMethod = TestController.class.getDeclaredMethod("deleteUser");
        RequestMapping deleteAnnotation = deleteMethod.getAnnotation(RequestMapping.class);
        assertEquals("DELETE 方法应该正确", RequestMethod.DELETE, deleteAnnotation.method());
    }

    @Test
    public void testPathParameters() throws NoSuchMethodException {
        // 测试路径参数
        Method method = TestController.class.getDeclaredMethod("getUser");
        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
        
        assertEquals("路径参数应该正确", "/users/{id}", annotation.value());
        assertTrue("路径应该包含参数占位符", annotation.value().contains("{id}"));
    }

    @Test
    public void testEmptyPath() throws NoSuchMethodException {
        // 测试空路径
        Method method = TestController.class.getDeclaredMethod("emptyPath");
        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
        
        assertEquals("空路径应该为空字符串", "", annotation.value());
    }

    @Test
    public void testAnnotationProperties() throws NoSuchMethodException {
        // 验证注解的基本属性
        Method method = TestController.class.getDeclaredMethod("defaultGetUsers");
        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
        
        // 验证注解类型
        assertEquals("注解类型应该是 RequestMapping", RequestMapping.class, annotation.annotationType());
        
        // 验证 toString 方法不为空
        assertNotNull("toString() 方法不应该返回 null", annotation.toString());
        assertTrue("toString() 方法应该包含注解信息", 
                   annotation.toString().contains("RequestMapping"));
    }

    @Test
    public void testDifferentAnnotationValues() throws NoSuchMethodException {
        // 测试不同值的注解不相等
        Method getUsersMethod = TestController.class.getDeclaredMethod("defaultGetUsers");
        Method createUserMethod = TestController.class.getDeclaredMethod("createUser");
        
        RequestMapping getUsersAnnotation = getUsersMethod.getAnnotation(RequestMapping.class);
        RequestMapping createUserAnnotation = createUserMethod.getAnnotation(RequestMapping.class);
        
        // 虽然路径相同，但 HTTP 方法不同，所以注解不相等
        assertNotEquals("不同 HTTP 方法的注解应该不相等", getUsersAnnotation, createUserAnnotation);
        assertEquals("路径值应该相同", getUsersAnnotation.value(), createUserAnnotation.value());
        assertNotEquals("HTTP 方法应该不同", getUsersAnnotation.method(), createUserAnnotation.method());
    }

    @Test
    public void testSimpleControllerMethods() throws NoSuchMethodException {
        // 测试简单控制器的方法
        Method homeMethod = SimpleController.class.getDeclaredMethod("home");
        RequestMapping homeAnnotation = homeMethod.getAnnotation(RequestMapping.class);
        assertEquals("首页路径应该为 '/'", "/", homeAnnotation.value());
        assertEquals("默认方法应该为 GET", RequestMethod.GET, homeAnnotation.method());
        
        Method aboutMethod = SimpleController.class.getDeclaredMethod("about");
        RequestMapping aboutAnnotation = aboutMethod.getAnnotation(RequestMapping.class);
        assertEquals("关于页面路径应该为 '/about'", "/about", aboutAnnotation.value());
        assertEquals("默认方法应该为 GET", RequestMethod.GET, aboutAnnotation.method());
    }
}
