package pers.orange.field;

import pers.orange.To;
import pers.orange.ToField;

/**
 * @author orange add
 */
@To( target = Target.class)
public class Source {

    private Integer id;

    @ToField( target = "targetName")
    private String sourceName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "Source{" +
            "id=" + id +
            ", name='" + sourceName + '\'' +
            '}';
    }
}
