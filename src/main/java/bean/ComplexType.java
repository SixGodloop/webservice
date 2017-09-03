package bean;

import java.util.List;

/**
 * Created by linzhipeng on 2017/8/13.
 */
public class ComplexType {
    private String name;
    private List<Ele> eles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ele> getEles() {
        return eles;
    }

    public void setEles(List<Ele> eles) {
        this.eles = eles;
    }
}
