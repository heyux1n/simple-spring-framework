package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.core.annotation.Autowired;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;

/**
 * ConstructorInjector 测试类
 * 
 * @author SimpleSpring Framework
 */
public class ConstructorInjectorTest {
    
    private BeanRegistry beanRegistry;
    private ConstructorInjector constructorInjector;
    
    @Before
    public void setUp() {
        beanRegistry = new BeanRegistry();
        constructorInjector = new ConstructorInjector(beanRegistry);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullRegistry() {
        // 测试构造函数参数为 null
        new ConstructorInjector(null);
    }
    
    @Test
    public void testCreateBeanInstanceWithDefaultConstructor() {
        // 测试使用默认构造函数创建实例
        Object instance = constructorInjector.createBeanInstance(TestBeanWithDefaultConstructor.class);
        
        assertNotNull(instance);
        assertTrue(instance instanceof TestBeanWithDefaultConstructor);
    }
    
    @Test
    public void testCreateBeanInstanceWithAutowiredConstructor() {
        // 测试使用带有 @Autowired 注解的构造函数创建实例
        
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
        
        // 创建实例
        Object instance = constructorInjector.createBeanInstance(TestBeanWithAutowiredConstructor.class);
        
        assertNotNull(instance);
        assertTrue(instance instanceof TestBeanWithAutowiredConstructor);
        
        TestBeanWithAutowiredConstructor testBean = (TestBeanWithAutowiredConstructor) instance;
        assertSame(dep1, testBean.dependency1);
        assertSame(dep2, testBean.dependency2);
    }
    
    @Test(expected = DependencyInjectionException.class)
    public void testCreateBeanInstanceWithMissingDependency() {
        // 测试缺少依赖的情况
        constructorInjector.createBeanInstance(TestBeanWithAutowiredConstructor.class);
    }
    
    @Test
    public void testCreateBeanInstanceWithOptionalDependency() {
        // 测试可选依赖的情况
        Object instance = constructorInjector.createBeanInstance(TestBeanWithOptionalConstructor.class);
        
        assertNotNull(instance);
        assertTrue(instance instanceof TestBeanWithOptionalConstructor);
        
        TestBeanWithOptionalConstructor testBean = (TestBeanWithOptionalConstructor) instance;
        assertNull(testBean.optionalDependency);
    }
    
    @Test(expected = DependencyInjectionException.class)
    public void testCreateBeanInstanceWithMultipleAutowiredConstructors() {
        // 测试多个 @Autowired 构造函数的情况
        constructorInjector.createBeanInstance(TestBeanWithMultipleAutowiredConstructors.class);
    }
    
    @Test(expected = DependencyInjectionException.class)
    public void testCreateBeanInstanceWithMultipleCandidates() {
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
        
        // 创建实例（应该抛出异常，因为有多个候选 Bean）
        constructorInjector.createBeanInstance(TestBeanWithSingleDependencyConstructor.class);
    }
    
    @Test
    public void testCreateBeanInstanceWithMinimalParameterConstructor() {
        // 测试选择参数最少的构造函数
        Object instance = constructorInjector.createBeanInstance(TestBeanWithMultipleConstructors.class);
        
        assertNotNull(instance);
        assertTrue(instance instanceof TestBeanWithMultipleConstructors);
        
        TestBeanWithMultipleConstructors testBean = (TestBeanWithMultipleConstructors) instance;
        assertTrue(testBean.usedDefaultConstructor);
    }
    
    @Test
    public void testGetPreferredConstructor() throws Exception {
        // 测试获取首选构造函数
        
        // 带有 @Autowired 注解的构造函数
        Constructor<?> autowiredConstructor = ConstructorInjector.getPreferredConstructor(TestBeanWithAutowiredConstructor.class);
        assertNotNull(autowiredConstructor);
        assertTrue(autowiredConstructor.isAnnotationPresent(Autowired.class));
        
        // 默认构造函数
        Constructor<?> defaultConstructor = ConstructorInjector.getPreferredConstructor(TestBeanWithDefaultConstructor.class);
        assertNotNull(defaultConstructor);
        assertEquals(0, defaultConstructor.getParameterTypes().length);
        
        // 参数最少的构造函数
        Constructor<?> minParamConstructor = ConstructorInjector.getPreferredConstructor(TestBeanWithMultipleConstructors.class);
        assertNotNull(minParamConstructor);
        assertEquals(0, minParamConstructor.getParameterTypes().length);
        
        // null 类
        assertNull(ConstructorInjector.getPreferredConstructor(null));
    }
    
    @Test
    public void testRequiresDependencyInjection() throws Exception {
        // 测试是否需要依赖注入
        
        Constructor<?> autowiredConstructor = TestBeanWithAutowiredConstructor.class.getDeclaredConstructor(TestDependency1.class, TestDependency2.class);
        Constructor<?> defaultConstructor = TestBeanWithDefaultConstructor.class.getDeclaredConstructor();
        Constructor<?> parameterConstructor = TestBeanWithMultipleConstructors.class.getDeclaredConstructor(String.class);
        
        assertTrue(ConstructorInjector.requiresDependencyInjection(autowiredConstructor));
        assertFalse(ConstructorInjector.requiresDependencyInjection(defaultConstructor));
        assertTrue(ConstructorInjector.requiresDependencyInjection(parameterConstructor));
        assertFalse(ConstructorInjector.requiresDependencyInjection(null));
    }
    
    @Test
    public void testHasAutowiredConstructor() {
        // 测试是否有 @Autowired 构造函数
        
        assertTrue(ConstructorInjector.hasAutowiredConstructor(TestBeanWithAutowiredConstructor.class));
        assertFalse(ConstructorInjector.hasAutowiredConstructor(TestBeanWithDefaultConstructor.class));
        assertFalse(ConstructorInjector.hasAutowiredConstructor(null));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateBeanInstanceWithNullClass() {
        // 测试空类参数
        constructorInjector.createBeanInstance(null);
    }
    
    // 测试用的类
    
    public static class TestBeanWithDefaultConstructor {
        public TestBeanWithDefaultConstructor() {
        }
    }
    
    public static class TestBeanWithAutowiredConstructor {
        private final TestDependency1 dependency1;
        private final TestDependency2 dependency2;
        
        @Autowired
        public TestBeanWithAutowiredConstructor(TestDependency1 dependency1, TestDependency2 dependency2) {
            this.dependency1 = dependency1;
            this.dependency2 = dependency2;
        }
    }
    
    public static class TestBeanWithOptionalConstructor {
        private final TestDependency1 optionalDependency;
        
        @Autowired(required = false)
        public TestBeanWithOptionalConstructor(TestDependency1 optionalDependency) {
            this.optionalDependency = optionalDependency;
        }
    }
    
    public static class TestBeanWithMultipleAutowiredConstructors {
        @Autowired
        public TestBeanWithMultipleAutowiredConstructors() {
        }
        
        @Autowired
        public TestBeanWithMultipleAutowiredConstructors(TestDependency1 dependency) {
        }
    }
    
    public static class TestBeanWithSingleDependencyConstructor {
        private final TestDependency1 dependency;
        
        @Autowired
        public TestBeanWithSingleDependencyConstructor(TestDependency1 dependency) {
            this.dependency = dependency;
        }
    }
    
    public static class TestBeanWithMultipleConstructors {
        private boolean usedDefaultConstructor = false;
        
        public TestBeanWithMultipleConstructors() {
            this.usedDefaultConstructor = true;
        }
        
        public TestBeanWithMultipleConstructors(String param) {
            this.usedDefaultConstructor = false;
        }
        
        public TestBeanWithMultipleConstructors(String param1, String param2) {
            this.usedDefaultConstructor = false;
        }
    }
    
    public static class TestDependency1 {
    }
    
    public static class TestDependency2 {
    }
}
