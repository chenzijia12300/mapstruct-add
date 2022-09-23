package pers.orange;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mapstruct.factory.Mappers;

/**
 * @author orange add
 */
public class BeanUtils {

    private static final Map<String,CommonMapper> commonMapperMap = new ConcurrentHashMap<>();

    private static final ClassLoader CLASS_LOADER = BeanUtils.class.getClassLoader();

    /**
     * 根据source对象更新target对象
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target){
        String mapperClassName = getMapperClassName( source.getClass(), target.getClass() );
        CommonMapper mapper = getMapper( mapperClassName );
        mapper.to( source, target );
    }

    /**
     * 创建T对象并赋值
     * @param source
     * @param targetClazz
     * @return
     * @param <T>
     */
    public static <T> T copProperties(Object source,Class<T> targetClazz){
        String mapperClassName = getMapperClassName( source.getClass(), targetClazz );
        CommonMapper mapper = getMapper( mapperClassName );
        return (T) mapper.to( source );
    }

    private static String getMapperClassName(Class source,Class target){
        String sourceName = source.getSimpleName();
        String targetName = target.getSimpleName();
        String packageName = source.getPackageName();
        String mapperClassName = packageName+"."+sourceName+"To"+targetName+"Mapper";
        return mapperClassName;
    }

    /**
     * 根据全限定类名获得对应的Mapper对象
     * @param mapperClassName
     * @return
     */
    private static CommonMapper getMapper(String mapperClassName){
        // 使用map缓存Mapper对象避免重复加载判断
        CommonMapper commonMapper = commonMapperMap.computeIfAbsent( mapperClassName, (className) -> {
            try {
                Class<?> mapperClass = CLASS_LOADER.loadClass( mapperClassName );
                CommonMapper mapper = (CommonMapper) Mappers.getMapper( mapperClass );
                return mapper;
            }
            catch ( ClassNotFoundException e ) {
                throw new RuntimeException( e );
            }
        } );
        if ( commonMapper == null ){
            throw new RuntimeException(mapperClassName+"不存在");
        }
        return commonMapper;
    }
}
