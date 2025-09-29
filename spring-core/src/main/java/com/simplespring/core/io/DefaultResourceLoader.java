package com.simplespring.core.io;

import com.simplespring.core.util.ClassUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 默认资源加载器实现
 * 支持类路径和 URL 资源加载，兼容 JDK 1.7
 * 
 * @author SimpleSpring Framework
 */
public class DefaultResourceLoader implements ResourceLoader {

    private ClassLoader classLoader;

    /**
     * 默认构造函数
     */
    public DefaultResourceLoader() {
        this.classLoader = ClassUtils.getDefaultClassLoader();
    }

    /**
     * 构造函数
     * 
     * @param classLoader 类加载器
     */
    public DefaultResourceLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 设置类加载器
     * 
     * @param classLoader 类加载器
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
    }

    @Override
    public Resource getResource(String location) {
        if (location == null) {
            throw new IllegalArgumentException("Location must not be null");
        }

        // 处理类路径资源
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
        }

        // 尝试作为 URL 处理
        try {
            URL url = new URL(location);
            return new UrlResource(url);
        } catch (MalformedURLException ex) {
            // 不是有效的 URL，作为类路径资源处理
            return new ClassPathResource(location, getClassLoader());
        }
    }

    /**
     * URL 资源实现
     */
    private static class UrlResource implements Resource {
        private final URL url;

        public UrlResource(URL url) {
            if (url == null) {
                throw new IllegalArgumentException("URL must not be null");
            }
            this.url = url;
        }

        @Override
        public boolean exists() {
            try {
                URL testUrl = this.url;
                if (ResourceUtils.isFileURL(testUrl)) {
                    return ResourceUtils.getFile(testUrl).exists();
                } else {
                    // 尝试打开连接来检查资源是否存在
                    testUrl.openConnection().getInputStream().close();
                    return true;
                }
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public java.io.InputStream getInputStream() throws java.io.IOException {
            return this.url.openStream();
        }

        @Override
        public String getFilename() {
            String path = this.url.getPath();
            int lastSlash = path.lastIndexOf('/');
            return (lastSlash != -1 ? path.substring(lastSlash + 1) : path);
        }

        @Override
        public String getDescription() {
            return "URL [" + this.url + "]";
        }

        @Override
        public URL getURL() {
            return this.url;
        }

        @Override
        public long contentLength() throws java.io.IOException {
            return this.url.openConnection().getContentLength();
        }

        @Override
        public long lastModified() throws java.io.IOException {
            return this.url.openConnection().getLastModified();
        }

        @Override
        public Resource createRelative(String relativePath) throws java.io.IOException {
            return new UrlResource(new URL(this.url, relativePath));
        }

        @Override
        public boolean equals(Object obj) {
            return (this == obj || (obj instanceof UrlResource && this.url.equals(((UrlResource) obj).url)));
        }

        @Override
        public int hashCode() {
            return this.url.hashCode();
        }

        @Override
        public String toString() {
            return getDescription();
        }
    }

    /**
     * 资源工具类（简化版）
     */
    private static class ResourceUtils {
        private static final String URL_PROTOCOL_FILE = "file";

        public static boolean isFileURL(URL url) {
            String protocol = url.getProtocol();
            return (URL_PROTOCOL_FILE.equals(protocol));
        }

        public static java.io.File getFile(URL resourceUrl) throws java.io.IOException {
            if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
                throw new java.io.IOException("URL cannot be resolved to absolute file path because it does not reside in the file system: " + resourceUrl);
            }
            try {
                return new java.io.File(resourceUrl.toURI().getSchemeSpecificPart());
            } catch (java.net.URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                return new java.io.File(resourceUrl.getFile());
            }
        }
    }
}
