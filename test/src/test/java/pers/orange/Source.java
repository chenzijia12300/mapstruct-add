package pers.orange;

import pers.orange.To;

/**
 * @author orange add
 */
@To( target = Target.class)
public class Source {

    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Source{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
