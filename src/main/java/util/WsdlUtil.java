package util;

import bean.*;
import bean.Part;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by linzhipeng on 2017/8/13.
 */
public class WsdlUtil {

    private static Set<String> baseType;

    static{
        baseType =  new HashSet<>();
        baseType.add("string");
        baseType.add("integer");
        baseType.add("long");
        baseType.add("int");
        baseType.add("short");
        baseType.add("byte");
        baseType.add("decimal");
        baseType.add("float");
        baseType.add("double");
        baseType.add("boolean");
        baseType.add("dateTime");
        baseType.add("date");
        baseType.add("time");
        baseType.add("boolean");
    }


    // 解析获取wsdl的operations 即所有的方法 对应的inputmessage和输出message
    public static List<Operat> getMethods(String wsdl)throws WSDLException {
        WSDLFactory factory = WSDLFactory .newInstance();
        WSDLReader reader = factory.newWSDLReader();
        Definition definition = reader.readWSDL(wsdl);
        List<Operat> operats = new ArrayList<>();
        Map<QName,Service> services = definition.getServices();
        for (Map.Entry<QName, Service> serviceEntry: services.entrySet()) {
            Service service = serviceEntry.getValue();
            Map<String,Port> portMap = service.getPorts();
            for (Map.Entry<String,Port> portEntry:portMap.entrySet()) {
                Port port = portEntry.getValue();
                Binding binding = port.getBinding();
                List<BindingOperation> bindingOperations = binding.getBindingOperations();
                for (BindingOperation bindingOperation:bindingOperations) {
                    String name =  bindingOperation.getName();
                    Operation operation = bindingOperation.getOperation();
                    Message input = operation.getInput().getMessage();
                    Message output = operation.getOutput().getMessage();
                    String inputName = input.getQName().getLocalPart();
                    String outputName = output.getQName().getLocalPart();
                    List<ExtensibilityElement> list = bindingOperation.getExtensibilityElements();
                    Operat operat = new Operat();

                    //解析获取soapAction
                    for (ExtensibilityElement e:list) {
                        if (e instanceof SOAPOperation){
                            String soapAction = ((SOAPOperation) e).getSoapActionURI();
                            operat.setSoapAction (soapAction);
                            break;
                        }else if (e instanceof SOAP12Operation){
                            String soapAction = ((SOAP12Operation) e).getSoapActionURI();
                            operat.setSoapAction (soapAction);
                            break;
                        }
                    }
                    operat.setName(name);
                    operat.setInputMessName(inputName);
                    operat.setOutputMessName(outputName);
                    operats.add(operat);
                }
            }
        }
        return operats;
    }


    //解析获取命名空间
    public static String getTargetNameSpace(String wsdl) throws WSDLException{
        WSDLFactory factory = WSDLFactory .newInstance();
        WSDLReader reader = factory.newWSDLReader();
        Definition definition = reader.readWSDL(wsdl);
        String uri = definition.getTargetNamespace();
        return uri;
    }
    //取得所有的messages元素和schema元素
    public static Map<String,List<Element>> getElements(String wsdl) throws MalformedURLException, DocumentException {
        SAXReader reader = new SAXReader();
        URL url = new URL(wsdl);
        Document document = reader.read(url);
        Element root = document.getRootElement();
        Element types = root.element("types");
        List<Element> messages =  root.elements("message");
        List<Element> schemas =  types.elements();
        Map<String,List<Element>> result = new HashMap<>();
        result.put("messages",messages);
        result.put("schemas",schemas);
        return result;
    }

    //将element转换为mess对象
    public static Map<String, Mess> getMessages(List<Element> messages){
        Map<String,Mess> messes = new HashMap<>();
        for (Element e:messages) {
            Mess mess = new Mess();
            mess.setName(e.attributeValue("name"));
            List<Element> partElements = e.elements("part");
            List<Part> parts = new ArrayList<>();
            for (Element partElement:partElements) {
                Part part = new Part();
                String name = partElement.attributeValue("name");
                String element = splitS(partElement.attributeValue("element"));
                String type = splitS(partElement.attributeValue("type"));
                //判断array类型
                part.setType((type));
                if (type == null && element.startsWith("ArrayOf")){
                    type = element.substring(7).toLowerCase();
                    part.setType(element);
                }
                part.setName(name);
                part.setElement(element);
                part.setBasic(isBasic(type));
                parts.add(part);
            }
            mess.setParts(parts);
            messes.put(mess.getName(),mess);
        }
        return messes;
    }

    //将complexType元素转化为ComplexType对象
    public static Map<String,ComplexType> getComplexTypes(List<Element> complexTypes) {
        Map<String,ComplexType> map = new HashMap<>();
        for (Element complexType : complexTypes) {
            ComplexType ct = initComplexType(complexType);
            map.put(ct.getName(),ct);
        }
        return map;
    }
    public static Map<String, Ele> getEles(List<Element> elements){
        Map<String,Ele> eles = new HashMap<>();
        for (Element e:elements) {
            Ele ele = initEle(e);
            List<Element> complexTypes = e.elements("complexType");
            if (complexTypes.size() >0) {
                List<ComplexType> cts = new ArrayList<>();
                for (Element complexType : complexTypes) {
                    ComplexType ct = initComplexType(complexType);
                    cts.add(ct);
                }
                ele.setComplexTypes(cts);
            }
            eles.put(ele.getName(),ele);
        }
        return eles;
    }

    public static List<Element> getChilds(List<Element> elements){
        List<Element> childs = new ArrayList<>();
        for (Element e:elements) {
            List<Element> child = e.elements();
            childs.addAll(child);
        }
        return childs;
    }
    private static Ele initEle(Element e){
        String type = splitS(e.attributeValue("type"));
        Ele ele = new Ele();
        ele.setName(e.attributeValue("name"));
        ele.setType(type);
        String ref = splitS(e.attributeValue("ref"));
        if (!"schema".equals(ref)){
            ele.setRef(ref);
        }
        ele.setBasic(isBasic(type));
        return ele;
    }

    //组装ComplexType
    private static ComplexType initComplexType(Element complexType){
        ComplexType ct = new ComplexType();
        String name = complexType.attributeValue("name");
        if (name == null) {
            name = complexType.getParent().attributeValue("name");
        }
        ct.setName(name);
        Element sequence = complexType.element("sequence");
        if (sequence != null) {
            List<Element> elements = sequence.elements("element");
            List<Ele> eles = new ArrayList<>();
            for (Element e : elements) {
                eles.add(initEle(e));
            }
            ct.setEles(eles);
        }
        return ct;
    }

    public static boolean isBasic(String type){
        String value = splitS(type);
        // 如果是ArrayOf开头的 判断ArrayOf后面的作为是否基本类型
        if (value != null && value.startsWith("ArrayOf")){
            value = value.substring(7).toLowerCase();
        }
        return baseType.contains(value);
    }

    /**
     * 获取真正的节点名称
     * @param value
     * @return
     */
    private static String splitS(String value){
        String ty = value;
        if(null!=value){
            String[] t =value.split(":");
            if(t.length>1){
                ty = t[1];
            }
        }
        return ty;
    }

    //将所有的complexType 转换为对应的基本类型
    public static Map<String,List<ParamInfo>> transferComplexToSimple(Map<String,Ele> eles, Map<String,ComplexType> complexTypes){
        Map<String,List<ParamInfo>> result = new HashMap<>();
        for (Map.Entry<String,ComplexType> entry:complexTypes.entrySet()) {
            ComplexType c = entry.getValue();
            List<ParamInfo> list = complexToSimple(c,complexTypes,eles);
            if (list !=null && list.size() != 0){
                result.put(c.getName(),list);
            }else {
                result.put(c.getName(),null);

            }
        }
        return result;
    }

    // 将复杂类型转换为对应的基本类型
    private static List<ParamInfo> complexToSimple(ComplexType complexType,Map<String,ComplexType> complexTypes,Map<String,Ele> eles){
        List<Ele> list = complexType.getEles();
        List<ParamInfo> result = new ArrayList<>();
        if (list != null){
            for (int i = 0; i < list.size(); i++ ){
                Ele ele = list.get(i);
                String ref = ele.getRef();
                String type = ele.getType();
                ParamInfo paramInfo = new ParamInfo();
                if (ref == null){
                    if (isBasic(type)){
                        paramInfo.setParamName(ele.getName());
                        paramInfo.setParamType(type);
                    }else {
                        ComplexType ct = complexTypes.get(type);
                        if (ct != null){
                            paramInfo.setParamName(ele.getName());
                            paramInfo.setParamInfos(complexToSimple(ct,complexTypes,eles));
                        }else {
                            paramInfo.setParamName(complexType.getName());
                        }
                    }
                }else {
                    Ele e = eles.get(ref);
                    String name = e.getName();
                    String ty = e.getType();
                    if (e.getBasic()){
                        paramInfo.setParamName(name);
                        paramInfo.setParamType(ty);
                    }else {
                        ComplexType ct = complexTypes.get(ty);
                        paramInfo.setParamName(name);
                        paramInfo.setParamInfos(complexToSimple(ct,complexTypes,eles));
                    }
                }
                result.add(paramInfo);
            }
        }
        return result;
    }

    /**
     *
     * @param messages
     * @param complexTypes
     * @param complexToSimple
     * @param eles
     * @return
     */
    //将各个message对应的参数类型进行映射封装
    public static Map<String,List<ParamInfo>> transferMessToSimple(Map<String,Mess> messages,Map<String,ComplexType> complexTypes,Map<String,List<ParamInfo>> complexToSimple,Map<String,Ele> eles){
        Map<String,List<ParamInfo>> map = new HashMap<>();
        for (Map.Entry<String,Mess> entry:messages.entrySet()) {
            String name = entry.getKey();
            Mess mess = entry.getValue();
            List<Part> list = mess.getParts();
            List<ParamInfo> partToSimple = partToSimple(list,complexTypes,complexToSimple,eles);
            if (partToSimple.size() != 0){
                map.put(name,partToSimple);
            }else {
                map.put(name,null);
            }
        }
        return map;
    }

    //对part进行封装
    private static List<ParamInfo> partToSimple(List<Part> parts,Map<String,ComplexType> complexTypes,Map<String,List<ParamInfo>> complexToSimple,Map<String,Ele> eles){
        //Map<String,Object> map = new HashMap<>();
        List<ParamInfo> result = new ArrayList<>();
        for (Part part:parts) {
            String name = part.getName();
            if (part.getBasic()){
                //判断是否是Array
                ParamInfo paramInfo = new ParamInfo();
                String type = part.getType();
                if (ty != null && ty.startsWith("ArrayOf")){
                        //没有type属性但是还是basic 则肯定是arrayofBasic
                        paramInfo.setParamName(ty.substring(7));
                        paramInfo.setParamType(ty);
                    }else {
                        paramInfo.setParamName(ele.getName());
                        paramInfo.setParamType(ele.getType());
                    }
                result.add(paramInfo);
            }else {
                String eleName = part.getElement();
                Ele ele = eles.get(eleName);
                String type = ele.getType();
                if (ele.getBasic()){
                    //没有type属性但是还是basic 则肯定是arrayofBasic
                    String ty = part.getType();
                    ParamInfo paramInfo = new ParamInfo();
                    paramInfo.setParamName(ty.substring(7));
                    paramInfo.setParamType(ty);
                    result.add(paramInfo);
                }else {
                    if (type != null){
                        List<ParamInfo> list = complexToSimple.get(type);
                        //map.put(name,o);
                        if (list != null){
                            result.addAll(list);
                        }
                    }else {
                        List<ComplexType> cts = ele.getComplexTypes();
                        List<Ele> list = null;
                        if (cts != null){
                            list = new ArrayList<>();
                            for (ComplexType ct:cts) {
                                List<ParamInfo> infos = complexToSimple(ct,complexTypes,eles);
                                result.addAll(infos);
                            }
                        }
                    }
                }
            }
        }
        return result;

    }


    //对所有的方法进行解析 将入参出参解析成A.B.C类型
    //Map<String,List> pathMap=new HashMap();//记录所有从根节点到叶子结点的路径
    public static void iteratorNode(ParamInfo n,Stack<ParamInfo> pathstack,Map<String,String> map) {
        pathstack.push(n);//入栈
        List<ParamInfo> childlist=n.getParamInfos();
        if(childlist==null)//没有孩子 说明是叶子结点
        {
            List<String> lst=new ArrayList();
            Iterator<ParamInfo> stackIt=pathstack.iterator();
            while(stackIt.hasNext())
            {
                lst.add(stackIt.next().getParamName());

            }
            String path = bulidParamater(lst);//打印路径
            map.put(path,n.getParamType());//保存路径信息
            return;
        }else
        {
            Iterator<ParamInfo> it=childlist.iterator();
            while(it.hasNext())
            {
                ParamInfo child=it.next();
                iteratorNode(child,pathstack,map);//深度优先 进入递归
                pathstack.pop();//回溯时候出栈
            }

        }

    }

    //组织参数格式
    private static String bulidParamater(List<String> lst){
        Iterator<String> it=lst.iterator();
        StringBuilder builder = new StringBuilder();
        while(it.hasNext()) {
            String n= it.next();
            builder.append(n+".");
        }
        String result = builder.toString();
        result = result.substring(0,result.length()-1);
        return result;
    }


    public static void rebuild(List<Operat> methods){
        for (Operat method:methods) {
            //处理入参
            List<ParamInfo> input = method.getInput();
            if (input != null && input.size() !=0){
                List<ParamInfo> list = new ArrayList<>();
                for (ParamInfo info:input) {
                    List<ParamInfo> inputList = rebuild(info);
                    list.addAll(inputList);
                }
                method.setInput(list);
            }
            //处理出参
            List<ParamInfo> output = method.getOutput();
            if (output != null && output.size() != 0){
                List<ParamInfo> list = new ArrayList<>();
                for (ParamInfo info:output) {
                    List<ParamInfo> outputList = rebuild(info);
                    list.addAll(outputList);
                }
                method.setOutput(list);
            }
        }
    }
    private static List<ParamInfo> rebuild(ParamInfo info){
        Stack<ParamInfo> stack=new Stack();
        Map<String,String> pathMap = new HashMap<>();
        WsdlUtil.iteratorNode(info,stack,pathMap);
        List<ParamInfo> result = new ArrayList<>();
        for (Map.Entry<String,String> entry:pathMap.entrySet()) {
            ParamInfo paramInfo = new ParamInfo();
            paramInfo.setParamName(entry.getKey());
            paramInfo.setParamType(entry.getValue());
            result.add(paramInfo);
        }
        return result;
    }

    public static List<Operat> wsdl(String wsdl) throws MalformedURLException, DocumentException, WSDLException {
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
        Map<String,Mess> messes = WsdlUtil.getMessages(messages);
        Map<String,ComplexType> complexTypes = WsdlUtil.getComplexTypes(complexType);
        Map<String,List<ParamInfo>> complexToSimple = WsdlUtil.transferComplexToSimple(eles,complexTypes);
        Map<String,List<ParamInfo>>  map =  WsdlUtil.transferMessToSimple(messes,complexTypes,complexToSimple,eles);
        List<Operat> methods = WsdlUtil.getMethods(wsdl);

        for (Operat o:methods) {
            String inputMess = o.getInputMessName();
            String outputMess = o.getOutputMessName();
            List<ParamInfo> input = map.get(inputMess);
            List<ParamInfo> output = map.get(outputMess);
            o.setInput(input);
            o.setOutput(output);
        }
        WsdlUtil.rebuild(methods);
        return methods;
    }
}
