/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nedzhang.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author nzhang
 */
public class XPathUtil {
    
    private static final SlidingWindowCacheManager<String, XPathExpression> xpathMap = 
            new SlidingWindowCacheManager<String, XPathExpression>();
    
    private static final XPathFactory xpathFactory = XPathFactory.newInstance();
    
    private XPathUtil() {
        
    }
    
//    public static XPathExpression getXPathExpression(String xpath) throws XPathExpressionException {
//        
//    	String callerClassName = sun.reflect.Reflection.getCallerClass().getName();
//        
//        String key = new StringBuilder(callerClassName).append('&').append(xpath).toString();
//        
//        return getXPathExpression(key, xpath);
//        
//    }
    
    public static XPathExpression getXPathExpression(String key, String xPath) throws XPathExpressionException {
        
    	String pathKey = new StringBuilder(key).append(':').append(xPath).toString();
    	
        if (! xpathMap.containsKey(pathKey)) {
            XPath xpath = xpathFactory.newXPath();
            XPathExpression xpathExpression = xpath.compile(xPath);
            xpathMap.put(pathKey, xpathExpression);
            
//            System.out.println(new StringBuilder("PUT | KEY: ").append(key).append(" | XPath: ").append(xPath));
        }
        
        return xpathMap.get(pathKey);
    }
    
    public static NodeList selectNode(String key, String xPath, Node node) throws XPathExpressionException {
    	
    	XPathExpression expression = getXPathExpression(key, xPath);
    	
    	return expression == null ? null : (NodeList) expression.evaluate(node, XPathConstants.NODESET);
    	
    }
 
    public static Node selectSingleNode(String key, String xPath, Node node) throws XPathExpressionException {
    	
    	XPathExpression expression = getXPathExpression(key, xPath);
    	
    	return expression == null ? null : (Node) expression.evaluate(node, XPathConstants.NODE);
    	
    }
}
