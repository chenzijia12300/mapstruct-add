package pers.orange;

import static org.junit.Assert.*;
import org.junit.Test;
import pers.orange.BeanUtils;

/**
 * @author orange add
 */

public class TestMappingProcessor {

    @Test
    public void testCreate(){
        Source source = new Source();
        source.setId( 1 );
        source.setName( "source" );
        Target target = new Target();
        BeanUtils.copyProperties( source, target );
        assertEquals( source.getId(), target.getId() );
        assertEquals( source.getName(), target.getName() );
    }

    @Test
    public void testUpdate(){
        Source source = new Source();
        source.setId( 1 );
        source.setName( "source" );
        Target target = BeanUtils.copProperties( source, Target.class );
        assertEquals( source.getId(), target.getId() );
        assertEquals( source.getName(), target.getName() );
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
