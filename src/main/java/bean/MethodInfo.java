package bean;

import java.util.List;

/**
 * Created by linzhipeng on 2017/8/15.
 */
public class MethodInfo {
    private String name;
    private List<ParamInfo> input;
    private List<ParamInfo> output;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParamInfo> getInput() {
        return input;
    }

    public void setInput(List<ParamInfo> input) {
        this.input = input;
    }

    public List<ParamInfo> getOutput() {
        return output;
    }

    public void setOutput(List<ParamInfo> output) {
        this.output = output;
    }
}
