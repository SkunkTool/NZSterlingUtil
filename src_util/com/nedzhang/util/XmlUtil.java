package com.nedzhang.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
//import com.sun.org.apache.xpath.internal.XPathAPI;

/***
 * 
 * The XmlUtil class is an utility class for xml operation
 * 
 * @author Sterling ATT RTP DEV TEAM
 * 
 */
public final class XmlUtil {

//	private static final Log logger = LogFactory.getLog(XmlUtil.class);

	// Questionable usage
	// public static void copyAttributes(final Element tgtEl, final Element
	// srcEl)
	// throws IllegalArgumentException {
	// if (null == srcEl || null == tgtEl) {
	// throw new IllegalArgumentException(
	// "Source or Target Element cannot be null");
	// }
	// final NamedNodeMap map = srcEl.getAttributes();
	// for (int i = 0; i < map.getLength(); i++) {
	// final Node nd = map.item(i);
	// tgtEl.setAttribute(nd.getNodeName(), nd.getNodeValue());
	// }
	// }

	/***
	 * 
	 * Delegate interface for process text node. It is used in the
	 * processChildTextNodes(Node, ITextNodeProcessor, int) method
	 * 
	 */
	public interface ITextNodeProcessor {
		public void process(Node node, String nodeName, Node textNode,
				String textValue);
	}

	public static void createChildTextNode(final Node parentNode,
			final String childNodeName, final String childNodeValue) {
		if (parentNode == null) {
			throw new IllegalArgumentException("parentNode cannot be null");
		}

		if ((childNodeName == null) || (childNodeName.length() == 0)) {
			throw new IllegalArgumentException(
					"childNodeName cannot be null or empty string");
		}

		final Node nameNode = XmlUtil.getChildNodeByName(parentNode,
				childNodeName, true);

		if (nameNode.hasChildNodes()) {
			XmlUtil.removeAllChilden(nameNode);
		}

		final Text valueNode = parentNode.getOwnerDocument().createTextNode(
				childNodeValue);

		nameNode.appendChild(valueNode);
	}

	public static String escapeXML(final String xml) {
		final StringWriter writer = new StringWriter();
		final char[] value = xml.toCharArray();
		String outval = null;
		int start = 0;
		int len = 0;
		for (int i = 0; i < value.length; i++) {
			switch (value[i]) {
			case '&':
				outval = "&amp;";
				break;
			case '\'':
				outval = "&apos;";
				break;
			case '\"':
				outval = "&quot;";
				break;
			case '<':
				outval = "&lt;";
				break;
			case '>':
				outval = "&gt;";
				break;
			default:
				len++;
				break;
			}

			if (outval != null) {
				if (len > 0) {
					writer.write(value, start, len);
				}
				writer.write(outval);
				start = i + 1;
				len = 0;
				outval = null;
			}
		}
		if (len > 0) {
			writer.write(value, start, len);
		}

		return writer.toString();
	}

	/**
	 * Gets value of an attribute from node
	 * 
	 * @param node
	 *            Node Object
	 * @param attributeName
	 *            Attribute Name
	 * @return Attribute Value
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 */
	public static String getAttribute(final Node node,
			final String attributeName) throws IllegalArgumentException {
		// Validate attribute name
		if (attributeName == null) {
			throw new IllegalArgumentException(
					"Attribute Name cannot be null in XmlUtils.getAttribute method");
		}

		// Validate node
		if (node == null) {
			throw new IllegalArgumentException(
					"Node cannot be null in XmlUtils.getAttribute method for attribute name:"
							+ attributeName);
		}

		final NamedNodeMap attributeList = node.getAttributes();
		if (attributeList != null) {
			final Node attribute = attributeList.getNamedItem(attributeName);
			return attribute == null ? null : ((Attr) attribute).getValue();
		} else {
			return null;
		}

	}

	// getAttr(final Node node, final String name) can do the same thing.
	// public static String getAttrVal(final Document doc, final String name) {
	// return doc.getDocumentElement().getAttribute(name);
	// }

	/**
	 * Gets value of an attribute from node. Returns default value if attribute
	 * not found.
	 * 
	 * @param node
	 *            Node Object
	 * @param attributeName
	 *            Attribute Name
	 * @param defaultValue
	 *            Default value if attribute not found
	 * @return Attribute Value
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 */
	public static String getAttribute(final Node node,
			final String attributeName, final String defaultValue)
			throws IllegalArgumentException {
		// Validate attribute name
		if (attributeName == null) {
			throw new IllegalArgumentException("Attribute Name "
					+ " cannot be null in XmlUtils.getAttribute method");
		}

		// Validate node
		if (node == null) {
			throw new IllegalArgumentException("Node cannot "
					+ " be null in XmlUtils.getAttribute method for "
					+ "Attribute Name:" + attributeName);
		}

		final NamedNodeMap attributeList = node.getAttributes();
		final Node attribute = attributeList.getNamedItem(attributeName);

		// Validate attribute name
		if (attribute == null) {
			return defaultValue;
		}

		return ((Attr) attribute).getValue();
	}

	// Bad practice
	// public static String getAttrVal(final Document doc, final String el,
	// final String name) {
	// return getAttrVal(doc, el, name, null);
	// }
	//
	// public static String getAttrVal(final Document doc, final String el,
	// final String name, final String def) {
	// return getAttrVal(doc.getDocumentElement(), el, name, def);
	// }
	//
	// public static String getAttrVal(final Element ctxEl, final String el,
	// final String name) {
	// return getAttrVal(ctxEl, el, name, null);
	// }

	// NOT IMPLEMENTED
	// /**
	// * Constructs Document object from XML String. This method uses DOMParser
	// to generate a document from a
	// * string coded as a well-formed XML. This method should be sparingly used
	// to build XMLs as all XMLs
	// * should be attempted to be built using Nodes and elements from scratch.
	// The advantage of this is that
	// * all encoding of special characters is automatically taken care of, and
	// is also the recommended way of
	// * building XML. The parser also validates if a boolean true is passed to
	// the DTD mentioned in the
	// * inputXMLString.
	// *
	// * @param inputXMLString
	// * Input XML String
	// * @param validate
	// * Boolean indicating the parser needs to validate the document against a
	// DTD or not.
	// * @return XML Document object
	// * @throws IllegalArgumentException
	// * if input is invalid
	// */
	// public static Document getDocument(String inputXMLString, boolean
	// validate)
	// throws IllegalArgumentException
	// {
	// // Validate input XML string
	// if (inputXMLString == null)
	// {
	// throw new IllegalArgumentException("Input XML string"
	// + " cannot be null in XmlUtils.getDocument method");
	// }
	//
	// // Create document builder factory instance
	// DocumentBuilderFactory documentBuilderFactory =
	// DocumentBuilderFactory.newInstance();
	//
	// // if validation set to true then enable validation
	// if (validate)
	// {
	// documentBuilderFactory.setValidating(true);
	// }
	//
	// // Create document builder
	// DocumentBuilder documentBuilder =
	// documentBuilderFactory.newDocumentBuilder();
	//
	// if (validate)
	// {
	// documentBuilder.setErrorHandler(new YantraGenericDTDErrorHandler());
	// }
	//
	// // SCR# 1139
	// // Convert input XML as a document object
	// Document resultDocument = null;
	//
	// try
	// {
	// resultDocument = documentBuilder.parse(new InputSource(new
	// BufferedReader(new InputStreamReader(
	// new ByteArrayInputStream(inputXMLString.getBytes())))));
	// }
	// catch (FileNotFoundException fne)
	// {
	// if (validate)
	// {
	// throw new FileNotFoundException("DTD declared for XML not found!");
	// }
	// }
	//
	// // Return result document
	// return resultDocument;
	// }

	// public static String getAttrVal(final Element ctxEl, final String el,
	// final String name, final String def) {
	// final Element elm = XmlUtil.getElementByName(ctxEl, el);
	// if (null != elm) {
	// return elm.getAttribute(name);
	// }
	// return def;
	// }

	/**
	 * 
	 * 
	 * @param node
	 *            Node object
	 * @param childName
	 *            Name of child node looking for
	 * @param createNode
	 *            If true, appends a child node with given name when not found
	 * @return Node Child Node
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 */
	public static Node getChildNodeByName(final Node node,
			final String childName, final boolean createNode)
			throws IllegalArgumentException {
		// Validate node
		if (node == null) {
			throw new IllegalArgumentException("Node cannot "
					+ "be null in XmlUtils.getChildNodebyName method");
		}

		// Validate child name
		if (childName == null) {
			throw new IllegalArgumentException("Child name cannot"
					+ " be null in XmlUtils.getChildNodebyName method");
		}

		final NodeList childList = node.getChildNodes();
		if (childList != null) {
			for (int childIndex = 0; childIndex < childList.getLength(); childIndex++) {
				if (childName.equals(childList.item(childIndex).getNodeName())) {
					return childList.item(childIndex);
				}
			}
		}

		if (createNode) {
			final Node newNode = node.getOwnerDocument().createElement(
					childName);
			node.appendChild(newNode);
			return newNode;
		} else {
			return null;
		}
	}

	/**
	 * Creates a Document object
	 * 
	 * @return empty Document object
	 * @throws ParserConfigurationException
	 */
	public static Document getDocument() throws ParserConfigurationException {

		// Create and return document object
		return getDocumentBuilder().newDocument();
	}

	/**
	 * Create a new document object with input element as the root.
	 * 
	 * @param inputElement
	 *            Input Element object
	 * @param deep
	 *            Include child nodes of this element true/false
	 * @return XML Document object
	 * @throws IllegalArgumentException
	 *             if input is invalid
	 * @throws ParserConfigurationException
	 */
	public static Document getDocument(final Element inputElement,
			final boolean deep) throws IllegalArgumentException,
			ParserConfigurationException {
		// Validate input element
		if (inputElement == null) {
			throw new IllegalArgumentException(
					"Input element cannot be null in "
							+ "XmlUtils.getDocument method");
		}

		// Create a new document
		final Document outputDocument = getDocument();

		// Import data from input element and
		// set as root element for output document
		outputDocument.appendChild(outputDocument
				.importNode(inputElement, deep));

		// return output document
		return outputDocument;
	}

	// Bad practice
	// /**
	// * Goes through each sibling of node, till the name of the node matches
	// the siblingName passed as input,
	// *
	// * @param node
	// * Node object
	// * @param siblingName
	// * Name of sibling node looking for
	// * @param createNode
	// * If true, adds a sibling with given sibling name when not found
	// * @return Node Sibling Node
	// * @throws IllegalArgumentException
	// * for Invalid input
	// * @throws Exception
	// * if node not found and createNode is false; and all others
	// */
	// public static Node getNextSiblingByName(Node node, String siblingName,
	// boolean createNode)
	// {
	// // Validate node
	// if (node == null)
	// {
	// throw new IllegalArgumentException("Node cannot be "
	// + " null in XmlUtils.getChildNodebyName method");
	// }
	//
	// // Validate sibling name
	// if (siblingName == null)
	// {
	// throw new IllegalArgumentException("Sibling name cannot"
	// + " be null in XmlUtils.getChildNodebyName method");
	// }
	//
	// Node siblingNode = node.getNextSibling();
	// if (siblingNode == null)
	// {
	// if (createNode)
	// {
	// Node newNode = node.getOwnerDocument().createElement(siblingName);
	// node.getParentNode().appendChild(newNode);
	// return newNode;
	// }
	// throw new Exception("Node " + siblingName + "not  "
	// + "found in XmlUtils.getNextSiblingbyName method");
	//
	// }
	// if (siblingNode.getNodeName().equals(siblingName))
	// {
	// return siblingNode;
	// }
	// return getNextSiblingByName(siblingNode, siblingName, createNode);
	// }

	/**
	 * Loads and constructs XML Document object from an input file
	 * 
	 * @param xmlFile
	 *            File object for the xml file
	 * @return XML Document object
	 * @throws IllegalArgumentException
	 *             if input file is null
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Document getDocument(final File xmlFile)
			throws IllegalArgumentException, ParserConfigurationException,
			SAXException, IOException {
		// Validate input file name
		// if (null == inputFileName || 0 == inputFileName.trim().length()) {
		// throw new IllegalArgumentException(
		// "Input Filename cannot be null or empty "
		// + " in XmlUtils.getXMLfromfile method");
		// }

		if (xmlFile == null) {

			throw new IllegalArgumentException(
					"Input xmlFile cannot be null in XmlUtils.getXMLfromfile method");

		}

		// Create document builder
		final DocumentBuilder documentBuilder = getDocumentBuilder();

		// load XML document from file
		final Document resultDocument = documentBuilder.parse(xmlFile);

		// return result document
		return resultDocument;
	}

	/**
	 * This method converts input stream as an XML document
	 * 
	 * @param inputStream
	 *            Input Stream
	 * @return Content of input stream as an XML Document object
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IllegalArgumentException
	 *             if input is not valid
	 */
	public static Document getDocument(final InputStream inputStream)
			throws SAXException, IOException, ParserConfigurationException {

		// Create document builder
		final DocumentBuilder builder = getDocumentBuilder();

		// Parse input stream as document
		final Document document = builder.parse(inputStream);

		// return output document
		return document;
	}

	public static Document getDocument(final Source source)
			throws TransformerException, IllegalArgumentException,
			ParserConfigurationException, SAXException, IOException {
		// TODO: check if we can avoid read the source
		// into string.
		final String text = readSource(source);

		return getDocument(text);
	}

	/**
	 * Constructs Document object from XML String. This method uses DOMParser to
	 * generate a document from a string coded as a well-formed XML. This method
	 * should be sparingly used to build XMLs as all XMLs should be attempted to
	 * be built using Nodes and elements from scratch. The advantage of this is
	 * that all encoding of special characters is automatically taken care of,
	 * and is also the recommended way of building XML.
	 * 
	 * @param inputXMLString
	 *            Input XML String
	 * @return XML Document object
	 * @throws IllegalArgumentException
	 *             if input is invalid
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Document getDocument(final String inputXMLString)
			throws IllegalArgumentException, ParserConfigurationException,
			SAXException, IOException {
		// Validate input XML string
		if (inputXMLString == null) {
			throw new IllegalArgumentException("Input XML string"
					+ " cannot be null in XmlUtils.getDocument method");
		}

		// Create document builder
		final DocumentBuilder documentBuilder = getDocumentBuilder();

		// Convert input XML as a document object
		final Document resultDocument = documentBuilder.parse(new InputSource(
				new BufferedReader(new InputStreamReader(
						new ByteArrayInputStream(inputXMLString.getBytes())))));

		// Return result document
		return resultDocument;
	}

	private static DocumentBuilder getDocumentBuilder()
			throws ParserConfigurationException {
		// TODO: check if we should cache the factory
		// Create a new Document Builder Factory instance
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		// DomSource continue to have issue with default namespace
		// We discontinued usage of DomSource and move to StreamSource
		// As the result, we no longer want and need documentBuilderFactory
		// to be namesapce aware.
		// documentBuilderFactory.setNamespaceAware(true);

		// Create new document builder
		final DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();

		return documentBuilder;
	}

	// Bad practice
	// /**
	// * Direct DOM access method.
	// *
	// * @param el
	// * @param name
	// * @return
	// */
	// public static Element getElementByName(final Node el, final String name)
	// {
	//
	// NodeList nList = null;
	// if (el instanceof Element) {
	// nList = ((Element) el).getElementsByTagName(name);
	// } else if (el instanceof Document) {
	// nList = ((Document) el).getElementsByTagName(name);
	// }
	// if (0 == nList.getLength()) {
	// return null;
	// }
	// return (Element) nList.item(0);
	// }

	// Bad practice
	// /**
	// * Finds all elements with specified tag name and returns node list. Do
	// not
	// * use this method if you have to access parent elements using the result
	// * node.
	// *
	// * @param inputElement
	// * Input Element object
	// * @param inputElementName
	// * Input Element name to search for
	// * @return NodeList List of nodes matching given name
	// * @throws IllegalArgumentException
	// * if input is invalid
	// */
	// public static NodeList getElementsByTagName(final Element inputElement,
	// final String inputElementName) throws IllegalArgumentException {
	// // Document
	// final Document document = null;
	//
	// // Validate child element name
	// if (null == inputElementName || 0 == inputElementName.trim().length()) {
	// throw new IllegalArgumentException(
	// "Input Element name cannot null or empty"
	// + " XmlUtils.getElementsByTagName method");
	// }
	//
	// // Validate input element
	// if (inputElement == null) {
	// throw new IllegalArgumentException(
	// "Input element cannot be null in "
	// + "XmlUtils.getElementsByTagName for "
	// + "input element name:" + inputElementName);
	// }
	//
	// return inputElement.getElementsByTagName(inputElementName);
	//
	// }

	// DON'T UNDERSTAND THE PURPOSE OF THIS METHOD
	// /**
	// * This method imports specified child node for target document and
	// returns
	// * reference to Node
	// *
	// * @param targetDocument
	// * Target document for which the child node needs to be imported
	// * @param inputNode
	// * Source Node whose child node needs to be imported
	// * @param childNodeName
	// * Name of child
	// * @param deep
	// * Include nodes while importing true/false
	// * @return Reference to imported child node
	// */
	// public static Node importChildNode(final Document targetDocument, final
	// Node inputNode,
	// final String childNodeName, final boolean deep) throws
	// IllegalArgumentException {
	// // Validate target document
	// if (targetDocument == null) {
	// throw new IllegalArgumentException("Target document cannot be null"
	// + " in XmlUtils.importChildNode method");
	// }
	//
	// // Validate input node
	// if (inputNode == null) {
	// throw new IllegalArgumentException("Input node cannot be null"
	// + " in XmlUtils.importChildNode method");
	// }
	//
	// // Validate child name
	// if (null == childNodeName || 0 == childNodeName.trim().length()) {
	// throw new IllegalArgumentException("Child node namecannot be null"
	// + " in XmlUtils.importChildNode method");
	// }
	//
	// // Get child node
	// final Node childNode = getChildNodeByName(inputNode, childNodeName,
	// false);
	//
	// // Import child
	// final Node resultNode = targetDocument.importNode(childNode, deep);
	//
	// // Return reference to imported child
	// return resultNode;
	// }

	// DO NOT USE THE USAGE OF THE METHOD
	// /**
	// * Creates a copy of element with attributes(not deep) with given name
	// *
	// * @param elementName
	// * Element Name
	// * @param newElementName
	// * New name for element
	// */
	// public static Element prepareElement(final Element elementName,
	// final String newElementName) throws IllegalArgumentException {
	// // Validate element name
	// if (elementName == null) {
	// throw new IllegalArgumentException("Input node cannot be null"
	// + " in XmlUtils.prepareElement method");
	// }
	//
	// // Create an element with given name
	// final Element resultElement =
	// elementName.getOwnerDocument().createElement(
	// newElementName);
	//
	// // Import all attributes to new element
	// setManyAttributes(resultElement, getManyAttributes(elementName));
	//
	// // Return result element
	// return resultElement;
	// }

	// Duplicating the getXmlString(Node) method
	// /**
	// * Constructs XML String from given element object
	// *
	// * @param inputElement
	// * Input element
	// * @return XML String
	// * @throws IllegalArgumentException
	// * for Invalid input
	// * @throws IOException
	// */
	// public static String getElementString(final Element inputElement)
	// throws IllegalArgumentException, IOException {
	// // Validate input element
	// if (inputElement == null) {
	// throw new IllegalArgumentException(
	// "Input element cannot be null in "
	// + "XmlUtils.getElementString method");
	// }
	//
	// // Convert document as element string
	// String xmlString = getXmlString(inputElement);
	//
	// // Remove Processing Instruction from xml string if exists
	// xmlString = removeProcessingInstruction(xmlString);
	//
	// // Return result XML string
	// return xmlString;
	// }

	// Questionable practice
	// /**
	// * Loads all attributes in given element to a Map
	// *
	// * @param element
	// * Element
	// * @return Map object containing all attributes of given element
	// * @throws IllegalArgumentException
	// * for Invalid input
	// */
	// public static Map getManyAttributes(final Element element)
	// throws IllegalArgumentException {
	// // Validate element
	// if (element == null) {
	// throw new IllegalArgumentException("Element cannot "
	// + " be null in XmlUtils.getManyAttributes method");
	// }
	//
	// final NamedNodeMap attributeList = element.getAttributes();
	// final Map map = Collections.synchronizedMap(new HashMap());
	// for (int index = 0; index < attributeList.getLength(); index++) {
	// final Node attributeNode = attributeList.item(index);
	// if (attributeNode == null) {
	// continue;
	// }
	// final Attr attribute = (Attr) attributeNode;
	// map.put(attribute.getName(), attribute.getValue());
	// }
	// return map;
	// }

	// QUESTIONABLE USAGE
	// public static String getEscapedXMLString(final Document document)
	// throws IOException {
	// return escapeXML(getXmlString(document));
	// }

	/**
	 * This method returns node value for given child. If there is no text
	 * available for given node, then this method returns null
	 * 
	 * @return Node value of input node
	 * @throws IllegalArgumentException
	 *             if input is invalid
	 */
	public static String getNodeValue(final Node inputNode) {
		// Child count
		int childCount = 0;

		if (inputNode == null) {
			return null;
		}

		// Return null if child not found
		final NodeList childList = inputNode.getChildNodes();
		if ((childList == null) || (childList.getLength() < 1)) {
			return null;
		}

		// Get child count
		childCount = childList.getLength();

		// For each child
		for (int childIndex = 0; childIndex < childCount; childIndex++) {
			// Get each child
			final Node childNode = childList.item(childIndex);

			// Check if text node
			if (childNode.getNodeType() == Node.TEXT_NODE) {
				// Return node value
				return childNode.getNodeValue();
			}
		}

		// If no text node found return null
		return null;
	}

//	public static String getNodeValue(final Node contextNode, final String path) {
//		return getNodeValue(contextNode, path, null);
//	}
//
//	private static String getNodeValue(final Node contextNode,
//			final String path, final Node namespaceNode) {
//		if (contextNode == null) {
//			return null;
//		}
//
//		if ((path == null) || (path.length() == 0)) {
//			return getNodeValue(contextNode);
//		} else {
//			Node targetNode;
//			try {
//				if (namespaceNode == null) {
//					targetNode = XPathAPI.selectSingleNode(contextNode, path);
//				} else {
//					targetNode = XPathAPI.selectSingleNode(contextNode, path,
//							namespaceNode);
//				}
//
//			} catch (final TransformerException e) {
//				e.printStackTrace();
//				return null;
//			}
//
//			return getNodeValue(targetNode);
//		}
//	}

	// Duplicating getXmlString(Node) method
	// /**
	// * Constructs XML String from given document object. This method returns
	// * null if document is empty
	// *
	// * @param inputDocument
	// * Input XML Document
	// * @return XML String. Returns null if input document does not have any
	// root
	// * element
	// */
	// public static String getXmlString(final Document inputDocument) {
	// return getXmlString((Node) inputDocument);
	// }

	// /**
	// * Validates a given XML string to conform to a given DTD
	// *
	// * @param inputXMLString
	// * The XML string to be validated
	// * @return True if the XML is valid.
	// * @throws IllegalArgumentException
	// * @throws Exception
	// */
	// public static boolean validate(String inputXMLString) throws
	// IllegalArgumentException, Exception
	// {
	//
	// boolean returnVal = false;
	//
	// // Validate input XML string
	// if (inputXMLString == null)
	// {
	// throw new IllegalArgumentException("Input XML string"
	// + " cannot be null in XmlUtils.getDocument method");
	// }
	//
	// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	// dbf.setValidating(true);
	// DocumentBuilder builder = dbf.newDocumentBuilder();
	// builder.setErrorHandler(new YantraGenericDTDErrorHandler());
	//
	// try
	// {
	//
	// InputSource src = new InputSource(new BufferedReader(new
	// InputStreamReader(
	// new ByteArrayInputStream(inputXMLString.getBytes()))));
	// Document doc = builder.parse(src);
	//
	// returnVal = true;
	//
	// }
	// catch (FileNotFoundException fne)
	// {
	// throw new
	// FileNotFoundException("DTD declared for the XML cannot be found!");
	// }
	// catch (SAXException se)
	// {
	// se.printStackTrace();
	// }
	// return returnVal;
	//
	// }

	/***
	 * Constructs XML String from given Node object. This method returns null if
	 * Node is empty
	 * 
	 * @param node
	 *            Input XML node
	 * 
	 * @return XML string of the node
	 */
	public static String getXmlString(final Node node) {
		return getXmlString(node, false);
	}

	/***
	 * Constructs XML String from given Node object. This method returns null if
	 * Node is empty
	 * 
	 * @param node
	 *            Input XML node
	 * @param removeProcessingInstruction
	 *            if true, this method will return a xml string without the
	 *            &lt;?xml version="1.0" encoding="UTF-8"?> header
	 * 
	 * @return XML string of the node
	 */
	public static String getXmlString(final Node node,
			final boolean removeProcessingInstruction) {
		if (node == null) {
			return null;
		}

		String xmlString;

		try {
			if (node instanceof Document) {
				xmlString = prettyPrint((Document) node);
			} else {
				final TransformerFactory xformFactory = TransformerFactory
						.newInstance();

				final Transformer idTransform = xformFactory.newTransformer();
				final Source input = new DOMSource(node);
				final OutputStream byteStream = new ByteArrayOutputStream();
				final Result output = new StreamResult(byteStream);
				idTransform.transform(input, output);
				byteStream.flush();

				xmlString = byteStream.toString();
			}
		} catch (final TransformerException e) {
			// We shouldn't have transformer exception.
//			if (logger.isErrorEnabled()) {
//				logger.error("Failed while getting string from a XML node.", e);
//			}
			return null;
		} catch (final IOException e) {
			// We shouldn't have IOException
//			if (logger.isErrorEnabled()) {
//				logger.error("Failed while getting string from a XML node.", e);
//			}
			return null;
		}

		if (xmlString == null) {
			// Return null if the xml string is null
			return null;
		} else if (removeProcessingInstruction) {
			// Return xml string without <?xml version="1.0" encoding="UTF-8"?>
			// header if removeProcessingInstruction is true
			return removeProcessingInstruction(xmlString);
		} else {
			return xmlString;
		}
	}

	// QUESTIONABLE USAGE
	// /**
	// * Determines if a child exists with given name
	// *
	// * @throws IllegalArgumentException
	// * if input is invalid
	// */
	// public static boolean hasChildNode(final Node node,
	// final String inputChildNodeName) throws IllegalArgumentException {
	// // Validate node
	// if (node == null) {
	// throw new IllegalArgumentException(
	// "Node cannot be null in XmlUtils.hasChildNode method");
	// }
	//
	// // Validate Child node name
	// if (inputChildNodeName == null) {
	// throw new IllegalArgumentException(
	// "Input Child Node name cannot be null in XmlUtils.hasChildNode method");
	// }
	//
	// final NodeList nodeList = node.getChildNodes();
	// if ((nodeList == null) || (nodeList.getLength() < 1)) {
	// return false;
	// }
	//
	// // Compare each child name and return true if match found
	// for (int index = 0; index < nodeList.getLength(); index++) {
	// final Node childNode = nodeList.item(index);
	// final String childNodeName = childNode.getNodeName();
	// if (childNodeName.equals(inputChildNodeName)) {
	// return true;
	// }
	// }
	//
	// // If no children found with given name then return false
	// return false;
	// }

//	public static String prettyPrint(final Document doc) throws IOException {
//
//		if (doc == null) {
//			return null;
//		}
//
//		final OutputFormat format = new OutputFormat(doc);
//		format.setLineWidth(65);
//		format.setIndenting(true);
//		format.setIndent(2);
//		final Writer outputWriter = new StringWriter();
//		final XMLSerializer serializer = new XMLSerializer(outputWriter, format);
//		serializer.serialize(doc);
//
//		return outputWriter.toString();
//	}
	
	public static String prettyPrint(final Document doc) throws TransformerException {
		
		TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    StreamResult result = new StreamResult(new StringWriter());
	    DOMSource source = new DOMSource(doc);
	    transformer.transform(source, result);
	    String xmlString = result.getWriter().toString();
	    
	    return xmlString;
	    
	}

	// Doc.importNode is sufficient
	// public static void importNode(final Node targetNode, final Node srcNode,
	// final boolean deep) throws Exception {
	// if (null == srcNode) {
	// throw new IllegalArgumentException("Source Node is null");
	// }
	// if (null == targetNode) {
	// throw new IllegalArgumentException("Target Node is null");
	// }
	// if (!(targetNode instanceof Element)) {
	// throw new IllegalArgumentException("Target Node is not an Element");
	// }
	//
	// final Document targetDoc = targetNode.getOwnerDocument();
	// final Node newNd = targetDoc.importNode(srcNode, deep);
	//
	// ((Element) targetNode).appendChild(newNd);
	// }

	public static String prettyPrint(final String inputXmlString) throws TransformerException, IllegalArgumentException, ParserConfigurationException, SAXException, IOException
			  {

		if ((inputXmlString == null) || (inputXmlString.length() == 0)) {
			return null;
		}

		final Document doc = XmlUtil.getDocument(inputXmlString);

		return prettyPrint(doc);
	}

	/***
	 * Fast xml formatter without loading xml into a document or using a 
	 * serializer. 
	 * 
	 * @param content
	 * @param lineBreaker
	 * @param nodeSpacer
	 * @return
	 */
	public static String prettyPrintNoDoc(String content, String lineBreaker,
			String nodeSpacer) {

		if (content == null || content.length() == 0) {
			return null;
		} else {
			StringBuilder prettyPrintBuilder = new StringBuilder(
					content.length() + 200);

			boolean inQuote = false;
			boolean inData = false;
			int nodeDepth = 0;
			char prevCh = 0;
			char lastMeaningfulChar = 0;

			for (int i = 0; i < content.length(); i++) {

				char ch = content.charAt(i);

				char nextCh = i < content.length() - 1 ? content.charAt(i + 1)
						: 0;

				if (inData) { // We are in <!-- or <!CDATA[[ block, find when to
								// exit it
					if ('>' == ch && prettyPrintBuilder.length() >= 2) {
						String lastTwoChar = prettyPrintBuilder
								.substring(prettyPrintBuilder.length() - 2);
						if ("--".equals(lastTwoChar)
								|| "]]".equals(lastTwoChar)) {
							inData = false;

							lastMeaningfulChar = '>';
						}
					}

					prettyPrintBuilder.append(ch);

				} else if (inQuote) { // in the quote, fine when to exit it
					if ('"' == ch) {
						inQuote = false;
					}

					prettyPrintBuilder.append(ch);

				} else { // Not in quote or data
					if (Character.isWhitespace(ch)) {
						if (!Character.isISOControl(prevCh)
								&& !Character.isWhitespace(prevCh)
								&& '>' != prevCh) {
							prettyPrintBuilder.append(' ');
						}
						ch = 0;
					} else if ('"' == ch) {
						inQuote = true;
					} else if ('<' == ch) { // We are at the start of a node

						if ('?' == nextCh) { // Start declaration node
							// move the nodeDepth to -1;
							nodeDepth = -1;
						} else if ('!' == nextCh) { // Start Data node
							inData = true;
							prettyPrintBuilder.append(lineBreaker);
							appendSpace(prettyPrintBuilder, nodeSpacer,
									nodeDepth + 1);
						} else if (i > 1) { // We are at the start of a regular
											// node or closing a regular node

							if ('>' == lastMeaningfulChar) { // A node just
																// finished
																// above
								prettyPrintBuilder.append(lineBreaker);

								if ('/' == nextCh) {// At closing of an node
													// section </
									appendSpace(prettyPrintBuilder, nodeSpacer,
											nodeDepth);
									nodeDepth--;
								} else { // At starting of a node
									nodeDepth++;
									appendSpace(prettyPrintBuilder, nodeSpacer,
											nodeDepth);
								}
							} else if ('/' == nextCh) { // At closing of an node
														// section </ but there
														// are text before this
														// closing node
								nodeDepth--;
							} //else {
//								prettyPrintBuilder
//										.append("start of an element but last meaningful char is "
//												+ lastMeaningfulChar);
//							}

						}
					} else if ('/' == ch && '>' == nextCh) { // closing a node with />
						nodeDepth--;
					}

					if (!Character.isISOControl(ch) && ch != 0) {
						prettyPrintBuilder.append(ch);

						if (!Character.isWhitespace(ch)) {
							lastMeaningfulChar = ch;
						}
					}
				}

				prevCh = ch;
			}
			return prettyPrintBuilder.toString();
		}

	}
	
	private static void appendSpace(StringBuilder buffer, String spaceString,
			int numOfSpace) {
		for (int i = 0; i < numOfSpace; i++) {
			buffer.append(spaceString);
		}
	}
	public static void processChildTextNodes(final Node node,
			final ITextNodeProcessor processor, final int maxDepth) {
		XmlUtil.processChildTextNodes(node, processor, maxDepth, 0);
	}

	private static void processChildTextNodes(final Node node,
			final ITextNodeProcessor processor, final int maxDepth,
			final int currentDepth) {
		if ((currentDepth < maxDepth) && (node != null)) {
			final NodeList children = node.getChildNodes();
			if ((children != null) && (children.getLength() > 0)) {

				for (int i = 0; i < children.getLength(); i++) {
					final Node child = children.item(i);

					if (child.getNodeType() == Node.TEXT_NODE) {
						final String textValue = child.getNodeValue();
						final String nodeName = getNodeName(node);
						processor.process(node, nodeName, child, textValue);
					} else {
						processChildTextNodes(child, processor, maxDepth,
								currentDepth + 1);
					}

				}
			}
		}
	}

	/***
	 * Get the name of the node without namespace
	 * 
	 * @param node
	 * @return
	 */
	public static String getNodeName(final Node node) {

		if (node == null) {
			return null;
		}

		String nodeName = node.getNodeName();

		// Remove namespace prefix if present
		int indexOfColon;
		if ((nodeName != null) && (nodeName.length() > 0 // Node name is not
															// empty
				) && ((indexOfColon = nodeName.indexOf(':')) >= 0)
				// there is a colon in the nodename
				&& (indexOfColon < (nodeName.length() - 1))
		// colon is not at the end of the name
		) {
			nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
		}

		return nodeName;
	}

	public static String readSource(final Source sourceToRead)
			throws TransformerException {
		// Create transformer
		final TransformerFactory tff = TransformerFactory.newInstance();
		final Transformer tf = tff.newTransformer();

		final Writer resultWriter = new StringWriter();

		final Result result = new StreamResult(resultWriter);

		tf.transform(sourceToRead, result);

		return resultWriter.toString();

	}

	// bad practice
	// public static void setAttrVal(final Document doc, final String el,
	// final String name, final String val) {
	// setAttrVal(doc.getDocumentElement(), el, name, val);
	// }

	// public static void setAttrVal(final Element ctxEl, final String el,
	// final String name, final String val) {
	// final Element elm = getElementByName(ctxEl, el);
	// if (null != elm) {
	// elm.setAttribute(name, val);
	// }
	// }

	/***
	 * Remove all children nodes
	 * 
	 * @param node
	 *            node to remove children from
	 * 
	 */
	public static void removeAllChilden(final Node node) {
		if (node != null) {
			final NodeList childrens = node.getChildNodes();

			for (int i = 0; i < childrens.getLength(); i++) {
				node.removeChild(childrens.item(i));
			}
		}
	}

	/**
	 * Removes processing instruction from input XML String. Requirement is that
	 * input XML string should be a valid XML.
	 * 
	 * @param xmlString
	 *            XML String thay may contain processing instruction
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @return XML String
	 */
	public static String removeProcessingInstruction(String xmlString)
			throws IllegalArgumentException {
		// Validate input XML string
		if (xmlString == null) {
			throw new IllegalArgumentException(
					"Input XML string cannot be null in "
							+ "XmlUtils.removeProcessingInstruction method");
		}

		// Is input contains processing instruction
		if ((xmlString.toLowerCase().trim().startsWith("<?xml"))) {
			// Get the ending index of processing instruction
			final int processInstructionEndIndex = xmlString.indexOf("?>");

			// If processing instruction ending found,
			if (processInstructionEndIndex != -1) {
				// Remove processing instruction
				xmlString = xmlString.substring(processInstructionEndIndex + 2);
			}
		}

		// Return XML string after update
		return xmlString;
	}

	/**
	 * Renames the attribute with the new name
	 * 
	 * @param elementObject
	 *            the element object
	 * @param oldName
	 *            Old name of the attribute
	 * @param newName
	 *            New name of the attribute
	 */
	public static void renameAttribute(final Element elementObject,
			final String oldName, final String newName)
			throws IllegalArgumentException {
		// Validate element Object
		if (elementObject == null) {
			throw new IllegalArgumentException("Element object cannot be null"
					+ " in XmlUtils.renameAttribute method");
		}

		// Validate old name
		if (oldName == null) {
			throw new IllegalArgumentException("Old name cannot be null"
					+ " in XmlUtils.renameAttribute method");
		}

		// Validate new name
		if (newName == null) {
			throw new IllegalArgumentException("New name cannot be null"
					+ " in XmlUtils.renameAttribute method");
		}

		// the attribute value
		String attributeValue = null;

		// get the value of the attribute
		attributeValue = getAttribute(elementObject, oldName, null);

		// remove the old attribute
		elementObject.removeAttribute(oldName);

		// if the attribute value is not null then set the attribute
		if (attributeValue != null) {
			elementObject.setAttribute(newName, attributeValue);
		}
	}

	// BAD USAGE
	// public static void setAttrVal(final Document doc, final String name,
	// final String val) {
	// doc.getDocumentElement().setAttribute(name, val);
	// }

	// WHAT THE HECK IS THIS? Why do we need this?
	// /**
	// * Calls setAttribute for all enumerations in the map. This way multiple
	// * attributes can be set in one go.
	// *
	// * @param element
	// * Element
	// * @param attributeMap
	// * Map Set of attributes to be set
	// * @return Element object after attribute setting
	// * @throws IllegalArgumentException
	// * for Invalid input
	// */
	// public static Element setManyAttributes(final Element element,
	// final Map<String, String> attributeMap)
	// throws IllegalArgumentException {
	// // Validate element
	// if (element == null) {
	// throw new IllegalArgumentException("Element cannot "
	// + " be null in XmlUtils.setManyAttributes method");
	// }
	//
	// // Validate attribute map
	// if (attributeMap == null) {
	// throw new IllegalArgumentException("Attribute map "
	// + "cannot be null in XmlUtils.setManyAttributes method");
	// }
	//
	// final Object[] keyList = attributeMap.keySet().toArray();
	// for (int index = 0; index < keyList.length; index++) {
	// final String attributeName = keyList[index].toString();
	//
	// final String attributeValue = attributeMap.get(keyList[index]);
	//
	// element.setAttribute(attributeName, attributeValue);
	// }
	// return element;
	// }

	/**
	 * This method sets value to given node
	 * 
	 * @param inputNode
	 *            Node to which value needs to be set
	 * @param nodeValue
	 *            Value to set
	 * @throws IllegalArgumentException
	 *             if input is invalid
	 */
	public static void setNodeTextValue(final Node inputNode,
			final String nodeValue) throws IllegalArgumentException {
		// Child list
		NodeList childList = null;

		// Validate input stream
		if (inputNode == null) {
			throw new IllegalArgumentException(
					"Input Node cannot be null in XmlUtils.setNodeValue");
		}

		// Get child list
		childList = inputNode.getChildNodes();

		// If child nodes found
		if ((childList != null) && (childList.getLength() > 0)) {
			// Get child count
			final int childCount = childList.getLength();

			// For each child
			for (int childIndex = 0; childIndex < childCount; childIndex++) {
				final Node childNode = childList.item(childIndex);
				// Check if text node
				if ((childNode != null)
						&& (childNode.getNodeType() == Node.TEXT_NODE)) {
					// Set value to text node
					childNode.setNodeValue(nodeValue);
					break;
				}
			}
		} else {
			// Create text node and set node value
			inputNode.appendChild(inputNode.getOwnerDocument().createTextNode(
					nodeValue));
		}
	}

	/**
	 * Writes content of XML Document to a file
	 * 
	 * @param inputDocument
	 *            XML Document Object
	 * @param targetFileName
	 *            Target filename with path
	 * @throws IllegalArgumentException
	 *             if input is not valid
	 * @throws IOException
	 */
	public static void writeXmlToFile(final Document inputDocument,
			final String targetFileName) throws IllegalArgumentException,
			IOException {
		// Validate target file name
		if ((null == targetFileName) || (0 == targetFileName.length())) {
			throw new IllegalArgumentException(
					"Target filename cannot be null or empty in XmlUtils.writeXMLtofile method");
		}

		// Validate input document
		if (inputDocument == null) {
			throw new IllegalArgumentException(
					"Input document cannot be null in XmlUtils.writeXMLtofile method");
		}

		// Convert document as a string
		final String xmlString = getXmlString(inputDocument);

		// Create output file
		final DataOutputStream dataOutputStream = new DataOutputStream(
				new FileOutputStream(targetFileName));

		// Write XML string to file
		dataOutputStream.writeBytes(xmlString);

		// Close file
		dataOutputStream.close();
	}

	private XmlUtil() {

	}

	// Bad usage
	// /**
	// * Removes the passed Node name from the input document. If no name is
	// * passed, it removes all the nodes.
	// *
	// * @param node
	// * Node from which we have to remove the child nodes
	// * @param nodeType
	// * nodeType e.g. Element Node, Comment Node or Text Node
	// * @param name
	// * Name of the Child node to be removed
	// */
	// public static void removeAll(final Node node, final short nodeType,
	// final String name) {
	// if (node.getNodeType() == nodeType
	// && (name == null || node.getNodeName().equals(name))) {
	// node.getParentNode().removeChild(node);
	// } else {
	// // Visit the children
	// final NodeList list = node.getChildNodes();
	// for (int i = 0; i < list.getLength(); i++) {
	// removeAll(list.item(i), nodeType, name);
	// }
	// }
	//
	// }

}