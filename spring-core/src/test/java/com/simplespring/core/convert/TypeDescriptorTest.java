package com.simplespring.core.convert;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * TypeDescriptor 的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class TypeDescriptorTest {

    // 测试用的字段
    private String stringField;
    private int intField;
    private List<String> listField;
    private Map<String, Integer> mapField;
    private String[] arrayField;

    @Test
    public void testBasicTypeDescriptor() {
        TypeDescriptor descriptor = new TypeDescriptor(String.class);
        
        assertEquals(String.class, descriptor.getType());
        assertEquals(String.class, descriptor.getGenericType());
        assertNull(descriptor.getSource());
        assertFalse(descriptor.isPrimitive());
        assertFalse(descriptor.isArray());
        assertFalse(descriptor.isCollection());
        assertFalse(descriptor.isMap());
    }

    @Test
    public void testPrimitiveTypeDescriptor() {
        TypeDescriptor descriptor = new TypeDescriptor(int.class);
        
        assertEquals(int.class, descriptor.getType());
        assertTrue(descriptor.isPrimitive());
        assertFalse(descriptor.isArray());
        assertFalse(descriptor.isCollection());
        assertFalse(descriptor.isMap());
    }

    @Test
    public void testArrayTypeDescriptor() {
        TypeDescriptor descriptor = new TypeDescriptor(String[].class);
        
        assertEquals(String[].class, descriptor.getType());
        assertFalse(descriptor.isPrimitive());
        assertTrue(descriptor.isArray());
        assertFalse(descriptor.isCollection());
        assertFalse(descriptor.isMap());
        
        TypeDescriptor elementDescriptor = descriptor.getElementTypeDescriptor();
        assertNotNull(elementDescriptor);
        assertEquals(String.class, elementDescriptor.getType());
    }

    @Test
    public void testCollectionTypeDescriptor() {
        TypeDescriptor descriptor = new TypeDescriptor(List.class);
        
        assertEquals(List.class, descriptor.getType());
        assertFalse(descriptor.isPrimitive());
        assertFalse(descriptor.isArray());
        assertTrue(descriptor.isCollection());
        assertFalse(descriptor.isMap());
    }

    @Test
    public void testMapTypeDescriptor() {
        TypeDescriptor descriptor = new TypeDescriptor(Map.class);
        
        assertEquals(Map.class, descriptor.getType());
        assertFalse(descriptor.isPrimitive());
        assertFalse(descriptor.isArray());
        assertFalse(descriptor.isCollection());
        assertTrue(descriptor.isMap());
    }

    @Test
    public void testForField() throws NoSuchFieldException {
        // 测试字符串字段
        Field stringField = TypeDescriptorTest.class.getDeclaredField("stringField");
        TypeDescriptor descriptor = TypeDescriptor.forField(stringField);
        
        assertNotNull(descriptor);
        assertEquals(String.class, descriptor.getType());
        assertEquals(stringField, descriptor.getSource());
        
        // 测试基本类型字段
        Field intField = TypeDescriptorTest.class.getDeclaredField("intField");
        descriptor = TypeDescriptor.forField(intField);
        
        assertNotNull(descriptor);
        assertEquals(int.class, descriptor.getType());
        assertTrue(descriptor.isPrimitive());
        
        // 测试数组字段
        Field arrayField = TypeDescriptorTest.class.getDeclaredField("arrayField");
        descriptor = TypeDescriptor.forField(arrayField);
        
        assertNotNull(descriptor);
        assertEquals(String[].class, descriptor.getType());
        assertTrue(descriptor.isArray());
        
        TypeDescriptor elementDescriptor = descriptor.getElementTypeDescriptor();
        assertNotNull(elementDescriptor);
        assertEquals(String.class, elementDescriptor.getType());
    }

    @Test
    public void testForFieldWithNull() {
        TypeDescriptor descriptor = TypeDescriptor.forField(null);
        assertNull(descriptor);
    }

    @Test
    public void testForMethodParameter() throws NoSuchMethodException {
        Method method = TypeDescriptorTest.class.getDeclaredMethod("testMethod", String.class, int.class);
        
        // 测试第一个参数
        TypeDescriptor descriptor = TypeDescriptor.forMethodParameter(method, 0);
        assertNotNull(descriptor);
        assertEquals(String.class, descriptor.getType());
        assertEquals(method, descriptor.getSource());
        
        // 测试第二个参数
        descriptor = TypeDescriptor.forMethodParameter(method, 1);
        assertNotNull(descriptor);
        assertEquals(int.class, descriptor.getType());
        assertTrue(descriptor.isPrimitive());
    }

    @Test
    public void testForMethodParameterWithInvalidIndex() throws NoSuchMethodException {
        Method method = TypeDescriptorTest.class.getDeclaredMethod("testMethod", String.class, int.class);
        
        // 测试无效索引
        TypeDescriptor descriptor = TypeDescriptor.forMethodParameter(method, -1);
        assertNull(descriptor);
        
        descriptor = TypeDescriptor.forMethodParameter(method, 2);
        assertNull(descriptor);
    }

    @Test
    public void testForMethodParameterWithNull() {
        TypeDescriptor descriptor = TypeDescriptor.forMethodParameter(null, 0);
        assertNull(descriptor);
    }

    @Test
    public void testForMethodReturnType() throws NoSuchMethodException {
        Method method = TypeDescriptorTest.class.getDeclaredMethod("testMethod", String.class, int.class);
        
        TypeDescriptor descriptor = TypeDescriptor.forMethodReturnType(method);
        assertNotNull(descriptor);
        assertEquals(String.class, descriptor.getType());
        assertEquals(method, descriptor.getSource());
    }

    @Test
    public void testForMethodReturnTypeWithNull() {
        TypeDescriptor descriptor = TypeDescriptor.forMethodReturnType(null);
        assertNull(descriptor);
    }

    @Test
    public void testForObject() {
        // 测试字符串对象
        TypeDescriptor descriptor = TypeDescriptor.forObject("test");
        assertNotNull(descriptor);
        assertEquals(String.class, descriptor.getType());
        
        // 测试整数对象
        descriptor = TypeDescriptor.forObject(123);
        assertNotNull(descriptor);
        assertEquals(Integer.class, descriptor.getType());
        
        // 测试 null 对象
        descriptor = TypeDescriptor.forObject(null);
        assertNull(descriptor);
    }

    @Test
    public void testEquals() {
        TypeDescriptor descriptor1 = new TypeDescriptor(String.class);
        TypeDescriptor descriptor2 = new TypeDescriptor(String.class);
        TypeDescriptor descriptor3 = new TypeDescriptor(Integer.class);
        
        assertEquals(descriptor1, descriptor2);
        assertNotEquals(descriptor1, descriptor3);
        assertNotEquals(descriptor1, null);
        assertNotEquals(descriptor1, "not a descriptor");
    }

    @Test
    public void testHashCode() {
        TypeDescriptor descriptor1 = new TypeDescriptor(String.class);
        TypeDescriptor descriptor2 = new TypeDescriptor(String.class);
        
        assertEquals(descriptor1.hashCode(), descriptor2.hashCode());
    }

    @Test
    public void testToString() {
        TypeDescriptor descriptor = new TypeDescriptor(String.class);
        String toString = descriptor.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("String"));
        assertTrue(toString.contains("TypeDescriptor"));
    }

    // 测试用的方法
    @SuppressWarnings("unused")
    private String testMethod(String param1, int param2) {
        return param1 + param2;
    }
}
