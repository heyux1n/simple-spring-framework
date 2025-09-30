package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.beans.factory.config.Scope;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * BeanRegistry 测试类
 * 
 * @author SimpleSpring Framework
 */
public class BeanRegistryTest {
    
    private BeanRegistry beanRegistry;
    
    @Before
    public void setUp() {
        beanRegistry = new BeanRegistry();
    }
    
    @Test
    public void testRegisterBeanDefinition() {
        // 测试注册 Bean 定义
        BeanDefinition beanDefinition = new BeanDefinition(String.class, "testBean");
        beanRegistry.registerBeanDefinition("testBean", beanDefinition);
        
        assertTrue(beanRegistry.containsBeanDefinition("testBean"));
        assertEquals(beanDefinition, beanRegistry.getBeanDefinition("testBean"));
        assertEquals(1, beanRegistry.getBeanDefinitionCount());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBeanDefinitionWithEmptyName() {
        // 测试注册空名称的 Bean 定义
        BeanDefinition beanDefinition = new BeanDefinition(String.class);
        beanRegistry.registerBeanDefinition("", beanDefinition);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBeanDefinitionWithNullDefinition() {
        // 测试注册 null Bean 定义
        beanRegistry.registerBeanDefinition("testBean", null);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRegisterDuplicateBeanDefinition() {
        // 测试注册重复的 Bean 定义
        BeanDefinition beanDefinition = new BeanDefinition(String.class, "testBean");
        beanRegistry.registerBeanDefinition("testBean", beanDefinition);
        beanRegistry.registerBeanDefinition("testBean", beanDefinition);
    }
    
    @Test
    public void testGetBeanDefinitionNames() {
        // 测试获取所有 Bean 定义名称
        BeanDefinition bd1 = new BeanDefinition(String.class, "bean1");
        BeanDefinition bd2 = new BeanDefinition(Integer.class, "bean2");
        
        beanRegistry.registerBeanDefinition("bean1", bd1);
        beanRegistry.registerBeanDefinition("bean2", bd2);
        
        String[] names = beanRegistry.getBeanDefinitionNames();
        assertEquals(2, names.length);
        assertTrue(contains(names, "bean1"));
        assertTrue(contains(names, "bean2"));
    }
    
    @Test
    public void testRegisterSingleton() {
        // 测试注册单例 Bean
        String singletonBean = "test singleton";
        beanRegistry.registerSingleton("testSingleton", singletonBean);
        
        assertTrue(beanRegistry.containsSingleton("testSingleton"));
        assertEquals(singletonBean, beanRegistry.getSingleton("testSingleton"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterSingletonWithEmptyName() {
        // 测试注册空名称的单例 Bean
        beanRegistry.registerSingleton("", "test");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterSingletonWithNullObject() {
        // 测试注册 null 单例对象
        beanRegistry.registerSingleton("testBean", null);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRegisterDuplicateSingleton() {
        // 测试注册重复的单例 Bean
        beanRegistry.registerSingleton("testBean", "test1");
        beanRegistry.registerSingleton("testBean", "test2");
    }
    
    @Test
    public void testGetBeanNamesForType() {
        // 测试根据类型获取 Bean 名称
        BeanDefinition stringBd = new BeanDefinition(String.class, "stringBean");
        BeanDefinition integerBd = new BeanDefinition(Integer.class, "integerBean");
        BeanDefinition anotherStringBd = new BeanDefinition(String.class, "anotherStringBean");
        
        beanRegistry.registerBeanDefinition("stringBean", stringBd);
        beanRegistry.registerBeanDefinition("integerBean", integerBd);
        beanRegistry.registerBeanDefinition("anotherStringBean", anotherStringBd);
        
        List<String> stringBeans = beanRegistry.getBeanNamesForType(String.class);
        assertEquals(2, stringBeans.size());
        assertTrue(stringBeans.contains("stringBean"));
        assertTrue(stringBeans.contains("anotherStringBean"));
        
        List<String> integerBeans = beanRegistry.getBeanNamesForType(Integer.class);
        assertEquals(1, integerBeans.size());
        assertTrue(integerBeans.contains("integerBean"));
        
        List<String> nonExistentBeans = beanRegistry.getBeanNamesForType(Double.class);
        assertEquals(0, nonExistentBeans.size());
    }
    
    @Test
    public void testGetBeanNamesForTypeWithInheritance() {
        // 测试继承关系的类型查找
        BeanDefinition bd = new BeanDefinition(TestSubClass.class, "testBean");
        beanRegistry.registerBeanDefinition("testBean", bd);
        
        // 应该能通过父类找到
        List<String> superClassBeans = beanRegistry.getBeanNamesForType(TestSuperClass.class);
        assertEquals(1, superClassBeans.size());
        assertTrue(superClassBeans.contains("testBean"));
        
        // 应该能通过接口找到
        List<String> interfaceBeans = beanRegistry.getBeanNamesForType(TestInterface.class);
        assertEquals(1, interfaceBeans.size());
        assertTrue(interfaceBeans.contains("testBean"));
        
        // 应该能通过具体类找到
        List<String> subClassBeans = beanRegistry.getBeanNamesForType(TestSubClass.class);
        assertEquals(1, subClassBeans.size());
        assertTrue(subClassBeans.contains("testBean"));
    }
    
    @Test
    public void testGetType() {
        // 测试获取 Bean 类型
        BeanDefinition beanDefinition = new BeanDefinition(String.class, "testBean");
        beanRegistry.registerBeanDefinition("testBean", beanDefinition);
        
        assertEquals(String.class, beanRegistry.getType("testBean"));
        assertNull(beanRegistry.getType("nonExistentBean"));
    }
    
    @Test
    public void testCircularDependencyDetection() {
        // 测试循环依赖检测
        assertFalse(beanRegistry.isCurrentlyInCreation("testBean"));
        
        beanRegistry.beforeSingletonCreation("testBean");
        assertTrue(beanRegistry.isCurrentlyInCreation("testBean"));
        
        beanRegistry.afterSingletonCreation("testBean");
        assertFalse(beanRegistry.isCurrentlyInCreation("testBean"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testCircularDependencyException() {
        // 测试循环依赖异常
        beanRegistry.beforeSingletonCreation("testBean");
        beanRegistry.beforeSingletonCreation("testBean"); // 应该抛出异常
    }
    
    @Test
    public void testRemoveBeanDefinition() {
        // 测试移除 Bean 定义
        BeanDefinition beanDefinition = new BeanDefinition(String.class, "testBean");
        beanRegistry.registerBeanDefinition("testBean", beanDefinition);
        
        assertTrue(beanRegistry.containsBeanDefinition("testBean"));
        
        beanRegistry.removeBeanDefinition("testBean");
        
        assertFalse(beanRegistry.containsBeanDefinition("testBean"));
        assertNull(beanRegistry.getBeanDefinition("testBean"));
        assertNull(beanRegistry.getType("testBean"));
        
        List<String> stringBeans = beanRegistry.getBeanNamesForType(String.class);
        assertEquals(0, stringBeans.size());
    }
    
    @Test
    public void testClear() {
        // 测试清空所有缓存
        BeanDefinition beanDefinition = new BeanDefinition(String.class, "testBean");
        beanRegistry.registerBeanDefinition("testBean", beanDefinition);
        beanRegistry.registerSingleton("singletonBean", "test");
        beanRegistry.beforeSingletonCreation("creatingBean");
        
        assertTrue(beanRegistry.containsBeanDefinition("testBean"));
        assertTrue(beanRegistry.containsSingleton("singletonBean"));
        assertTrue(beanRegistry.isCurrentlyInCreation("creatingBean"));
        
        beanRegistry.clear();
        
        assertFalse(beanRegistry.containsBeanDefinition("testBean"));
        assertFalse(beanRegistry.containsSingleton("singletonBean"));
        assertFalse(beanRegistry.isCurrentlyInCreation("creatingBean"));
        assertEquals(0, beanRegistry.getBeanDefinitionCount());
    }
    
    // 辅助方法
    private boolean contains(String[] array, String value) {
        for (String item : array) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
    
    // 测试用的类和接口
    private interface TestInterface {
    }
    
    private static class TestSuperClass {
    }
    
    private static class TestSubClass extends TestSuperClass implements TestInterface {
    }
}
