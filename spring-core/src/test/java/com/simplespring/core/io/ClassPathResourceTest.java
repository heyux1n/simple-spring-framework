package com.simplespring.core.io;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * ClassPathResource 的单元测试
 * 
 * @author SimpleSpring Framework
 */
public class ClassPathResourceTest {

    @Test
    public void testConstructorWithPath() {
        ClassPathResource resource = new ClassPathResource("test.properties");
        assertEquals("test.properties", resource.getPath());
        assertNotNull(resource.getClassLoader());
    }

    @Test
    public void testConstructorWithPathAndClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassPathResource resource = new ClassPathResource("test.properties", classLoader);
        assertEquals("test.properties", resource.getPath());
        assertEquals(classLoader, resource.getClassLoader());
    }

    @Test
    public void testConstructorWithPathAndClass() {
        ClassPathResource resource = new ClassPathResource("test.properties", ClassPathResourceTest.class);
        assertEquals("test.properties", resource.getPath());
        assertEquals(ClassPathResourceTest.class.getClassLoader(), resource.getClassLoader());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullPath() {
        new ClassPathResource(null);
    }

    @Test
    public void testPathCleaning() {
        // 测试路径清理
        ClassPathResource resource = new ClassPathResource("/test.properties");
        assertEquals("test.properties", resource.getPath());
        
        resource = new ClassPathResource("com\\example\\test.properties");
        assertEquals("com/example/test.properties", resource.getPath());
    }

    @Test
    public void testExists() {
        // 测试存在的资源（使用 JUnit 的类路径资源）
        ClassPathResource resource = new ClassPathResource("org/junit/Test.class");
        assertTrue(resource.exists());
        
        // 测试不存在的资源
        resource = new ClassPathResource("nonexistent.properties");
        assertFalse(resource.exists());
    }

    @Test
    public void testGetInputStream() throws IOException {
        // 测试获取存在资源的输入流
        ClassPathResource resource = new ClassPathResource("org/junit/Test.class");
        InputStream inputStream = resource.getInputStream();
        assertNotNull(inputStream);
        
        // 验证可以读取数据
        assertTrue(inputStream.read() != -1);
        inputStream.close();
    }

    @Test(expected = IOException.class)
    public void testGetInputStream_NonExistent() throws IOException {
        ClassPathResource resource = new ClassPathResource("nonexistent.properties");
        resource.getInputStream();
    }

    @Test
    public void testGetFilename() {
        ClassPathResource resource = new ClassPathResource("com/example/test.properties");
        assertEquals("test.properties", resource.getFilename());
        
        resource = new ClassPathResource("test");
        assertEquals("test", resource.getFilename());
        
        resource = new ClassPathResource("");
        assertNull(resource.getFilename());
    }

    @Test
    public void testGetDescription() {
        ClassPathResource resource = new ClassPathResource("test.properties");
        String description = resource.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("class path resource"));
        assertTrue(description.contains("test.properties"));
        
        // 测试相对于类的资源描述
        resource = new ClassPathResource("test.properties", ClassPathResourceTest.class);
        description = resource.getDescription();
        assertTrue(description.contains("ClassPathResourceTest"));
    }

    @Test
    public void testGetURL() throws IOException {
        ClassPathResource resource = new ClassPathResource("org/junit/Test.class");
        assertNotNull(resource.getURL());
        assertTrue(resource.getURL().toString().contains("Test.class"));
    }

    @Test(expected = IOException.class)
    public void testGetURL_NonExistent() throws IOException {
        ClassPathResource resource = new ClassPathResource("nonexistent.properties");
        resource.getURL();
    }

    @Test
    public void testContentLength() throws IOException {
        ClassPathResource resource = new ClassPathResource("org/junit/Test.class");
        long length = resource.contentLength();
        assertTrue(length > 0);
    }

    @Test
    public void testLastModified() throws IOException {
        ClassPathResource resource = new ClassPathResource("org/junit/Test.class");
        long lastModified = resource.lastModified();
        assertTrue(lastModified >= 0);
    }

    @Test
    public void testCreateRelative() throws IOException {
        ClassPathResource resource = new ClassPathResource("org/junit/Test.class");
        Resource relative = resource.createRelative("../junit/Test.class");
        
        assertNotNull(relative);
        assertTrue(relative instanceof ClassPathResource);
        assertTrue(relative.getDescription().contains("Test.class"));
        
        // 测试相对路径的构建
        ClassPathResource baseResource = new ClassPathResource("com/example/test.properties");
        Resource relativeResource = baseResource.createRelative("other.properties");
        assertTrue(relativeResource.getDescription().contains("other.properties"));
    }

    @Test
    public void testEquals() {
        ClassPathResource resource1 = new ClassPathResource("test.properties");
        ClassPathResource resource2 = new ClassPathResource("test.properties");
        ClassPathResource resource3 = new ClassPathResource("other.properties");
        
        assertEquals(resource1, resource2);
        assertNotEquals(resource1, resource3);
        assertNotEquals(resource1, null);
        assertNotEquals(resource1, "not a resource");
    }

    @Test
    public void testHashCode() {
        ClassPathResource resource1 = new ClassPathResource("test.properties");
        ClassPathResource resource2 = new ClassPathResource("test.properties");
        
        assertEquals(resource1.hashCode(), resource2.hashCode());
    }

    @Test
    public void testToString() {
        ClassPathResource resource = new ClassPathResource("test.properties");
        String toString = resource.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("test.properties"));
        assertEquals(resource.getDescription(), toString);
    }
}
