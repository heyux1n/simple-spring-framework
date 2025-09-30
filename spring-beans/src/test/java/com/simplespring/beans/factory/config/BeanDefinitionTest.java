package com.simplespring.beans.factory.config;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * BeanDefinition 测试类
 * 
 * @author SimpleSpring Framework
 */
public class BeanDefinitionTest {
    
    private BeanDefinition beanDefinition;
    
    @Before
    public void setUp() {
        beanDefinition = new BeanDefinition();
    }
    
    @Test
    public void testDefaultConstructor() {
        // 测试默认构造函数
        assertNull(beanDefinition.getBeanClass());
        assertNull(beanDefinition.getBeanName());
        assertEquals(Scope.SINGLETON, beanDefinition.getScope());
        assertTrue(beanDefinition.isSingleton());
        assertFalse(beanDefinition.isLazyInit());
    }
    
    @Test
    public void testConstructorWithClass() {
        // 测试带 Class 参数的构造函数
        BeanDefinition bd = new BeanDefinition(String.class);
        assertEquals(String.class, bd.getBeanClass());
        assertNull(bd.getBeanName());
    }
    
    @Test
    public void testConstructorWithClassAndName() {
        // 测试带 Class 和名称参数的构造函数
        BeanDefinition bd = new BeanDefinition(String.class, "testBean");
        assertEquals(String.class, bd.getBeanClass());
        assertEquals("testBean", bd.getBeanName());
    }
    
    @Test
    public void testSetScope() {
        // 测试设置作用域
        beanDefinition.setScope(Scope.PROTOTYPE);
        assertEquals(Scope.PROTOTYPE, beanDefinition.getScope());
        assertFalse(beanDefinition.isSingleton());
        
        beanDefinition.setScope(Scope.SINGLETON);
        assertEquals(Scope.SINGLETON, beanDefinition.getScope());
        assertTrue(beanDefinition.isSingleton());
    }
    
    @Test
    public void testSetSingleton() {
        // 测试设置单例标志
        beanDefinition.setSingleton(false);
        assertFalse(beanDefinition.isSingleton());
        assertEquals(Scope.PROTOTYPE, beanDefinition.getScope());
        
        beanDefinition.setSingleton(true);
        assertTrue(beanDefinition.isSingleton());
        assertEquals(Scope.SINGLETON, beanDefinition.getScope());
    }
    
    @Test
    public void testConstructorHandling() throws Exception {
        // 测试构造函数处理
        Constructor<String> constructor = String.class.getConstructor(String.class);
        beanDefinition.setConstructor(constructor);
        
        assertEquals(constructor, beanDefinition.getConstructor());
        assertArrayEquals(new Class<?>[]{String.class}, beanDefinition.getConstructorParameterTypes());
        assertTrue(beanDefinition.hasConstructorParameters());
        assertEquals(1, beanDefinition.getConstructorParameterCount());
    }
    
    @Test
    public void testAutowiredFields() throws Exception {
        // 测试自动装配字段
        Field field = TestClass.class.getDeclaredField("testField");
        
        assertFalse(beanDefinition.hasAutowiredFields());
        
        beanDefinition.addAutowiredField(field);
        assertTrue(beanDefinition.hasAutowiredFields());
        assertEquals(1, beanDefinition.getAutowiredFields().size());
        assertTrue(beanDefinition.getAutowiredFields().contains(field));
    }
    
    @Test
    public void testAutowiredMethods() throws Exception {
        // 测试自动装配方法
        Method method = TestClass.class.getDeclaredMethod("testMethod");
        
        assertFalse(beanDefinition.hasAutowiredMethods());
        
        beanDefinition.addAutowiredMethod(method);
        assertTrue(beanDefinition.hasAutowiredMethods());
        assertEquals(1, beanDefinition.getAutowiredMethods().size());
        assertTrue(beanDefinition.getAutowiredMethods().contains(method));
    }
    
    @Test
    public void testAddNullFieldAndMethod() {
        // 测试添加 null 字段和方法
        beanDefinition.addAutowiredField(null);
        beanDefinition.addAutowiredMethod(null);
        
        assertFalse(beanDefinition.hasAutowiredFields());
        assertFalse(beanDefinition.hasAutowiredMethods());
    }
    
    @Test
    public void testToString() {
        // 测试 toString 方法
        beanDefinition.setBeanClass(String.class);
        beanDefinition.setBeanName("testBean");
        
        String result = beanDefinition.toString();
        assertTrue(result.contains("String"));
        assertTrue(result.contains("testBean"));
        assertTrue(result.contains("SINGLETON"));
    }
    
    // 测试用的内部类
    private static class TestClass {
        private String testField;
        
        public void testMethod() {
        }
    }
}
