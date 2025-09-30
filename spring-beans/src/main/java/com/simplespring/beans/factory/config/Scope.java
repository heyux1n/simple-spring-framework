package com.simplespring.beans.factory.config;

/**
 * Bean 作用域枚举
 * 定义 Bean 的生命周期和实例化策略
 * 
 * @author SimpleSpring Framework
 */
public enum Scope {
    
    /**
     * 单例模式 - 容器中只存在一个实例
     * 这是默认的作用域，Bean 在容器启动时创建，整个应用生命周期内复用同一个实例
     */
    SINGLETON("singleton"),
    
    /**
     * 原型模式 - 每次请求都创建新实例
     * 每次调用 getBean() 方法时都会创建一个新的 Bean 实例
     */
    PROTOTYPE("prototype");
    
    private final String value;
    
    /**
     * 构造函数
     * @param value 作用域的字符串表示
     */
    Scope(String value) {
        this.value = value;
    }
    
    /**
     * 获取作用域的字符串值
     * @return 作用域字符串
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 根据字符串值获取对应的作用域枚举
     * @param value 作用域字符串
     * @return 对应的 Scope 枚举值
     * @throws IllegalArgumentException 如果找不到对应的作用域
     */
    public static Scope fromValue(String value) {
        for (Scope scope : values()) {
            if (scope.value.equals(value)) {
                return scope;
            }
        }
        throw new IllegalArgumentException("未知的作用域: " + value);
    }
    
    /**
     * 判断是否为单例作用域
     * @return 如果是单例作用域返回 true，否则返回 false
     */
    public boolean isSingleton() {
        return this == SINGLETON;
    }
    
    /**
     * 判断是否为原型作用域
     * @return 如果是原型作用域返回 true，否则返回 false
     */
    public boolean isPrototype() {
        return this == PROTOTYPE;
    }
}
