package com.simplespring.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 资源接口
 * 抽象资源访问操作，兼容 JDK 1.7
 * 
 * @author SimpleSpring Framework
 */
public interface Resource {

    /**
     * 检查资源是否存在
     * 
     * @return 如果资源存在返回 true，否则返回 false
     */
    boolean exists();

    /**
     * 获取资源的输入流
     * 
     * @return 资源的输入流
     * @throws IOException 如果无法获取输入流
     */
    InputStream getInputStream() throws IOException;

    /**
     * 获取资源的文件名
     * 
     * @return 资源的文件名，如果无法确定则返回 null
     */
    String getFilename();

    /**
     * 获取资源的描述信息
     * 
     * @return 资源的描述信息
     */
    String getDescription();

    /**
     * 获取资源的 URL
     * 
     * @return 资源的 URL
     * @throws IOException 如果无法获取 URL
     */
    URL getURL() throws IOException;

    /**
     * 获取资源的内容长度
     * 
     * @return 资源的内容长度，如果无法确定则返回 -1
     * @throws IOException 如果发生 I/O 错误
     */
    long contentLength() throws IOException;

    /**
     * 获取资源的最后修改时间
     * 
     * @return 资源的最后修改时间（毫秒），如果无法确定则返回 0
     * @throws IOException 如果发生 I/O 错误
     */
    long lastModified() throws IOException;

    /**
     * 创建相对于此资源的资源
     * 
     * @param relativePath 相对路径
     * @return 相对资源
     * @throws IOException 如果无法创建相对资源
     */
    Resource createRelative(String relativePath) throws IOException;
}
