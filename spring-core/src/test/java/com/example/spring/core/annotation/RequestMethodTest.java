package com.example.spring.core.annotation;

import com.simplespring.core.annotation.RequestMethod;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * RequestMethod 枚举的单元测试
 * 
 * 测试 RequestMethod 枚举的基本功能，包括：
 * 1. 枚举值的存在性验证
 * 2. 枚举值的数量和名称
 * 3. 枚举的基本属性
 */
public class RequestMethodTest {

    @Test
    public void testAllHttpMethods() {
        // 验证所有 HTTP 方法枚举值都存在
        RequestMethod[] methods = RequestMethod.values();
        
        assertEquals("应该有 7 个 HTTP 方法", 7, methods.length);
        
        // 验证每个方法都存在
        boolean hasGet = false, hasPost = false, hasPut = false, hasDelete = false;
        boolean hasPatch = false, hasHead = false, hasOptions = false;
        
        for (RequestMethod method : methods) {
            switch (method) {
                case GET:
                    hasGet = true;
                    break;
                case POST:
                    hasPost = true;
                    break;
                case PUT:
                    hasPut = true;
                    break;
                case DELETE:
                    hasDelete = true;
                    break;
                case PATCH:
                    hasPatch = true;
                    break;
                case HEAD:
                    hasHead = true;
                    break;
                case OPTIONS:
                    hasOptions = true;
                    break;
            }
        }
        
        assertTrue("应该包含 GET 方法", hasGet);
        assertTrue("应该包含 POST 方法", hasPost);
        assertTrue("应该包含 PUT 方法", hasPut);
        assertTrue("应该包含 DELETE 方法", hasDelete);
        assertTrue("应该包含 PATCH 方法", hasPatch);
        assertTrue("应该包含 HEAD 方法", hasHead);
        assertTrue("应该包含 OPTIONS 方法", hasOptions);
    }

    @Test
    public void testSpecificHttpMethods() {
        // 测试特定的 HTTP 方法
        assertEquals("GET 方法名称应该正确", "GET", RequestMethod.GET.name());
        assertEquals("POST 方法名称应该正确", "POST", RequestMethod.POST.name());
        assertEquals("PUT 方法名称应该正确", "PUT", RequestMethod.PUT.name());
        assertEquals("DELETE 方法名称应该正确", "DELETE", RequestMethod.DELETE.name());
        assertEquals("PATCH 方法名称应该正确", "PATCH", RequestMethod.PATCH.name());
        assertEquals("HEAD 方法名称应该正确", "HEAD", RequestMethod.HEAD.name());
        assertEquals("OPTIONS 方法名称应该正确", "OPTIONS", RequestMethod.OPTIONS.name());
    }

    @Test
    public void testValueOfMethod() {
        // 测试 valueOf 方法
        assertEquals("valueOf('GET') 应该返回 GET", RequestMethod.GET, RequestMethod.valueOf("GET"));
        assertEquals("valueOf('POST') 应该返回 POST", RequestMethod.POST, RequestMethod.valueOf("POST"));
        assertEquals("valueOf('PUT') 应该返回 PUT", RequestMethod.PUT, RequestMethod.valueOf("PUT"));
        assertEquals("valueOf('DELETE') 应该返回 DELETE", RequestMethod.DELETE, RequestMethod.valueOf("DELETE"));
        assertEquals("valueOf('PATCH') 应该返回 PATCH", RequestMethod.PATCH, RequestMethod.valueOf("PATCH"));
        assertEquals("valueOf('HEAD') 应该返回 HEAD", RequestMethod.HEAD, RequestMethod.valueOf("HEAD"));
        assertEquals("valueOf('OPTIONS') 应该返回 OPTIONS", RequestMethod.OPTIONS, RequestMethod.valueOf("OPTIONS"));
    }

    @Test
    public void testInvalidValueOf() {
        // 测试无效的 valueOf 调用
        try {
            RequestMethod.valueOf("INVALID");
            fail("应该抛出 IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 预期的异常
            assertTrue("异常消息应该包含无效值", e.getMessage().contains("INVALID"));
        }
        
        try {
            RequestMethod.valueOf("get"); // 小写
            fail("应该抛出 IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 预期的异常
            assertTrue("异常消息应该包含无效值", e.getMessage().contains("get"));
        }
    }

    @Test
    public void testEnumEquality() {
        // 测试枚举相等性
        assertEquals("相同的枚举值应该相等", RequestMethod.GET, RequestMethod.GET);
        assertNotEquals("不同的枚举值应该不相等", RequestMethod.GET, RequestMethod.POST);
        
        // 测试与 valueOf 返回值的相等性
        assertEquals("valueOf 返回的值应该与直接引用相等", 
                     RequestMethod.GET, RequestMethod.valueOf("GET"));
    }

    @Test
    public void testEnumOrdinal() {
        // 测试枚举序号（虽然不应该依赖序号，但可以验证枚举定义的顺序）
        RequestMethod[] methods = RequestMethod.values();
        
        for (int i = 0; i < methods.length; i++) {
            assertEquals("枚举序号应该正确", i, methods[i].ordinal());
        }
    }

    @Test
    public void testEnumToString() {
        // 测试 toString 方法（默认返回枚举名称）
        assertEquals("GET.toString() 应该返回 'GET'", "GET", RequestMethod.GET.toString());
        assertEquals("POST.toString() 应该返回 'POST'", "POST", RequestMethod.POST.toString());
        assertEquals("PUT.toString() 应该返回 'PUT'", "PUT", RequestMethod.PUT.toString());
        assertEquals("DELETE.toString() 应该返回 'DELETE'", "DELETE", RequestMethod.DELETE.toString());
        assertEquals("PATCH.toString() 应该返回 'PATCH'", "PATCH", RequestMethod.PATCH.toString());
        assertEquals("HEAD.toString() 应该返回 'HEAD'", "HEAD", RequestMethod.HEAD.toString());
        assertEquals("OPTIONS.toString() 应该返回 'OPTIONS'", "OPTIONS", RequestMethod.OPTIONS.toString());
    }

    @Test
    public void testEnumInSwitchStatement() {
        // 测试在 switch 语句中使用枚举
        for (RequestMethod method : RequestMethod.values()) {
            String description = getMethodDescription(method);
            assertNotNull("每个方法都应该有描述", description);
            assertFalse("描述不应该为空", description.isEmpty());
        }
    }

    private String getMethodDescription(RequestMethod method) {
        switch (method) {
            case GET:
                return "获取资源";
            case POST:
                return "创建资源";
            case PUT:
                return "更新资源";
            case DELETE:
                return "删除资源";
            case PATCH:
                return "部分更新资源";
            case HEAD:
                return "获取资源头信息";
            case OPTIONS:
                return "获取支持的方法";
            default:
                return "未知方法";
        }
    }

    @Test
    public void testEnumComparison() {
        // 测试枚举比较
        assertTrue("GET 应该等于 GET", RequestMethod.GET.equals(RequestMethod.GET));
        assertFalse("GET 不应该等于 POST", RequestMethod.GET.equals(RequestMethod.POST));
        
        // 测试 hashCode
        assertEquals("相同枚举值的 hashCode 应该相等", 
                     RequestMethod.GET.hashCode(), RequestMethod.GET.hashCode());
        
        // 测试 compareTo（基于序号）
        assertTrue("GET 的序号应该小于 POST", RequestMethod.GET.compareTo(RequestMethod.POST) < 0);
        assertEquals("相同枚举值的 compareTo 应该返回 0", 0, RequestMethod.GET.compareTo(RequestMethod.GET));
    }
}
