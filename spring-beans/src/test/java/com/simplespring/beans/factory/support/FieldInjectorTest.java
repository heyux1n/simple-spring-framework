package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.core.annotation.Autowired;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

/**
 * FieldInjector 测试类
 * 
 * @author SimpleSpring Framework
 */
public class FieldInjectorTest {
    
    private BeanRegistry beanRegistry;
    private FieldInjector fieldInjector;
    
    @Before
    public void setUp() {
        beanRegistry = new BeanRegistry();
        fieldInjector = new FieldInjector(beanRegistry);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullRegistry() {
        // 测试构造函数参数为 null
        new FieldInjector(null);
    }
    
    @Test
    public void testScanAutowiredFields() {
        // 测试扫描带有 @Autowired 注解的字段
        List<Field> fields = FieldInjector.scanAutowiredFields(TestBean.class);
        
        assertEquals(2, fields.size());
        
        // 验证字段名称
        boolean foundDependency1 = false;
        boolean foundDependency2 = false;
        
        for (Field field : fields) {
            if ("dependency1".equals(field.getName())) {
                foundDependency1 = true;
            } else if ("dependency2".equals(field.getName())) {
                foundDependency2 = true;
            }
        }
        
        assertTrue("应该找到 dependency1 字段", foundDependency1);
        assertTrue("应该找到 dependency2 字段", foundDependency2);
    }
    
    @Test
    public void testScanAutowiredFieldsWithInheritance() {
        // 测试继承关系中的字段扫描
        List<Field> fields = FieldInjector.scanAutowiredFields(TestSubBean.class);
        
        assertEquals(3, fields.size()); // 父类2个 + 子类1个
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testScanAutowiredFieldsWithNullClass() {
        // 测试空类参数
        FieldInjector.scanAutowiredFields(null);
    }
    
    @Test
    public void testIsAutowiredField() throws Exception {
        // 测试字段是否带有 @Autowired 注解
        Field autowiredField = TestBean.class.getDeclaredField("dependency1");
        Field normalField = TestBean.class.getDeclaredField("normalField");
        
        assertTrue(FieldInjector.isAutowiredField(autowiredField));
        assertFalse(FieldInjector.isAutowiredField(normalField));
        assertFalse(FieldInjector.isAutowiredField(null));
    }
    
    @Test
    public void testInjectFieldsSuccess() {
        // 测试成功的字段注入
        
        // 准备依赖 Bean
        TestDependency1 dep1 = new TestDependency1();
        TestDependency2 dep2 = new TestDependency2();
        
        // 注册依赖 Bean
        beanRegistry.registerSingleton("dep1", dep1);
        beanRegistry.registerSingleton("dep2", dep2);
        
        // 注册依赖 Bean 的定义
        BeanDefinition dep1Definition = new BeanDefinition(TestDependency1.class, "dep1");
        BeanDefinition dep2Definition = new BeanDefinition(TestDependency2.class, "dep2");
        beanRegistry.registerBeanDefinition("dep1", dep1Definition);
        beanRegistry.registerBeanDefinition("dep2", dep2Definition);
        
        // 创建目标 Bean 和定义
        TestBean testBean = new TestBean();
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class, "testBean");
        
        // 扫描并设置自动装配字段
        List<Field> autowiredFields = FieldInjector.scanAutowiredFields(TestBean.class);
        beanDefinition.setAutowiredFields(autowiredFields);
        
        // 执行字段注入
        fieldInjector.injectFields(testBean, beanDefinition);
        
        // 验证注入结果
        assertNotNull(testBean.dependency1);
        assertNotNull(testBean.dependency2);
        assertSame(dep1, testBean.dependency1);
        assertSame(dep2, testBean.dependency2);
        assertNull(testBean.normalField); // 普通字段不应该被注入
    }
    
    @Test(expected = DependencyInjectionException.class)
    public void testInjectFieldsWithMissingRequiredDependency() {
        // 测试缺少必需依赖的情况
        
        TestBean testBean = new TestBean();
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class, "testBean");
        
        // 扫描并设置自动装配字段
        List<Field> autowiredFields = FieldInjector.scanAutowiredFields(TestBean.class);
        beanDefinition.setAutowiredFields(autowiredFields);
        
        // 执行字段注入（没有注册依赖 Bean，应该抛出异常）
        fieldInjector.injectFields(testBean, beanDefinition);
    }
    
    @Test
    public void testInjectFieldsWithOptionalDependency() {
        // 测试可选依赖的情况
        
        TestBeanWithOptionalDependency testBean = new TestBeanWithOptionalDependency();
        BeanDefinition beanDefinition = new BeanDefinition(TestBeanWithOptionalDependency.class, "testBean");
        
        // 扫描并设置自动装配字段
        List<Field> autowiredFields = FieldInjector.scanAutowiredFields(TestBeanWithOptionalDependency.class);
        beanDefinition.setAutowiredFields(autowiredFields);
        
        // 执行字段注入（没有注册依赖 Bean，但是可选的，不应该抛出异常）
        fieldInjector.injectFields(testBean, beanDefinition);
        
        // 验证可选依赖没有被注入
        assertNull(testBean.optionalDependency);
    }
    
    @Test(expected = DependencyInjectionException.class)
    public void testInjectFieldsWithMultipleCandidates() {
        // 测试多个候选 Bean 的情况
        
        // 注册多个相同类型的 Bean
        TestDependency1 dep1a = new TestDependency1();
        TestDependency1 dep1b = new TestDependency1();
        
        beanRegistry.registerSingleton("dep1a", dep1a);
        beanRegistry.registerSingleton("dep1b", dep1b);
        
        BeanDefinition dep1aDefinition = new BeanDefinition(TestDependency1.class, "dep1a");
        BeanDefinition dep1bDefinition = new BeanDefinition(TestDependency1.class, "dep1b");
        beanRegistry.registerBeanDefinition("dep1a", dep1aDefinition);
        beanRegistry.registerBeanDefinition("dep1b", dep1bDefinition);
        
        // 创建目标 Bean
        TestBeanWithSingleDependency testBean = new TestBeanWithSingleDependency();
        BeanDefinition beanDefinition = new BeanDefinition(TestBeanWithSingleDependency.class, "testBean");
        
        List<Field> autowiredFields = FieldInjector.scanAutowiredFields(TestBeanWithSingleDependency.class);
        beanDefinition.setAutowiredFields(autowiredFields);
        
        // 执行字段注入（应该抛出异常，因为有多个候选 Bean）
        fieldInjector.injectFields(testBean, beanDefinition);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInjectFieldsWithNullBeanInstance() {
        // 测试 Bean 实例为 null
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class, "testBean");
        fieldInjector.injectFields(null, beanDefinition);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInjectFieldsWithNullBeanDefinition() {
        // 测试 Bean 定义为 null
        TestBean testBean = new TestBean();
        fieldInjector.injectFields(testBean, null);
    }
    
    @Test
    public void testInjectFieldsWithNoAutowiredFields() {
        // 测试没有自动装配字段的情况
        TestBeanWithoutAutowired testBean = new TestBeanWithoutAutowired();
        BeanDefinition beanDefinition = new BeanDefinition(TestBeanWithoutAutowired.class, "testBean");
        
        // 不应该抛出异常
        fieldInjector.injectFields(testBean, beanDefinition);
    }
    
    // 测试用的类
    
    public static class TestBean {
        @Autowired
        private TestDependency1 dependency1;
        
        @Autowired
        private TestDependency2 dependency2;
        
        private String normalField;
    }
    
    public static class TestSubBean extends TestBean {
        @Autowired
        private TestDependency1 subDependency;
    }
    
    public static class TestBeanWithOptionalDependency {
        @Autowired(required = false)
        private TestDependency1 optionalDependency;
    }
    
    public static class TestBeanWithSingleDependency {
        @Autowired
        private TestDependency1 dependency;
    }
    
    public static class TestBeanWithoutAutowired {
        private String normalField;
    }
    
    public static class TestDependency1 {
    }
    
    public static class TestDependency2 {
    }
}
