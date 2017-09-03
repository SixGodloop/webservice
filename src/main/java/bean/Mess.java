package bean;

import java.util.List;

/**
 * Created by linzhipeng on 2017/8/13.
 */
public class Mess {
    private String name;

    private List<Part> parts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }
}
