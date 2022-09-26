package pers.orange.field;

import org.junit.Test;
import pers.orange.BeanUtils;

import static org.junit.Assert.assertEquals;

/**
 * @author orange add
 */

public class TestToField {

    @Test
    public void testCreate(){
        Source source = new Source();
        source.setId( 1 );
        source.setSourceName( "source" );
        Target target = new Target();
        BeanUtils.copyProperties( source, target );
        assertEquals( source.getId(), target.getId() );
        assertEquals( source.getSourceName(), target.getTargetName() );
    }


}
