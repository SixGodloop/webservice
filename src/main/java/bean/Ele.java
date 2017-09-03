package bean;

import java.util.List;

/**
 * Created by linzhipeng on 2017/8/13.
 */
public class Ele {

    private String name;

    private String type;

    private Boolean isBasic;

    private String ref;

    private List<ComplexType> complexTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getBasic() {
        return isBasic;
    }

    public void setBasic(Boolean basic) {
        isBasic = basic;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public List<ComplexType> getComplexTypes() {
        return complexTypes;
    }

    public void setComplexTypes(List<ComplexType> complexTypes) {
        this.complexTypes = complexTypes;
    }

    @Override
    public String toString() {
        return "Ele{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isBasic=" + isBasic +
                ", ref='" + ref + '\'' +
                ", complexTypes=" + complexTypes +
                '}';
    }
}
