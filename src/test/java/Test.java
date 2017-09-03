import bean.*;
import org.dom4j.Element;
import util.WsdlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by linzhipeng on 2017/9/3.
 */
public class Test {

    public void test() throws Exception{
        //String wsdl = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx?wsdl";
        String wsdl = "http://115.29.2.80/ZJJGPublicInterface/Dowload_YCService/AcceptSgxkBy_YC.asmx?wsdl";
        Map<String,List<Element>> listMap = WsdlUtil.getElements(wsdl);
        List<Element> messages = listMap.get("messages");
        List<Element> schemas = listMap.get("schemas");
        List<Element> elements = new ArrayList<>();
        List<Element> complexType = new ArrayList<>();
        for (Element schema :schemas) {
            elements.addAll(schema.elements("element"));
            complexType.addAll(schema.elements("complexType"));
        }
        Map<String, Ele> eles = WsdlUtil.getEles(elements);
        Map<String, Mess> messes = WsdlUtil.getMessages(messages);
        System.out.println("messages");
        //System.out.println(JsonUtil.toJsonString(messes));;
        Map<String, ComplexType> complexTypes = WsdlUtil.getComplexTypes(complexType);
        Map<String,List<ParamInfo>> complexToSimple = WsdlUtil.transferComplexToSimple(eles,complexTypes);
        System.out.println("复杂类型转换为对应的基本类型map");
        //System.out.println(JsonUtil.toJsonString(complexToSimple));
        Map<String,List<ParamInfo>>  map =  WsdlUtil.transferMessToSimple(messes,complexTypes,complexToSimple,eles);
        System.out.println("每个message对应的基本类型");
        //System.out.println(JsonUtil.toJsonString(map));;
        List<Operat> methods = WsdlUtil.getMethods(wsdl);

        for (Operat o:methods) {
            String inputMess = o.getInputMessName();
            String outputMess = o.getOutputMessName();
            List<ParamInfo> input = map.get(inputMess);
            List<ParamInfo> output = map.get(outputMess);
            o.setInput(input);
            o.setOutput(output);
        }


        //System.out.println(JsonUtil.toJsonString(methods));;

        WsdlUtil.rebuild(methods);

        //System.out.println(JsonUtil.toJsonString(methods));;

        //先组建Message 和 element的对应关系
        //message 与part 关联 part与element 关联
    }
    //System.out.println("method");
    //System.out.println(JsonUtil.toJsonString(map));;
}
