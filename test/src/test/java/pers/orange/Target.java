package pers.orange;

/**
 * @author orange add
 */
public class Target {

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
        return "Target{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
