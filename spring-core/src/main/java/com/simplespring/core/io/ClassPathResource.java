package com.simplespring.core.io;

import com.simplespring.core.util.ClassUtils;
import com.simplespring.core.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 类路径资源实现
 * 支持类路径资源访问，兼容 JDK 1.7
 * 
 * @author SimpleSpring Framework
 */
public class ClassPathResource implements Resource {

    private final String path;
    private final ClassLoader classLoader;
    private final Class<?> clazz;

    /**
     * 构造函数
     * 
     * @param path 资源路径
     */
    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    /**
     * 构造函数
     * 
     * @param path 资源路径
     * @param classLoader 类加载器
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null");
        }
        
        String pathToUse = StringUtils.cleanPath(path);
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        
        this.path = pathToUse;
        this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
        this.clazz = null;
    }

    /**
     * 构造函数
     * 
     * @param path 资源路径
     * @param clazz 相对的类
     */
    public ClassPathResource(String path, Class<?> clazz) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null");
        }
        
        this.path = StringUtils.cleanPath(path);
        this.classLoader = null;
        this.clazz = clazz;
    }

    /**
     * 获取资源路径
     * 
     * @return 资源路径
     */
    public final String getPath() {
        return this.path;
    }

    /**
     * 获取类加载器
     * 
     * @return 类加载器
     */
    public final ClassLoader getClassLoader() {
        return (this.clazz != null ? this.clazz.getClassLoader() : this.classLoader);
    }

    @Override
    public boolean exists() {
        return (resolveURL() != null);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream inputStream;
        
        if (this.clazz != null) {
            inputStream = this.clazz.getResourceAsStream(this.path);
        } else if (this.classLoader != null) {
            inputStream = this.classLoader.getResourceAsStream(this.path);
        } else {
            inputStream = ClassLoader.getSystemResourceAsStream(this.path);
        }
        
        if (inputStream == null) {
            throw new IOException("Resource '" + getDescription() + "' cannot be opened because it does not exist");
        }
        
        return inputStream;
    }

    @Override
    public String getFilename() {
        String filename = StringUtils.getShortName(this.path);
        return StringUtils.hasText(filename) ? filename : null;
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("class path resource [");
        
        String pathToUse = this.path;
        if (this.clazz != null && !pathToUse.startsWith("/")) {
            builder.append(ClassUtils.getShortName(this.clazz));
            builder.append('/');
        }
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        builder.append(pathToUse);
        builder.append(']');
        
        return builder.toString();
    }

    @Override
    public URL getURL() throws IOException {
        URL url = resolveURL();
        if (url == null) {
            throw new IOException("Resource '" + getDescription() + "' cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    @Override
    public long contentLength() throws IOException {
        URL url = getURL();
        URLConnection connection = url.openConnection();
        try {
            return connection.getContentLength();
        } finally {
            if (connection.getInputStream() != null) {
                connection.getInputStream().close();
            }
        }
    }

    @Override
    public long lastModified() throws IOException {
        URL url = getURL();
        URLConnection connection = url.openConnection();
        try {
            return connection.getLastModified();
        } finally {
            if (connection.getInputStream() != null) {
                connection.getInputStream().close();
            }
        }
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        String pathToUse = StringUtils.cleanPath(this.path + "/" + relativePath);
        return (this.clazz != null ? new ClassPathResource(pathToUse, this.clazz) :
                new ClassPathResource(pathToUse, this.classLoader));
    }

    /**
     * 解析资源 URL
     * 
     * @return 资源 URL，如果不存在则返回 null
     */
    protected URL resolveURL() {
        if (this.clazz != null) {
            return this.clazz.getResource(this.path);
        } else if (this.classLoader != null) {
            return this.classLoader.getResource(this.path);
        } else {
            return ClassLoader.getSystemResource(this.path);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ClassPathResource)) {
            return false;
        }
        
        ClassPathResource other = (ClassPathResource) obj;
        return (this.path.equals(other.path) &&
                ObjectUtils.nullSafeEquals(this.classLoader, other.classLoader) &&
                ObjectUtils.nullSafeEquals(this.clazz, other.clazz));
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    @Override
    public String toString() {
        return getDescription();
    }

    /**
     * 对象工具类（简化版）
     */
    private static class ObjectUtils {
        public static boolean nullSafeEquals(Object o1, Object o2) {
            if (o1 == o2) {
                return true;
            }
            if (o1 == null || o2 == null) {
                return false;
            }
            return o1.equals(o2);
        }
    }
}
