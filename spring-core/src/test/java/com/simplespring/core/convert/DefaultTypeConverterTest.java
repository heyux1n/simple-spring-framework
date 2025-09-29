package com.simplespring.core.convert;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * DefaultTypeConverter 的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class DefaultTypeConverterTest {

    private DefaultTypeConverter converter;

    @Before
    public void setUp() {
        converter = new DefaultTypeConverter();
    }

    @Test
    public void testConvertIfNecessary_SameType() {
        // 测试相同类型直接返回
        String value = "test";
        String result = converter.convertIfNecessary(value, String.class);
        assertSame(value, result);
        
        Integer intValue = 123;
        Integer intResult = converter.convertIfNecessary(intValue, Integer.class);
        assertSame(intValue, intResult);
    }

    @Test
    public void testConvertIfNecessary_NullValue() {
        // 测试 null 值转换到引用类型
        String result = converter.convertIfNecessary(null, String.class);
        assertNull(result);
        
        Integer intResult = converter.convertIfNecessary(null, Integer.class);
        assertNull(intResult);
    }

    @Test(expected = TypeMismatchException.class)
    public void testConvertIfNecessary_NullToPrimitive() {
        // 测试 null 值转换到基本类型应该抛出异常
        converter.convertIfNecessary(null, int.class);
    }

    @Test
    public void testConvertFromString_ToBoolean() {
        // 测试字符串到布尔值的转换
        assertTrue(converter.convertIfNecessary("true", boolean.class));
        assertTrue(converter.convertIfNecessary("TRUE", Boolean.class));
        assertTrue(converter.convertIfNecessary("1", boolean.class));
        
        assertFalse(converter.convertIfNecessary("false", boolean.class));
        assertFalse(converter.convertIfNecessary("FALSE", Boolean.class));
        assertFalse(converter.convertIfNecessary("0", boolean.class));
        assertFalse(converter.convertIfNecessary("anything", boolean.class));
    }

    @Test
    public void testConvertFromString_ToInteger() {
        // 测试字符串到整数的转换
        assertEquals(Integer.valueOf(123), converter.convertIfNecessary("123", Integer.class));
        assertEquals(Integer.valueOf(-456), converter.convertIfNecessary("-456", int.class));
        assertEquals(Integer.valueOf(0), converter.convertIfNecessary("0", Integer.class));
    }

    @Test(expected = TypeMismatchException.class)
    public void testConvertFromString_ToInteger_Invalid() {
        converter.convertIfNecessary("not a number", Integer.class);
    }

    @Test
    public void testConvertFromString_ToLong() {
        // 测试字符串到长整数的转换
        assertEquals(Long.valueOf(123456789L), converter.convertIfNecessary("123456789", Long.class));
        assertEquals(Long.valueOf(-987654321L), converter.convertIfNecessary("-987654321", long.class));
    }

    @Test
    public void testConvertFromString_ToDouble() {
        // 测试字符串到双精度浮点数的转换
        assertEquals(Double.valueOf(123.45), converter.convertIfNecessary("123.45", Double.class));
        assertEquals(Double.valueOf(-67.89), converter.convertIfNecessary("-67.89", double.class));
    }

    @Test
    public void testConvertFromString_ToFloat() {
        // 测试字符串到单精度浮点数的转换
        assertEquals(Float.valueOf(123.45f), converter.convertIfNecessary("123.45", Float.class));
        assertEquals(Float.valueOf(-67.89f), converter.convertIfNecessary("-67.89", float.class));
    }

    @Test
    public void testConvertFromString_ToCharacter() {
        // 测试字符串到字符的转换
        assertEquals(Character.valueOf('A'), converter.convertIfNecessary("A", Character.class));
        assertEquals(Character.valueOf('z'), converter.convertIfNecessary("z", char.class));
    }

    @Test(expected = TypeMismatchException.class)
    public void testConvertFromString_ToCharacter_Invalid() {
        converter.convertIfNecessary("AB", Character.class);
    }

    @Test
    public void testConvertPrimitive_NumberTypes() {
        // 测试数字类型之间的转换
        assertEquals(Integer.valueOf(123), converter.convertIfNecessary(123L, Integer.class));
        assertEquals(Long.valueOf(456), converter.convertIfNecessary(456, Long.class));
        assertEquals(Double.valueOf(123.0), converter.convertIfNecessary(123, Double.class));
        assertEquals(Float.valueOf(456.0f), converter.convertIfNecessary(456, Float.class));
    }

    @Test
    public void testConvertToString() {
        // 测试任何类型到字符串的转换
        assertEquals("123", converter.convertIfNecessary(123, String.class));
        assertEquals("true", converter.convertIfNecessary(true, String.class));
        assertEquals("123.45", converter.convertIfNecessary(123.45, String.class));
    }

    @Test
    public void testCanConvert() {
        // 测试相同类型
        assertTrue(converter.canConvert(String.class, String.class));
        assertTrue(converter.canConvert(Integer.class, Integer.class));
        
        // 测试继承关系
        assertTrue(converter.canConvert(String.class, Object.class));
        
        // 测试字符串到基本类型
        assertTrue(converter.canConvert(String.class, Integer.class));
        assertTrue(converter.canConvert(String.class, boolean.class));
        assertTrue(converter.canConvert(String.class, Double.class));
        
        // 测试任何类型到字符串
        assertTrue(converter.canConvert(Integer.class, String.class));
        assertTrue(converter.canConvert(Boolean.class, String.class));
        
        // 测试基本类型和包装类型之间的转换
        assertTrue(converter.canConvert(int.class, Integer.class));
        assertTrue(converter.canConvert(Integer.class, int.class));
        assertTrue(converter.canConvert(int.class, long.class));
        assertTrue(converter.canConvert(Long.class, Integer.class));
        
        // 测试不兼容的类型
        assertFalse(converter.canConvert(String.class, java.util.Date.class));
        assertFalse(converter.canConvert(Integer.class, Boolean.class));
        
        // 测试 null 情况
        assertFalse(converter.canConvert(null, String.class));
        assertFalse(converter.canConvert(String.class, null));
    }

    @Test(expected = TypeMismatchException.class)
    public void testConvertIfNecessary_UnsupportedConversion() {
        converter.convertIfNecessary("test", java.util.Date.class);
    }

    @Test
    public void testConvertFromString_WithWhitespace() {
        // 测试带空白字符的字符串转换
        assertEquals(Integer.valueOf(123), converter.convertIfNecessary("  123  ", Integer.class));
        assertTrue(converter.convertIfNecessary("  true  ", Boolean.class));
        assertEquals(Double.valueOf(123.45), converter.convertIfNecessary("  123.45  ", Double.class));
    }

    @Test
    public void testConvertFromString_EmptyString() {
        // 测试空字符串的转换
        assertNull(converter.convertIfNecessary("", Integer.class));
        assertFalse(converter.convertIfNecessary("   ", Boolean.class)); // 空字符串转布尔值为 false
        assertNull(converter.convertIfNecessary("", Double.class));
    }
}
