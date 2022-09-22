package pers.orange;

import org.junit.Test;
import pers.orange.BeanUtils;

/**
 * @author orange add
 */

public class TestMappingProcessor {

    @Test
    public void test01(){
        Source source = new Source();
        source.setId( 1 );
        source.setName( "source" );
        Target target = new Target();
        BeanUtils.copyProperties( source, target );
        System.out.println(target);
    }
}
