package pers.orange;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mapstruct.factory.Mappers;

/**
 * @author orange add
 */
public class BeanUtils {

    private static final Map<String,CommonMapper> commonMapperMap = new ConcurrentHashMap<>();


    public static void copyProperties(Object source, Object target){
        ClassLoader classLoader = BeanUtils.class.getClassLoader();
        String sourceName = source.getClass().getSimpleName();
        String targetName = target.getClass().getSimpleName();
        String packageName = source.getClass().getPackageName();
        String mapperClassName = packageName+"."+sourceName+"To"+targetName+"Mapper";
        CommonMapper commonMapper = commonMapperMap.computeIfAbsent( mapperClassName, (className) -> {
            try {
                Class<?> mapperClass = classLoader.loadClass( mapperClassName );
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
        commonMapper.to( source, target );
    }
}
