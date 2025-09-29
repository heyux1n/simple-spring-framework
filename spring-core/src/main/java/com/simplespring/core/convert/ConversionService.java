package com.simplespring.core.convert;

/**
 * 转换服务接口
 * 扩展类型转换功能，提供更丰富的转换能力
 * 
 * @author SimpleSpring Framework
 */
public interface ConversionService extends TypeConverter {

    /**
     * 检查是否可以从源类型描述符转换到目标类型描述符
     * 
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 如果可以转换返回 true，否则返回 false
     */
    boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);

    /**
     * 将源对象从源类型描述符转换到目标类型描述符
     * 
     * @param source 源对象
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 转换后的对象
     * @throws TypeMismatchException 如果转换失败
     */
    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws TypeMismatchException;
}
