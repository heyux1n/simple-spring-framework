package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.core.annotation.Autowired;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * MethodInjector 测试类
 * 
 * @author SimpleSpring Framework
 */
public class MethodInjectorTest {
    
    private BeanRegistry beanRegistry;
    private MethodInjector methodInjector;
    
    @Before
    public void setUp() {
        beanRegistry = new BeanRegistry();
        methodInjector = new MethodInjector(beanRegistry);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullRegistry() {
        // 测试构造函数参数为 null
        new MethodInjector(null);
    }
    
    @Test
    public void testScanAutowiredMethods() {
        // 测试扫描带有 @Autowired 注解的方法
        List<Method> methods = MethodInjector.scanAutowiredMethods(TestBean.class);
        
        assertEquals(3, methods.size());
        
        // 验证方法名称
        boolean foundSetDependency1 = false;
        boolean foundSetDependency2 = false;
        boolean foundInitMethod = false;
        
        for (Method method : methods) {
            String methodName = method.getName();
            if ("setDependency1".equals(methodName)) {
                foundSetDependency1 = true;
            } else if ("setDependency2".equals(methodName)) {
                foundSetDependency2 = true;
            } else if ("initMethod".equals(methodName)) {
                foundInitMethod = true;
            }
        }
        
        assertTrue("应该找到 setDependency1 方法", foundSetDependency1);
        assertTrue("应该找到 setDependency2 方法", foundSetDependency2);
        assertTrue("应该找到 initMethod 方法", foundInitMethod);
    }
    
    @Test
    public void testScanAutowiredMethodsWithInheritance() {
        // 测试继承关系中的方法扫描
        List<Method> methods = MethodInjector.scanAutowiredMethods(TestSubBean.class);
        
        assertEquals(4, methods.size()); // 父类3个 + 子类1个
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testScanAutowiredMethodsWithNullClass() {
        // 测试空类参数
        MethodInjector.scanAutowiredMethods(null);
    }
    
    @Test
    public void testIsAutowiredMethod() throws Exception {
        // 测试方法是否带有 @Autowired 注解
        Method autowiredMethod = TestBean.class.getDeclaredMethod("setDependency1", TestDependency1.class);
        Method normalMethod = TestBean.class.getDeclaredMethod("normalMethod");
        
        assertTrue(MethodInjector.isAutowiredMethod(autowiredMethod));
        assertFalse(MethodInjector.isAutowiredMethod(normalMethod));
        assertFalse(MethodInjector.isAutowiredMethod(null));
    }
    
    @Test
    public void testIsSetterMethod() throws Exception {
        // 测试是否为 setter 方法
        Method setterMethod = TestBean.class.getDeclaredMethod("setDependency1", TestDependency1.class);
        Method normalMethod = TestBean.class.getDeclaredMethod("normalMethod");
        Method initMethod = TestBean.class.getDeclaredMethod("initMethod", TestDependency1.class, TestDependency2.class);
        
        assertTrue(MethodInjector.isSetterMethod(setterMethod));
        assertFalse(MethodInjector.isSetterMethod(normalMethod));
        assertFalse(MethodInjector.isSetterMethod(initMethod));
        assertFalse(MethodInjector.isSetterMethod(null));
    }
    
    @Test
    public void testInjectMethodsSuccess() {
        // 测试成功的方法注入
        
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
        
        // 扫描并设置自动装配方法
        List<Method> autowiredMethods = MethodInjector.scanAutowiredMethods(TestBean.class);
        beanDefinition.setAutowiredMethods(autowiredMethods);
        
        // 执行方法注入
        methodInjector.injectMethods(testBean, beanDefinition);
        
        // 验证注入结果
        assertNotNull(testBean.dependency1);
        assertNotNull(testBean.dependency2);
        assertSame(dep1, testBean.dependency1);
        assertSame(dep2, testBean.dependency2);
        assertTrue(testBean.initMethodCalled);
    }
    
    @Test(expected = DependencyInjectionException.class)
    public void testInjectMethodsWithMissingRequiredDependency() {
        // 测试缺少必需依赖的情况
        
        TestBean testBean = new TestBean();
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class, "testBean");
        
        // 扫描并设置自动装配方法
        List<Method> autowiredMethods = MethodInjector.scanAutowiredMethods(TestBean.class);
        beanDefinition.setAutowiredMethods(autowiredMethods);
        
        // 执行方法注入（没有注册依赖 Bean，应该抛出异常）
        methodInjector.injectMethods(testBean, beanDefinition);
    }
    
    @Test
    public void testInjectMethodsWithOptionalDependency() {
        // 测试可选依赖的情况
        
        TestBeanWithOptionalDependency testBean = new TestBeanWithOptionalDependency();
        BeanDefinition beanDefinition = new BeanDefinition(TestBeanWithOptionalDependency.class, "testBean");
        
        // 扫描并设置自动装配方法
        List<Method> autowiredMethods = MethodInjector.scanAutowiredMethods(TestBeanWithOptionalDependency.class);
        beanDefinition.setAutowiredMethods(autowiredMethods);
        
        // 执行方法注入（没有注册依赖 Bean，但是可选的，不应该抛出异常）
        methodInjector.injectMethods(testBean, beanDefinition);
        
        // 验证可选依赖没有被注入
        assertNull(testBean.optionalDependency);
    }
    
    @Test(expected = DependencyInjectionException.class)
    public void testInjectMethodsWithMultipleCandidates() {
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
        
        List<Method> autowiredMethods = MethodInjector.scanAutowiredMethods(TestBeanWithSingleDependency.class);
        beanDefinition.setAutowiredMethods(autowiredMethods);
        
        // 执行方法注入（应该抛出异常，因为有多个候选 Bean）
        methodInjector.injectMethods(testBean, beanDefinition);
    }
    
    @Test
    public void testInjectMethodsWithNoParameterMethod() {
        // 测试无参方法的注入
        
        TestBeanWithNoParamMethod testBean = new TestBeanWithNoParamMethod();
        BeanDefinition beanDefinition = new BeanDefinition(TestBeanWithNoParamMethod.class, "testBean");
        
        List<Method> autowiredMethods = MethodInjector.scanAutowiredMethods(TestBeanWithNoParamMethod.class);
        beanDefinition.setAutowiredMethods(autowiredMethods);
        
        // 执行方法注入
        methodInjector.injectMethods(testBean, beanDefinition);
        
        // 验证无参方法被调用
        assertTrue(testBean.noParamMethodCalled);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInjectMethodsWithNullBeanInstance() {
        // 测试 Bean 实例为 null
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class, "testBean");
        methodInjector.injectMethods(null, beanDefinition);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInjectMethodsWithNullBeanDefinition() {
        // 测试 Bean 定义为 null
        TestBean testBean = new TestBean();
        methodInjector.injectMethods(testBean, null);
    }
    
    @Test
    public void testInjectMethodsWithNoAutowiredMethods() {
        // 测试没有自动装配方法的情况
        TestBeanWithoutAutowired testBean = new TestBeanWithoutAutowired();
        BeanDefinition beanDefinition = new BeanDefinition(TestBeanWithoutAutowired.class, "testBean");
        
        // 不应该抛出异常
        methodInjector.injectMethods(testBean, beanDefinition);
    }
    
    // 测试用的类
    
    public static class TestBean {
        private TestDependency1 dependency1;
        private TestDependency2 dependency2;
        private boolean initMethodCalled = false;
        
        @Autowired
        public void setDependency1(TestDependency1 dependency1) {
            this.dependency1 = dependency1;
        }
        
        @Autowired
        public void setDependency2(TestDependency2 dependency2) {
            this.dependency2 = dependency2;
        }
        
        @Autowired
        public void initMethod(TestDependency1 dep1, TestDependency2 dep2) {
            this.initMethodCalled = true;
        }
        
        public void normalMethod() {
            // 普通方法，不应该被注入
        }
    }
    
    public static class TestSubBean extends TestBean {
        @Autowired
        public void subMethod(TestDependency1 dependency) {
            // 子类的自动装配方法
        }
    }
    
    public static class TestBeanWithOptionalDependency {
        private TestDependency1 optionalDependency;
        
        @Autowired(required = false)
        public void setOptionalDependency(TestDependency1 optionalDependency) {
            this.optionalDependency = optionalDependency;
        }
    }
    
    public static class TestBeanWithSingleDependency {
        private TestDependency1 dependency;
        
        @Autowired
        public void setDependency(TestDependency1 dependency) {
            this.dependency = dependency;
        }
    }
    
    public static class TestBeanWithNoParamMethod {
        private boolean noParamMethodCalled = false;
        
        @Autowired
        public void initMethod() {
            this.noParamMethodCalled = true;
        }
    }
    
    public static class TestBeanWithoutAutowired {
        public void normalMethod() {
            // 普通方法
        }
    }
    
    public static class TestDependency1 {
    }
    
    public static class TestDependency2 {
    }
}
