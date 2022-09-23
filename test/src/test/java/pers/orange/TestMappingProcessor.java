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
        Target target1 = BeanUtils.copProperties( source, Target.class );
        System.out.println(target1);
    }

    @Test
    public void testNoExistMapper(){
        Target target = new Target();
        target.setId( 1 );
        target.setName( "target" );
        Source source = BeanUtils.copProperties( target, Source.class );
        System.out.println(source);
    }

}
