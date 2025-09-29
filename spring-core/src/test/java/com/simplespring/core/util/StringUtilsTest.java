package com.simplespring.core.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * StringUtils 工具类的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class StringUtilsTest {

    @Test
    public void testHasText() {
        // 测试正常情况
        assertTrue(StringUtils.hasText("hello"));
        assertTrue(StringUtils.hasText(" hello "));
        assertTrue(StringUtils.hasText("hello world"));
        
        // 测试边界情况
        assertFalse(StringUtils.hasText(null));
        assertFalse(StringUtils.hasText(""));
        assertFalse(StringUtils.hasText("   "));
        assertFalse(StringUtils.hasText("\t"));
        assertFalse(StringUtils.hasText("\n"));
    }

    @Test
    public void testIsEmpty() {
        // 测试正常情况
        assertFalse(StringUtils.isEmpty("hello"));
        assertFalse(StringUtils.isEmpty(" "));
        
        // 测试边界情况
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
    }

    @Test
    public void testTokenizeToStringArray() {
        // 测试正常情况
        String[] result = StringUtils.tokenizeToStringArray("a,b,c", ",");
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
        
        // 测试多个分隔符
        result = StringUtils.tokenizeToStringArray("a,b;c:d", ",;:");
        assertEquals(4, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
        assertEquals("d", result[3]);
        
        // 测试带空格的情况
        result = StringUtils.tokenizeToStringArray(" a , b , c ", ",");
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
        
        // 测试边界情况
        result = StringUtils.tokenizeToStringArray(null, ",");
        assertEquals(0, result.length);
        
        result = StringUtils.tokenizeToStringArray("", ",");
        assertEquals(0, result.length);
        
        result = StringUtils.tokenizeToStringArray("a", ",");
        assertEquals(1, result.length);
        assertEquals("a", result[0]);
    }

    @Test
    public void testUncapitalize() {
        // 测试正常情况
        assertEquals("hello", StringUtils.uncapitalize("Hello"));
        assertEquals("hELLO", StringUtils.uncapitalize("HELLO"));
        assertEquals("hello", StringUtils.uncapitalize("hello"));
        
        // 测试单字符
        assertEquals("a", StringUtils.uncapitalize("A"));
        assertEquals("a", StringUtils.uncapitalize("a"));
        
        // 测试边界情况
        assertEquals(null, StringUtils.uncapitalize(null));
        assertEquals("", StringUtils.uncapitalize(""));
    }

    @Test
    public void testCapitalize() {
        // 测试正常情况
        assertEquals("Hello", StringUtils.capitalize("hello"));
        assertEquals("HELLO", StringUtils.capitalize("hELLO"));
        assertEquals("Hello", StringUtils.capitalize("Hello"));
        
        // 测试单字符
        assertEquals("A", StringUtils.capitalize("a"));
        assertEquals("A", StringUtils.capitalize("A"));
        
        // 测试边界情况
        assertEquals(null, StringUtils.capitalize(null));
        assertEquals("", StringUtils.capitalize(""));
    }

    @Test
    public void testCleanPath() {
        // 测试正常情况
        assertEquals("com/example/Test", StringUtils.cleanPath("com\\example\\Test"));
        assertEquals("com/example/Test", StringUtils.cleanPath("com/example/Test"));
        assertEquals("com/example/Test", StringUtils.cleanPath("com\\example/Test"));
        
        // 测试边界情况
        assertEquals(null, StringUtils.cleanPath(null));
        assertEquals("", StringUtils.cleanPath(""));
        assertEquals("Test", StringUtils.cleanPath("Test"));
    }
}
