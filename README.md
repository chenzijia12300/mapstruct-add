# mapstruct-add
mapstruct 增强

利用APT自动生成Mapper类型

# 使用例子
```java
    @To( target = Target.class)
    public class Source {
    
        private Integer id;
    
        private String name;
        
        // Getter And Setter...
        
    }
    
    public class Target {
    
        private Integer id;
    
        private String name;
    
         // Getter And Setter...
    }

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

```