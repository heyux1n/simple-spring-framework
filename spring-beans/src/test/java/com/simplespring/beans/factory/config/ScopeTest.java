package com.simplespring.beans.factory.config;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Scope 枚举测试类
 * 
 * @author SimpleSpring Framework
 */
public class ScopeTest {
    
    @Test
    public void testScopeValues() {
        // 测试枚举值
        assertEquals("singleton", Scope.SINGLETON.getValue());
        assertEquals("prototype", Scope.PROTOTYPE.getValue());
    }
    
    @Test
    public void testFromValue() {
        // 测试根据字符串值获取枚举
        assertEquals(Scope.SINGLETON, Scope.fromValue("singleton"));
        assertEquals(Scope.PROTOTYPE, Scope.fromValue("prototype"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFromValueWithInvalidValue() {
        // 测试无效值抛出异常
        Scope.fromValue("invalid");
    }
    
    @Test
    public void testIsSingleton() {
        // 测试单例判断
        assertTrue(Scope.SINGLETON.isSingleton());
        assertFalse(Scope.PROTOTYPE.isSingleton());
    }
    
    @Test
    public void testIsPrototype() {
        // 测试原型判断
        assertFalse(Scope.SINGLETON.isPrototype());
        assertTrue(Scope.PROTOTYPE.isPrototype());
    }
}
