package com.wynk.utils;

import com.wynk.common.*;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class XMLUtils {

    private static final Logger logger         = LoggerFactory.getLogger(XMLUtils.class.getCanonicalName());
    
    public static List<Element> getChildren(Document document, String elementName) {
        NodeList itemList = document.getElementsByTagName(elementName);
        if(itemList == null || itemList.getLength() <= 0)
            return null;
        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < itemList.getLength(); i++)
            elements.add((Element) itemList.item(i));
        return elements;
    }

    public static Node getNode(Element element, String nodeName) {
        NodeList itemList = element.getElementsByTagName(nodeName);
        if (itemList == null || itemList.getLength() <= 0) {
            return null;
        }
        return itemList.item(0);
    }
    
    public static Element getElement(Document document, String elementName) {
        List<Element> elements = getChildren(document, elementName);
        if (elements == null || elements.size() <= 0)
            return null;
        return elements.get(0);
    }
    
    public static List<Node> getChildren(Element itemElem, String nodeName) {
        NodeList elementsByTagName = itemElem.getElementsByTagName(nodeName);
        if (elementsByTagName == null || elementsByTagName.getLength() == 0) {
            return null;
        }
        Node node = elementsByTagName.item(0);
        NodeList childNodes = node.getChildNodes();
        
        List<Node> children = null;
        if (childNodes != null && childNodes.getLength() > 0) {
            children = new ArrayList<>();
            for (int i = 0; i < childNodes.getLength(); i++)
                children.add(childNodes.item(i));
        }
        return children;
    }
    
    public static String getValue(Element itemElem, String nodeName) {
        List<Node> children = getChildren(itemElem, nodeName);
        if (children != null && !children.isEmpty()) {
            for (Node item : children) {
                if (item.getNodeType() == Node.TEXT_NODE || item.getNodeType() == Node.CDATA_SECTION_NODE)
                    return item.getNodeValue() != null ? item.getNodeValue().trim() : null;
            }
        }
        return null;
    }
    
    /**
     * Returns the attribute value for attribute 'attributeName' for the first node named 'nodeName' in the 'itemElem'
     */
    public static String getAttributeValue(Element itemElem, String nodeName, String attributeName) {
        NodeList elementsByTagName = itemElem.getElementsByTagName(nodeName);
        if (elementsByTagName == null || elementsByTagName.getLength() == 0) {
            return null;
        }
        return getAttributeValue(elementsByTagName.item(0), attributeName);
    }
    
    /**
     * Returns the attribute value for attribute 'attributeName' in the node
     */
    public static String getAttributeValue(Node node, String attributeName) {
        if (node == null)
            return null;
        if (StringUtils.isBlank(attributeName))
            return null;
        NamedNodeMap map = node.getAttributes();
        if (map == null)
            return null;
        Node attributeNode = map.getNamedItem(attributeName);
        if (attributeNode == null)
            return null;
        return attributeNode.getNodeValue();
    }

    public static String getNodeValue(Node node) {
        NodeList childNodes = node.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                if (item.getNodeType() == Node.TEXT_NODE || item.getNodeType() == Node.CDATA_SECTION_NODE) {
                    return item.getNodeValue();
                }
            }
        }
        return null;
    }

    public static String stripInvalidXMLCharacters(String s) {
        StringBuilder sb = new StringBuilder();

        if(s == null || s.equals(""))
            return "";
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if((c == 0x9) || (c == 0xA) || (c == 0xD) || ((c >= 0x20) && (c <= 0xD7FF)) || ((c >= 0xE000) && (c <= 0xFFFD)) || ((c >= 0x10000) && (c <= 0x10FFFF)))
                sb.append(c);
        }
        return sb.toString();
    }
    
    public static Document parseIgnoreInvalidChar(String xml)
        throws Exception
    {
        String validXML = stripInvalidXMLCharacters(xml);
        return parse(new ByteArrayInputStream(validXML.getBytes("UTF-8")));
    }

    public static Document parseWithException(String xml)
        throws Exception
    {
        return parseIgnoreInvalidChar(xml);
    }

    public static Document parse(String xml) {
        try {
            //return parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            return parseIgnoreInvalidChar(xml);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Document parse(InputStream in) throws Exception {
        if (null == in) {
            return null;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);

            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    return new InputSource(new ByteArrayInputStream(new byte[0]));
                }
            });
            Document document = documentBuilder.parse(in, "UTF-8");
            return document;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public static void addFieldToDocElem(Document doc, Element rootElem, String name, String value, boolean isCDATA) {
        if (value == null) {
            return;
        }
        Element elem = doc.createElement("field");
        elem.setAttribute("name", name);
        if (isCDATA) {
            CDATASection cdataSection = doc.createCDATASection(value);
            elem.appendChild(cdataSection);
        }
        else {
            Text textNode = doc.createTextNode(value);
            elem.appendChild(textNode);
        }

        rootElem.appendChild(elem);
    }
    
    public static void updateFieldToDocElem(Document doc, Element rootElem, String name, String value, boolean isCDATA) {
        if (value == null) {
            return;
        }
        Element elem = doc.createElement("field");
        elem.setAttribute("name", name);
        elem.setAttribute("update", "set");
        if (isCDATA) {
            CDATASection cdataSection = doc.createCDATASection(value);
            elem.appendChild(cdataSection);
        }
        else {
            Text textNode = doc.createTextNode(value);
            elem.appendChild(textNode);
        }

        rootElem.appendChild(elem);
    }

    public static void convertJsonToXML(JSONObject obj, Document doc, Node parentNode) {
        if (obj == null) {
            return;
        }
        Set entrySet = obj.entrySet();
        for (Object en : entrySet) {
            if (en instanceof Entry) {
                Entry entry = (Entry) en;
                String key = (String) entry.getKey();
                Element elem = doc.createElement(key);
                Object object = entry.getValue();
                if (object instanceof JSONObject) {
                    JSONObject childJsonObj = (JSONObject) object;
                    convertJsonToXML(childJsonObj, doc, elem);
                }
                else if (object instanceof JSONArray) {
                    JSONArray arr = (JSONArray) object;
                    for (int i = 0; i < arr.size(); i++) {
                        JSONObject childJsonObj = (JSONObject) arr.get(i);
                        Element arrElem = doc.createElement(key);
                        convertJsonToXML(childJsonObj, doc, arrElem);
                        parentNode.appendChild(arrElem);
                    }
                }
                else {
                    String text = object == null ? "" : object.toString();
                    Text textNode = doc.createTextNode(text);
                    elem.appendChild(textNode);
                }
                parentNode.appendChild(elem);
            }
        }
    }

    public static String serialize(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(node), new StreamResult(sw));
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return sw.toString();
    }

    public static JSONObject convertNodeToJson(Node elem) {
        if (elem == null) {
            return null;
        }
        String name = elem.getNodeName();
        JSONObject jsonObject = new JSONObject();
        NodeList childNodes = elem.getChildNodes();
        if (childNodes != null) {
            JSONArray arr = new JSONArray();
            boolean isTextNode = false;
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE) {
                    jsonObject.put(name, node.getNodeValue());
                    isTextNode = true;
                }
                else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    JSONObject obj = convertNodeToJson(node);
                    NamedNodeMap attributes = node.getAttributes();
                    if (attributes != null) {
                        for (int j = 0; j < attributes.getLength(); j++) {
                            Node attribute = attributes.item(j);
                            obj.put(attribute.getNodeName(), attribute.getNodeValue());
                        }
                    }
                    arr.add(obj);
                }
            }
            if (!isTextNode || childNodes.getLength() > 1) {
                jsonObject.put(name, arr);
            }
        }
        return jsonObject;
    }
    
    public static JSONObject convertNodeToCollapsedJson(Node elem) {
        if (elem == null) {
            return null;
        }
        String name = elem.getNodeName();
        JSONObject jsonObject = new JSONObject();
        NodeList childNodes = elem.getChildNodes();
        if (childNodes != null) {
            JSONArray arr = new JSONArray();
            boolean isTextNode = false;
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                String nodeName = node.getNodeName();
                if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE) {
                    jsonObject.put(name, node.getNodeValue());
                    isTextNode = true;
                }
                else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    boolean isLeafNode = true;
                    if(node.getAttributes()!=null && node.getAttributes().getLength()>0){
                        isLeafNode=false;
                    }
                    if(isLeafNode){
                        NodeList childNodeList = node.getChildNodes();
                        if(childNodeList!=null){
                            for(int j=0;j<childNodeList.getLength();j++){
                                Node item = childNodeList.item(j);
                                if(item.getNodeType()== Node.ELEMENT_NODE){
                                    isLeafNode=false;
                                    break;
                                }
                                NamedNodeMap attributes = item.getAttributes();
                                if(attributes!=null && attributes.getLength()>0){
                                    isLeafNode=false;
                                    break;
                                }
                            }
                        }
                    }
                    if(isLeafNode){
                        String val = "";
                        NodeList childNodeList = node.getChildNodes();
                        for(int j=0;j<childNodeList.getLength();j++){
                            Node item = childNodeList.item(j);
                            if(item.getNodeType() == Node.TEXT_NODE || item.getNodeType() == Node.CDATA_SECTION_NODE){
                               String nodeValue = item.getNodeValue();
                               if(nodeValue!=null){
                                   val += nodeValue.trim();
                               }
                            }
                        }
                        if(jsonObject.get(nodeName)==null){
                           jsonObject.put(nodeName, val);
                        }else{
                            JSONObject leafObj = new JSONObject();
                            Object object = jsonObject.get(nodeName);
                            if(object instanceof String){
                                String string = (String) object;
                                JSONArray leafArr = new JSONArray();
                                leafArr.add(string);
                                leafArr.add(val);
                                jsonObject.put(nodeName, leafArr);
                            }else if(object instanceof JSONArray){
                                JSONArray leafArr = (JSONArray) object;
                                leafArr.add(val);
                            }
                        }
                    }else{
                        JSONObject obj = convertNodeToJson(node);
                        NamedNodeMap attributes = node.getAttributes();
                        if (attributes != null) {
                            for (int j = 0; j < attributes.getLength(); j++) {
                                Node attribute = attributes.item(j);
                                obj.put(attribute.getNodeName(), attribute.getNodeValue());
                            }
                        }
                        arr.add(obj);
                    }
                    
                }
            }
            if (!isTextNode || childNodes.getLength() > 1) {
                jsonObject.put(name, arr);
            }
        }
        return jsonObject;
    }

    public static String formatDuration(String duration) {
        try {
            int i = Integer.parseInt(duration);
            if (i >= 0) {
                int min = i / 60;
                int sec = i % 60;
                String formattedString = (min < 10 ? "0" + min : "" + min) + ":" + (sec < 10 ? "0" + sec : "" + sec);
                return formattedString;
            }
            else {
                return "";
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return duration;
    }

    public static void main(String[] args) throws Exception {
        String s = "";
        String line = null;
        BufferedReader br = new BufferedReader(new FileReader("config/dev/FeedDetails.xml"));
        while((line=br.readLine())!=null){
            s+=line;
        }
        System.out.println("before: "+s);
       String stripInvalidXMLCharacters = stripInvalidXMLCharacters(s);
       System.out.println("after: "+stripInvalidXMLCharacters);
       Document parse = parse(stripInvalidXMLCharacters);
//       getCategoryDetails(new file)
    }
    
    public static double getDoubleAttributeValue(Element elem, String attributeName){
        if(elem!=null){
            String attribute = elem.getAttribute(attributeName);
            if(attribute!=null){
                try{
                    double d = Double.parseDouble(attribute);
                    return d;
                }catch(Exception e){
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return 0;
    }
    
    public static long getLongAttributeValue(Element elem, String attributeName){
        if(elem!=null){
            String attribute = elem.getAttribute(attributeName);
            if(attribute!=null){
                try{
                    long val = Long.parseLong(attribute);
                    return val;
                }catch(Exception e){
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return 0;
    }
    
    public static int getIntegerAttributeValue(Element elem, String attributeName){
        if(elem!=null){
            String attribute = elem.getAttribute(attributeName);
            if(attribute!=null){
                try{
                    int val = Integer.parseInt(attribute);
                    return val;
                }catch(Exception e){
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return 0;
    }
    
    public static String converXMLWithXSLT(String xmlString, String xsltFilePath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer newTransformer = transformerFactory.newTransformer(new StreamSource(new File(xsltFilePath)));
            StringWriter writer = new StringWriter();
            newTransformer.transform(new StreamSource(new ByteArrayInputStream(xmlString.getBytes("utf-8"))), new StreamResult(writer));
            return writer.toString();
        }
        catch (Exception e) {
            logger.error("Error while converting xml with xslt. XSLT: " + xsltFilePath + ". Xml: " + xmlString, e);
        }
        return null;
    }
    
    public static String getDocumentAsPayloadString(Document doc)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult requestPayload = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, requestPayload);
		return writer.toString();
	}
    
}
