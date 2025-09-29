package com.simplespring.core.io;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * DefaultResourceLoader 的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class DefaultResourceLoaderTest {

    private DefaultResourceLoader resourceLoader;

    @Before
    public void setUp() {
        resourceLoader = new DefaultResourceLoader();
    }

    @Test
    public void testDefaultConstructor() {
        DefaultResourceLoader loader = new DefaultResourceLoader();
        assertNotNull(loader.getClassLoader());
    }

    @Test
    public void testConstructorWithClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DefaultResourceLoader loader = new DefaultResourceLoader(classLoader);
        assertEquals(classLoader, loader.getClassLoader());
    }

    @Test
    public void testSetClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        resourceLoader.setClassLoader(classLoader);
        assertEquals(classLoader, resourceLoader.getClassLoader());
    }

    @Test
    public void testGetResource_ClassPath() {
        // 测试类路径前缀
        Resource resource = resourceLoader.getResource("classpath:org/junit/Test.class");
        assertNotNull(resource);
        assertTrue(resource instanceof ClassPathResource);
        assertTrue(resource.exists());
    }

    @Test
    public void testGetResource_WithoutPrefix() {
        // 测试没有前缀的路径（应该作为类路径资源处理）
        Resource resource = resourceLoader.getResource("org/junit/Test.class");
        assertNotNull(resource);
        assertTrue(resource instanceof ClassPathResource);
        assertTrue(resource.exists());
    }

    @Test
    public void testGetResource_HttpUrl() {
        // 测试 HTTP URL
        Resource resource = resourceLoader.getResource("http://www.example.com/test.txt");
        assertNotNull(resource);
        // 应该是 UrlResource 类型，但由于是内部类，我们检查它不是 ClassPathResource
        assertFalse(resource instanceof ClassPathResource);
        assertEquals("http://www.example.com/test.txt", resource.getDescription().substring(5, resource.getDescription().length() - 1));
    }

    @Test
    public void testGetResource_FileUrl() {
        // 测试 file URL
        Resource resource = resourceLoader.getResource("file:/tmp/test.txt");
        assertNotNull(resource);
        assertFalse(resource instanceof ClassPathResource);
        assertTrue(resource.getDescription().contains("file:/tmp/test.txt"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResource_NullLocation() {
        resourceLoader.getResource(null);
    }

    @Test
    public void testGetResource_EmptyLocation() {
        Resource resource = resourceLoader.getResource("");
        assertNotNull(resource);
        assertTrue(resource instanceof ClassPathResource);
    }

    @Test
    public void testGetResource_ClassPathPrefix() {
        Resource resource1 = resourceLoader.getResource("classpath:org/junit/Test.class");
        Resource resource2 = resourceLoader.getResource("org/junit/Test.class");
        
        // 两种方式都应该返回 ClassPathResource
        assertTrue(resource1 instanceof ClassPathResource);
        assertTrue(resource2 instanceof ClassPathResource);
        
        // 路径应该相同（去掉前缀后）
        ClassPathResource cpr1 = (ClassPathResource) resource1;
        ClassPathResource cpr2 = (ClassPathResource) resource2;
        assertEquals(cpr1.getPath(), cpr2.getPath());
    }

    @Test
    public void testGetResource_InvalidUrl() {
        // 测试无效的 URL 格式（应该回退到类路径资源）
        Resource resource = resourceLoader.getResource("invalid://url/format");
        assertNotNull(resource);
        assertTrue(resource instanceof ClassPathResource);
    }

    @Test
    public void testGetClassLoader_Default() {
        DefaultResourceLoader loader = new DefaultResourceLoader();
        ClassLoader classLoader = loader.getClassLoader();
        assertNotNull(classLoader);
    }

    @Test
    public void testGetClassLoader_Custom() {
        ClassLoader customClassLoader = new ClassLoader() {};
        DefaultResourceLoader loader = new DefaultResourceLoader(customClassLoader);
        assertEquals(customClassLoader, loader.getClassLoader());
    }

    @Test
    public void testGetClassLoader_SetToNull() {
        resourceLoader.setClassLoader(null);
        ClassLoader classLoader = resourceLoader.getClassLoader();
        assertNotNull(classLoader); // 应该返回默认的类加载器
    }
}
