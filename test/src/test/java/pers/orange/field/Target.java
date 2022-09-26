package pers.orange.field;

/**
 * @author orange add
 */
public class Target {

    private Integer id;

    private String targetName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public String toString() {
        return "Target{" +
            "id=" + id +
            ", name='" + targetName + '\'' +
            '}';
    }
}
