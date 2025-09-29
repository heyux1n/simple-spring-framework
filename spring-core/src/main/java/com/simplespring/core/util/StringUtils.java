package com.simplespring.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 字符串工具类
 * 提供字符串处理的常用方法，兼容 JDK 1.7
 * 
 * @author SimpleSpring Framework
 */
public class StringUtils {

    /**
     * 检查字符串是否有实际内容（不为 null 且去除空白字符后长度大于 0）
     * 
     * @param str 要检查的字符串
     * @return 如果字符串有实际内容返回 true，否则返回 false
     */
    public static boolean hasText(String str) {
        return str != null && str.trim().length() > 0;
    }

    /**
     * 检查字符串是否为空（null 或长度为 0）
     * 
     * @param str 要检查的字符串
     * @return 如果字符串为空返回 true，否则返回 false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 将字符串按指定分隔符分割成字符串数组
     * 使用 JDK 1.7 兼容的 StringTokenizer 实现
     * 
     * @param str 要分割的字符串
     * @param delimiters 分隔符
     * @return 分割后的字符串数组
     */
    public static String[] tokenizeToStringArray(String str, String delimiters) {
        if (str == null) {
            return new String[0];
        }
        
        StringTokenizer tokenizer = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.length() > 0) {
                tokens.add(token);
            }
        }
        
        return tokens.toArray(new String[tokens.size()]);
    }

    /**
     * 将字符串首字母转换为小写
     * 
     * @param str 要转换的字符串
     * @return 首字母小写的字符串
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 将字符串首字母转换为大写
     * 
     * @param str 要转换的字符串
     * @return 首字母大写的字符串
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 清理路径字符串，将反斜杠转换为正斜杠
     * 
     * @param path 路径字符串
     * @return 清理后的路径字符串
     */
    public static String cleanPath(String path) {
        if (isEmpty(path)) {
            return path;
        }
        
        return path.replace('\\', '/');
    }

    /**
     * 获取路径的短名称（文件名部分）
     * 
     * @param path 路径字符串
     * @return 短名称
     */
    public static String getShortName(String path) {
        if (isEmpty(path)) {
            return path;
        }
        
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return path.substring(lastSlashIndex + 1);
        }
        
        return path;
    }
}
