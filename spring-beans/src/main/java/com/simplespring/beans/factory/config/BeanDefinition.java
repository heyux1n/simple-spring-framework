package com.simplespring.beans.factory.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean 定义类
 * 存储 Bean 的元数据信息，包括类型、作用域、依赖注入信息等
 * 
 * @author SimpleSpring Framework
 */
public class BeanDefinition {
    
    /**
     * Bean 的 Class 类型
     */
    private Class<?> beanClass;
    
    /**
     * Bean 的名称，如果未指定则使用类名的首字母小写形式
     */
    private String beanName;
    
    /**
     * Bean 的作用域，默认为单例
     */
    private Scope scope = Scope.SINGLETON;
    
    /**
     * 是否为单例 Bean，默认为 true
     */
    private boolean singleton = true;
    
    /**
     * 用于创建 Bean 的构造函数
     */
    private Constructor<?> constructor;
    
    /**
     * 需要进行依赖注入的字段列表
     */
    private List<Field> autowiredFields = new ArrayList<Field>();
    
    /**
     * 需要进行依赖注入的方法列表
     */
    private List<Method> autowiredMethods = new ArrayList<Method>();
    
    /**
     * 构造函数参数类型列表
     */
    private Class<?>[] constructorParameterTypes;
    
    /**
     * 是否为懒加载，默认为 false
     */
    private boolean lazyInit = false;
    
    /**
     * 默认构造函数
     */
    public BeanDefinition() {
    }
    
    /**
     * 构造函数
     * @param beanClass Bean 的 Class 类型
     */
    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }
    
    /**
     * 构造函数
     * @param beanClass Bean 的 Class 类型
     * @param beanName Bean 的名称
     */
    public BeanDefinition(Class<?> beanClass, String beanName) {
        this.beanClass = beanClass;
        this.beanName = beanName;
    }
    
    // Getter 和 Setter 方法
    
    public Class<?> getBeanClass() {
        return beanClass;
    }
    
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }
    
    public String getBeanName() {
        return beanName;
    }
    
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
    
    public Scope getScope() {
        return scope;
    }
    
    public void setScope(Scope scope) {
        this.scope = scope;
        this.singleton = (scope == Scope.SINGLETON);
    }
    
    public boolean isSingleton() {
        return singleton;
    }
    
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
        this.scope = singleton ? Scope.SINGLETON : Scope.PROTOTYPE;
    }
    
    public Constructor<?> getConstructor() {
        return constructor;
    }
    
    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
        if (constructor != null) {
            this.constructorParameterTypes = constructor.getParameterTypes();
        }
    }
    
    public List<Field> getAutowiredFields() {
        return autowiredFields;
    }
    
    public void setAutowiredFields(List<Field> autowiredFields) {
        this.autowiredFields = autowiredFields != null ? autowiredFields : new ArrayList<Field>();
    }
    
    public void addAutowiredField(Field field) {
        if (field != null) {
            this.autowiredFields.add(field);
        }
    }
    
    public List<Method> getAutowiredMethods() {
        return autowiredMethods;
    }
    
    public void setAutowiredMethods(List<Method> autowiredMethods) {
        this.autowiredMethods = autowiredMethods != null ? autowiredMethods : new ArrayList<Method>();
    }
    
    public void addAutowiredMethod(Method method) {
        if (method != null) {
            this.autowiredMethods.add(method);
        }
    }
    
    public Class<?>[] getConstructorParameterTypes() {
        return constructorParameterTypes;
    }
    
    public void setConstructorParameterTypes(Class<?>[] constructorParameterTypes) {
        this.constructorParameterTypes = constructorParameterTypes;
    }
    
    public boolean isLazyInit() {
        return lazyInit;
    }
    
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    
    /**
     * 判断是否有构造函数参数
     * @return 如果有构造函数参数返回 true，否则返回 false
     */
    public boolean hasConstructorParameters() {
        return constructorParameterTypes != null && constructorParameterTypes.length > 0;
    }
    
    /**
     * 获取构造函数参数数量
     * @return 构造函数参数数量
     */
    public int getConstructorParameterCount() {
        return constructorParameterTypes != null ? constructorParameterTypes.length : 0;
    }
    
    /**
     * 判断是否有需要注入的字段
     * @return 如果有需要注入的字段返回 true，否则返回 false
     */
    public boolean hasAutowiredFields() {
        return autowiredFields != null && !autowiredFields.isEmpty();
    }
    
    /**
     * 判断是否有需要注入的方法
     * @return 如果有需要注入的方法返回 true，否则返回 false
     */
    public boolean hasAutowiredMethods() {
        return autowiredMethods != null && !autowiredMethods.isEmpty();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BeanDefinition{");
        sb.append("beanClass=").append(beanClass != null ? beanClass.getName() : "null");
        sb.append(", beanName='").append(beanName).append('\'');
        sb.append(", scope=").append(scope);
        sb.append(", singleton=").append(singleton);
        sb.append(", lazyInit=").append(lazyInit);
        sb.append(", autowiredFields=").append(autowiredFields.size());
        sb.append(", autowiredMethods=").append(autowiredMethods.size());
        sb.append('}');
        return sb.toString();
    }
}
