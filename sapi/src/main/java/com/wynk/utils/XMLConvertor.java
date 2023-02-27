package com.wynk.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XMLConvertor<T extends Object> {

    public T convert(Node docNode) {

        try {
            T newObj = (T) getClass().newInstance();
            NodeList childNodes = docNode.getChildNodes();
            if(childNodes != null) {
                for(int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element elem = (Element) node;
                        String fieldName = elem.getAttribute("name");
                        String fieldvalue = getValue(elem);

                        if(elem.getNodeName().equals("str") || elem.getNodeName().equals("string")) {

                            setFieldValue(newObj, fieldName, fieldvalue);

                        }
                        else if(elem.getNodeName().equals("date")) {

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                            try {
                                Date date = sdf.parse(fieldvalue);
                                setFieldValue(newObj, fieldName, date);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            return newObj;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<T> parseXML(InputStream is) {
        ArrayList<T> list = new ArrayList<T>();
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = db.parse(is);
            NodeList resultNodeList = document.getElementsByTagName("result");
            if(resultNodeList != null) {
                for(int i = 0; i < resultNodeList.getLength(); i++) {
                    Element resultElem = (Element) resultNodeList.item(i);
                    NodeList docNodeList = resultElem.getElementsByTagName("doc");// FTS
                    if(docNodeList != null) {
                        for(int j = 0; j < docNodeList.getLength(); j++) {
                            Node docNode = docNodeList.item(j);
                            T newObj = convert(docNode);
                            list.add(newObj);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        Field declaredField;
        try {
            declaredField = obj.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(obj, fieldValue);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getValue(Node node) {
        NodeList childNodes = node.getChildNodes();
        if(childNodes != null) {
            for(int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                if(item.getNodeType() == Node.TEXT_NODE) {
                    return item.getNodeValue();
                }
            }
        }
        return null;
    }
}