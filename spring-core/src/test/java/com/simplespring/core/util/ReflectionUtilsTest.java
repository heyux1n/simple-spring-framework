package com.simplespring.core.util;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * ReflectionUtils 工具类的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class ReflectionUtilsTest {

    // 测试用的内部类
    private static class TestClass {
        private String privateField;
        public String publicField;
        protected String protectedField;
        
        private String privateMethod() {
            return "private";
        }
        
        public String publicMethod() {
            return "public";
        }
        
        public String methodWithParams(String param1, int param2) {
            return param1 + param2;
        }
        
        public void setPrivateField(String value) {
            this.privateField = value;
        }
        
        public String getPrivateField() {
            return this.privateField;
        }
    }

    @Test
    public void testFindField() {
        // 测试查找私有字段
        Field privateField = ReflectionUtils.findField(TestClass.class, "privateField");
        assertNotNull(privateField);
        assertEquals("privateField", privateField.getName());
        assertEquals(String.class, privateField.getType());
        
        // 测试查找公共字段
        Field publicField = ReflectionUtils.findField(TestClass.class, "publicField");
        assertNotNull(publicField);
        assertEquals("publicField", publicField.getName());
        
        // 测试查找不存在的字段
        Field nonExistentField = ReflectionUtils.findField(TestClass.class, "nonExistentField");
        assertNull(nonExistentField);
        
        // 测试按类型查找字段
        Field fieldByType = ReflectionUtils.findField(TestClass.class, null, String.class);
        assertNotNull(fieldByType);
        assertEquals(String.class, fieldByType.getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindFieldWithNullClass() {
        ReflectionUtils.findField(null, "field");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindFieldWithNullNameAndType() {
        ReflectionUtils.findField(TestClass.class, null, null);
    }

    @Test
    public void testFindMethod() {
        // 测试查找无参方法
        Method publicMethod = ReflectionUtils.findMethod(TestClass.class, "publicMethod");
        assertNotNull(publicMethod);
        assertEquals("publicMethod", publicMethod.getName());
        assertEquals(0, publicMethod.getParameterTypes().length);
        
        // 测试查找有参方法
        Method methodWithParams = ReflectionUtils.findMethod(TestClass.class, "methodWithParams", String.class, int.class);
        assertNotNull(methodWithParams);
        assertEquals("methodWithParams", methodWithParams.getName());
        assertEquals(2, methodWithParams.getParameterTypes().length);
        
        // 测试查找私有方法
        Method privateMethod = ReflectionUtils.findMethod(TestClass.class, "privateMethod");
        assertNotNull(privateMethod);
        assertEquals("privateMethod", privateMethod.getName());
        
        // 测试查找不存在的方法
        Method nonExistentMethod = ReflectionUtils.findMethod(TestClass.class, "nonExistentMethod");
        assertNull(nonExistentMethod);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindMethodWithNullClass() {
        ReflectionUtils.findMethod(null, "method");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindMethodWithNullName() {
        ReflectionUtils.findMethod(TestClass.class, null);
    }

    @Test
    public void testMakeAccessible() {
        // 测试使字段可访问
        Field privateField = ReflectionUtils.findField(TestClass.class, "privateField");
        assertNotNull(privateField);
        assertFalse(privateField.isAccessible());
        
        ReflectionUtils.makeAccessible(privateField);
        assertTrue(privateField.isAccessible());
        
        // 测试使方法可访问
        Method privateMethod = ReflectionUtils.findMethod(TestClass.class, "privateMethod");
        assertNotNull(privateMethod);
        assertFalse(privateMethod.isAccessible());
        
        ReflectionUtils.makeAccessible(privateMethod);
        assertTrue(privateMethod.isAccessible());
        
        // 测试 null 情况
        ReflectionUtils.makeAccessible((Field) null);
        ReflectionUtils.makeAccessible((Method) null);
    }

    @Test
    public void testInvokeMethod() {
        TestClass testInstance = new TestClass();
        
        // 测试调用公共方法
        Method publicMethod = ReflectionUtils.findMethod(TestClass.class, "publicMethod");
        assertNotNull(publicMethod);
        
        Object result = ReflectionUtils.invokeMethod(publicMethod, testInstance);
        assertEquals("public", result);
        
        // 测试调用带参数的方法
        Method methodWithParams = ReflectionUtils.findMethod(TestClass.class, "methodWithParams", String.class, int.class);
        assertNotNull(methodWithParams);
        
        result = ReflectionUtils.invokeMethod(methodWithParams, testInstance, "test", 123);
        assertEquals("test123", result);
    }

    @Test
    public void testGetAndSetField() {
        TestClass testInstance = new TestClass();
        
        // 测试获取和设置私有字段
        Field privateField = ReflectionUtils.findField(TestClass.class, "privateField");
        assertNotNull(privateField);
        ReflectionUtils.makeAccessible(privateField);
        
        // 设置字段值
        ReflectionUtils.setField(privateField, testInstance, "test value");
        
        // 获取字段值
        Object value = ReflectionUtils.getField(privateField, testInstance);
        assertEquals("test value", value);
        
        // 验证通过 getter 方法也能获取到相同的值
        assertEquals("test value", testInstance.getPrivateField());
    }

    @Test
    public void testGetAllFields() {
        List<Field> fields = ReflectionUtils.getAllFields(TestClass.class);
        
        // TestClass 有 3 个字段
        assertTrue(fields.size() >= 3);
        
        // 验证包含预期的字段
        boolean hasPrivateField = false;
        boolean hasPublicField = false;
        boolean hasProtectedField = false;
        
        for (Field field : fields) {
            if ("privateField".equals(field.getName())) {
                hasPrivateField = true;
            } else if ("publicField".equals(field.getName())) {
                hasPublicField = true;
            } else if ("protectedField".equals(field.getName())) {
                hasProtectedField = true;
            }
        }
        
        assertTrue(hasPrivateField);
        assertTrue(hasPublicField);
        assertTrue(hasProtectedField);
    }

    @Test
    public void testGetAllMethods() {
        List<Method> methods = ReflectionUtils.getAllMethods(TestClass.class);
        
        // 应该包含 TestClass 的方法以及从 Object 继承的方法
        assertTrue(methods.size() > 4);
        
        // 验证包含预期的方法
        boolean hasPrivateMethod = false;
        boolean hasPublicMethod = false;
        boolean hasMethodWithParams = false;
        
        for (Method method : methods) {
            if ("privateMethod".equals(method.getName())) {
                hasPrivateMethod = true;
            } else if ("publicMethod".equals(method.getName())) {
                hasPublicMethod = true;
            } else if ("methodWithParams".equals(method.getName())) {
                hasMethodWithParams = true;
            }
        }
        
        assertTrue(hasPrivateMethod);
        assertTrue(hasPublicMethod);
        assertTrue(hasMethodWithParams);
    }

    @Test
    public void testHandleReflectionException() {
        // 测试处理 RuntimeException
        try {
            ReflectionUtils.handleReflectionException(new RuntimeException("test"));
            fail("Should have thrown RuntimeException");
        } catch (RuntimeException ex) {
            assertEquals("test", ex.getMessage());
        }
        
        // 测试处理 IllegalAccessException
        try {
            ReflectionUtils.handleReflectionException(new IllegalAccessException("access denied"));
            fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            assertTrue(ex.getMessage().contains("Could not access method"));
        }
    }
}
