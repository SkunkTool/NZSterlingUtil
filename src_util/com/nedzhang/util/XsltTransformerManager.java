package com.nedzhang.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

/***
 * 
 * This is a xslt template cache class. The XsltManager class utilizes
 * SlidingWindowCacheManager to cache templates and expires idling ones.
 * 
 * @author nzhang
 * 
 */
public final class XsltTransformerManager {

	private static XsltTransformerManager instance = new XsltTransformerManager();

//	private static Log logger = LogFactory.getLog(XsltTransformerManager.class);

	/***
	 * Return a singleton instance
	 * 
	 * @return
	 */
	public static XsltTransformerManager getInstance() {
		return instance;
	}

//	private static String readXsltText(
//			final Class<? extends Object> callerClass, final String cacheKey,
//			final String xsltResourceName) throws IOException {
//
//		InputStream xsltFileStreamToRead = null;
//		try {
//			xsltFileStreamToRead = callerClass
//					.getResourceAsStream(xsltResourceName);
//			// We don't know the charset of the xslt file
//			// so we have to assume it is the default charset
//			final String xslText = CharsetUtil.decodeStream(
//					xsltFileStreamToRead, null);
//			return xslText;
//
//		} finally {
//			if (xsltFileStreamToRead != null) {
//				try {
//					xsltFileStreamToRead.close();
//				} catch (final IOException e) {
//					// Do nothing in this case
//				}
//			}
//		}
//	}

	/***
	 * Perform a xslt transformation.
	 * 
	 * @param transformer
	 *            the transformer
	 * @param xmlSource
	 *            the xml input
	 * @param parameterMap
	 *            the parameters
	 * @return transformation result in string
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	private static String transform(final Transformer transformer,
			final Source xmlSource, final Map<String, String> parameterMap)
			throws TransformerConfigurationException, TransformerException {

		final Writer resultWriter = new StringWriter();

		final Result result = new StreamResult(resultWriter);

		if (parameterMap != null) {
			for (final Entry<String, String> parameterSet : parameterMap
					.entrySet()) {
				transformer.setParameter(parameterSet.getKey(),
						parameterSet.getValue());
			}
		}

		transformer.transform(xmlSource, result);

		final String resultString = resultWriter.toString();

		return resultString;
	}

	/***
	 * Perform a one time xslt transformation. The xsltSource and/or its
	 * transformer will not be cached.
	 * 
	 * @param xsltSource
	 * @param xmlSource
	 * @param parameterMap
	 * @return
	 * @throws TransformerException
	 */
	public static String transformToString(final Source xsltSource,
			final Source xmlSource, final Map<String, String> parameterMap)
			throws TransformerException {
		final TransformerFactory transFact = TransformerFactory.newInstance();

		final Templates template = transFact.newTemplates(xsltSource);

		final Transformer transformer = template.newTransformer();

		return transform(transformer, xmlSource, parameterMap);
	}

	/***
	 * The cache to store the xslt template. The cached object is a
	 * Pair<Templates, String>. The first part is the xslt template, the second
	 * part is the string content of the xslt template. The second part is only
	 * populated when logger.isDebug is true.
	 * 
	 */
	private final SlidingWindowCacheManager<String, Pair<Templates, String>> templateManager;

	/***
	 * private constructor (singleton pattern)
	 */
	private XsltTransformerManager() {
		this.templateManager = new SlidingWindowCacheManager<String, Pair<Templates, String>>();
	}

	private String createCacheKey(final Class<? extends Object> callerClass,
			final String xsltResourceName) {
		// Creating the cacheKey by combine caller class name and resourceName
		final String callerClassName = callerClass.getName();
		final StringBuilder cacheKeyBuilder = new StringBuilder(
				xsltResourceName.length() + callerClassName.length() + "<<R@C>>".length());

		cacheKeyBuilder.append(xsltResourceName);
		cacheKeyBuilder.append("<<R@C>>");
		cacheKeyBuilder.append(callerClassName);

		return cacheKeyBuilder.toString();
	}

	private Templates getTemplateFromCache(final String cacheKey) {

		if (this.templateManager.containsKey(cacheKey)) {
			// Get the cached template, string pair from cache manager
			final Pair<Templates, String> tempPair = this.templateManager
					.get(cacheKey);
			// Get the template from the pair

			return tempPair == null ? null : tempPair.getValue1();
			
		} else {
			return null;
		}
	}

	/***
	 * Get a transformer. This method will cache the xslt template in memory and
	 * try to reuse them later.
	 * 
	 * @param callerClass
	 * @param xsltResourceName
	 * @return transformer loaded from the xslt resource
	 * @throws TransformerConfigurationException
	 */
	public Transformer getTransformer(
			final Class<? extends Object> callerClass,
			final String xsltResourceName)
			throws TransformerConfigurationException {
		if (callerClass == null) {
			throw new IllegalArgumentException("callerCalss cannot be null");
		}

		if ((xsltResourceName == null) || (xsltResourceName.length() == 0)) {
			throw new IllegalArgumentException(
					"resourceName cannot be null or empty string");
		}

		final String cacheKey = createCacheKey(callerClass, xsltResourceName);

		final Templates templates = getTemplateFromCache(cacheKey);

		if (null != templates) {
//			if (logger.isDebugEnabled()) {
//				logger.debug(String.format(
//						"Retrieved from cache template %s for class %s",
//						xsltResourceName, callerClass.getName()));
//			}
			return templates.newTransformer();
		} else {
			return loadXsltTemplate(callerClass, xsltResourceName, cacheKey);
		}
	}

	private Transformer loadXsltTemplate(
			final Class<? extends Object> callerClass,
			final String xsltResourceName, final String cacheKey)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException {

		final InputStream xsltFileStream = callerClass
				.getResourceAsStream(xsltResourceName);

		if (xsltFileStream != null) {
			final Source xsltSource = new StreamSource(xsltFileStream);

			final TransformerFactory transFact = TransformerFactory
					.newInstance();

			final Templates template = transFact.newTemplates(xsltSource);

			String tempalteText = null;

//			if (logger.isDebugEnabled()) {
//				try {
//					tempalteText = readXsltText(callerClass, cacheKey,
//							xsltResourceName);
//				} catch (final IOException e) {
//					if (logger.isWarnEnabled()) {
//						logger.warn(
//								"Failed while try to read xsl template for debug reason. resource: "
//										+ xsltResourceName, e);
//					}
//				}
//			}

			// Store the template in cache
			this.templateManager.put(cacheKey, new Pair<Templates, String>(
					template, tempalteText));

			try {
				xsltFileStream.close();
			} catch (final IOException e) {
				// Since we already got the XSLT transformer,
				// We will log and keep going in this case
//				if (logger.isErrorEnabled()) {
//					final String logMessage = String
//							.format("Failed to close the xslt stream after open the \"%s\" resource",
//									xsltResourceName);
//					logger.error(logMessage, e);
//				}
			}

//			if (logger.isDebugEnabled()) {
//				logger.debug(String
//						.format("Retrieved from resouce and cached template %s for class %s",
//								xsltResourceName, callerClass.getName()));
//			}

			return template.newTransformer();
		} else {
			throw new TransformerConfigurationException(String.format(
					"XSL resource [%s] is not valid. Please verify.",
					xsltResourceName));
		}
	}

	/****
	 * Release all cached templates
	 */
	public void releaseAllTransformer() {

		this.templateManager.clear();

//		if (logger.isInfoEnabled()) {
//			logger.info("Released all cached xsl templates");
//		}
	}

	/***
	 * Perform a xslt transformation. This method will cache the xslt template
	 * in memory.
	 * 
	 * @param callerClass
	 *            the caller class
	 * @param xsltResourceName
	 *            the xslt resource name (path)
	 * @param xmlNode
	 *            the xml input
	 * @param parameterMap
	 *            the parameter map
	 * @return transformation result in string
	 * @throws TransformerException
	 *             when an exception occur during transformation
	 */
	public String transformToString(final Class<? extends Object> callerClass,
			final String xsltResourceName, final Node xmlNode,
			final Map<String, String> parameterMap) throws TransformerException {

		// **********************************************************************
		// ALERT. There is a bug with JAVA DOM SOURCE with default namespace.
		// This is why we are not using DomSource directly.
		//
		// The way to get around the default namespace issue is to read
		// the string content out of Dom object and then create a StreamSource
		// **********************************************************************
		// final Source xmlSource = new DOMSource(xmlNode);
		//
		// return transformToString(callerClass, xsltResourceName, xmlSource,
		// parameterMap);
		// **********************************************************************

		final String xmlText = XmlUtil.getXmlString(xmlNode);

		final Reader xmlTextReader = new StringReader(xmlText);
		final StreamSource replicatedXmlSource = new StreamSource(xmlTextReader);

		return transformToString(callerClass, xsltResourceName,
				replicatedXmlSource, parameterMap);
	}

	/***
	 * Perform a xslt transformation. This method will cache the xslt template
	 * in memory.
	 * 
	 * @param callerClass
	 *            the caller class
	 * @param xsltResourceName
	 *            the xslt resource name (path)
	 * @param xmlSource
	 *            the xml input
	 * @param parameterMap
	 *            the parameter map
	 * @return transformation result in string
	 * @throws TransformerException
	 *             when an exception occur during transformation
	 */
	public String transformToString(final Class<? extends Object> callerClass,
			final String xsltResourceName, final StreamSource xmlSource,
			final Map<String, String> parameterMap) throws TransformerException {

		final Transformer transformer = getTransformer(callerClass,
				xsltResourceName);

		final String result;

//		if (logger.isDebugEnabled()) {
//			// Log the transformation if required.
//			final String xmlText = XmlUtil.readSource(xmlSource);
//
//			final Reader xmlTextReader = new StringReader(xmlText);
//			final Source replicatedXmlSource = new StreamSource(xmlTextReader);
//
//			result = transform(transformer, replicatedXmlSource, parameterMap);
//
//			final String cacheKey = createCacheKey(callerClass,
//					xsltResourceName);
//
//			final StringBuilder buffer = new StringBuilder();
//			LogMessageBuilder.appendSeparator(buffer, "Xslt Transformation");
//			// NZ 05/26 comment out the next line
//			// The call stack usually does not provide useful information
//			// because only base class name are in the stack
//			// LogMessageBuilder.appendText(buffer, " Call Stack", CallStackUtil
//			// .showCallStack(Thread.currentThread().getStackTrace()));
//			LogMessageBuilder.appendText(buffer, " Input XML", xmlText);
//			LogMessageBuilder.appendParameter(buffer, " Parameters",
//					parameterMap);
//			final Pair<Templates, String> cachedTemplate = this.templateManager
//					.get(cacheKey);
//			if (cachedTemplate != null) {
//				LogMessageBuilder.appendText(buffer, " XSLT: " + cacheKey,
//						cachedTemplate.getValue2());
//			}
//			LogMessageBuilder.appendText(buffer, " Transformation Result",
//					result);
//			LogMessageBuilder.appendSeparator(buffer, null);
//
//			logger.debug(buffer.toString());
//		} else {
			// Perform the transformation without logging
			result = transform(transformer, xmlSource, parameterMap);
//		}

		return result;
	}
}
