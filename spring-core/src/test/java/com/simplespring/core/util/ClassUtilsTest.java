package com.simplespring.core.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ClassUtils 工具类的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class ClassUtilsTest {

    @Test
    public void testForName() throws ClassNotFoundException {
        // 测试正常情况
        Class<?> stringClass = ClassUtils.forName("java.lang.String", null);
        assertEquals(String.class, stringClass);
        
        // 测试使用类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> integerClass = ClassUtils.forName("java.lang.Integer", classLoader);
        assertEquals(Integer.class, integerClass);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testForNameWithNonExistentClass() throws ClassNotFoundException {
        ClassUtils.forName("com.nonexistent.Class", null);
    }

    @Test
    public void testIsAssignable() {
        // 测试相同类型
        assertTrue(ClassUtils.isAssignable(String.class, String.class));
        
        // 测试继承关系
        assertTrue(ClassUtils.isAssignable(String.class, Object.class));
        assertFalse(ClassUtils.isAssignable(Object.class, String.class));
        
        // 测试基本类型和包装类型
        assertTrue(ClassUtils.isAssignable(int.class, Integer.class));
        assertTrue(ClassUtils.isAssignable(Integer.class, int.class));
        assertTrue(ClassUtils.isAssignable(boolean.class, Boolean.class));
        assertTrue(ClassUtils.isAssignable(Boolean.class, boolean.class));
        
        // 测试不兼容的类型
        assertFalse(ClassUtils.isAssignable(String.class, Integer.class));
        assertFalse(ClassUtils.isAssignable(int.class, String.class));
        
        // 测试 null 情况
        assertFalse(ClassUtils.isAssignable(null, String.class));
        assertFalse(ClassUtils.isAssignable(String.class, null));
        assertFalse(ClassUtils.isAssignable(null, null));
    }

    @Test
    public void testGetShortName() {
        // 测试正常情况
        assertEquals("String", ClassUtils.getShortName(String.class));
        assertEquals("Integer", ClassUtils.getShortName(Integer.class));
        
        // 测试内部类
        assertEquals("Map$Entry", ClassUtils.getShortName("java.util.Map$Entry"));
        
        // 测试无包名的类
        assertEquals("TestClass", ClassUtils.getShortName("TestClass"));
        
        // 测试边界情况
        assertEquals("", ClassUtils.getShortName((Class<?>) null));
        assertEquals("", ClassUtils.getShortName(""));
        assertEquals("", ClassUtils.getShortName((String) null));
    }

    @Test
    public void testGetDefaultClassLoader() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        assertNotNull(classLoader);
    }

    @Test
    public void testIsPrimitiveType() {
        // 测试基本类型
        assertTrue(ClassUtils.isPrimitiveType(int.class));
        assertTrue(ClassUtils.isPrimitiveType(boolean.class));
        assertTrue(ClassUtils.isPrimitiveType(char.class));
        assertTrue(ClassUtils.isPrimitiveType(byte.class));
        assertTrue(ClassUtils.isPrimitiveType(short.class));
        assertTrue(ClassUtils.isPrimitiveType(long.class));
        assertTrue(ClassUtils.isPrimitiveType(float.class));
        assertTrue(ClassUtils.isPrimitiveType(double.class));
        
        // 测试非基本类型
        assertFalse(ClassUtils.isPrimitiveType(Integer.class));
        assertFalse(ClassUtils.isPrimitiveType(String.class));
        assertFalse(ClassUtils.isPrimitiveType(Object.class));
        assertFalse(ClassUtils.isPrimitiveType(null));
    }

    @Test
    public void testIsPrimitiveWrapper() {
        // 测试包装类型
        assertTrue(ClassUtils.isPrimitiveWrapper(Integer.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Boolean.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Character.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Byte.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Short.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Long.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Float.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Double.class));
        
        // 测试非包装类型
        assertFalse(ClassUtils.isPrimitiveWrapper(int.class));
        assertFalse(ClassUtils.isPrimitiveWrapper(String.class));
        assertFalse(ClassUtils.isPrimitiveWrapper(Object.class));
        assertFalse(ClassUtils.isPrimitiveWrapper(null));
    }

    @Test
    public void testResolvePrimitiveWrapper() {
        // 测试基本类型转包装类型
        assertEquals(Integer.class, ClassUtils.resolvePrimitiveWrapper(int.class));
        assertEquals(Boolean.class, ClassUtils.resolvePrimitiveWrapper(boolean.class));
        assertEquals(Character.class, ClassUtils.resolvePrimitiveWrapper(char.class));
        assertEquals(Byte.class, ClassUtils.resolvePrimitiveWrapper(byte.class));
        assertEquals(Short.class, ClassUtils.resolvePrimitiveWrapper(short.class));
        assertEquals(Long.class, ClassUtils.resolvePrimitiveWrapper(long.class));
        assertEquals(Float.class, ClassUtils.resolvePrimitiveWrapper(float.class));
        assertEquals(Double.class, ClassUtils.resolvePrimitiveWrapper(double.class));
        
        // 测试非基本类型
        assertEquals(String.class, ClassUtils.resolvePrimitiveWrapper(String.class));
        assertEquals(Object.class, ClassUtils.resolvePrimitiveWrapper(Object.class));
    }
}
