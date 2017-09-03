package bean;

import java.util.List;

/**
 * Created by linzhipeng on 2017/8/13.
 */
public class ParamInfo {

    private String paramName;
    private String paramType;
    private List<ParamInfo> paramInfos;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public List<ParamInfo> getParamInfos() {
        return paramInfos;
    }

    public void setParamInfos(List<ParamInfo> paramInfos) {
        this.paramInfos = paramInfos;
    }
}
