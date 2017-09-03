package bean;

import java.util.List;

/**
 * Created by linzhipeng on 2017/8/13.
 */
public class Operat {

    private String name;
    private String inputMessName;
    private List<ParamInfo> input;
    private String outputMessName;
    private List<ParamInfo> output;
    private String soapAction;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInputMessName() {
        return inputMessName;
    }

    public void setInputMessName(String inputMessName) {
        this.inputMessName = inputMessName;
    }

    public List<ParamInfo> getInput() {
        return input;
    }

    public void setInput(List<ParamInfo> input) {
        this.input = input;
    }

    public String getOutputMessName() {
        return outputMessName;
    }

    public void setOutputMessName(String outputMessName) {
        this.outputMessName = outputMessName;
    }

    public List<ParamInfo> getOutput() {
        return output;
    }

    public void setOutput(List<ParamInfo> output) {
        this.output = output;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }
}
