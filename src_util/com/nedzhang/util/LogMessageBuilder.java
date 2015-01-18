package com.nedzhang.util;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

/***
 * LogMessageBuilder class is an utility class to format logmessage in standard
 * format.
 * 
 * @author nzhang
 * 
 */
public final class LogMessageBuilder {

	private static final String TITLE_START_LINE = "===================================================================================\n";

	private static final String TITLE_END_LINE = "\n-----------------------------------------------------------------------------------\n";

	private static final String SEPARATOR_COMPLETE_LINE = "***********************************************************************************";

	private LogMessageBuilder() {

	}

	/***
	 * Append a separator (usually a header or footer) to the logMessageBuilder
	 * 
	 * @param logMessageBuilder
	 *            the string builder to append to
	 * @param message
	 *            the content. If null, the method appends a complete line of
	 *            separator characters.
	 */
	public static void appendSeparator(final StringBuilder logMessageBuilder,
			final String message) {
		if ((message == null) || (message.length() == 0)) {
			logMessageBuilder.append(SEPARATOR_COMPLETE_LINE);
		} else {
			logMessageBuilder.append("*********** ");
			logMessageBuilder.append(message);
			logMessageBuilder.append(' ');
			if ((message != null) && (message.length() < 70)) {
				logMessageBuilder.append(SEPARATOR_COMPLETE_LINE.substring(0,
						70 - message.length()));
			}
		}
		logMessageBuilder.append('\n');
	}

	/***
	 * Append a xml content to the logMessageBuilder
	 * 
	 * @param logMessageBuilder
	 *            the string builder to append to
	 * @param title
	 *            the title of the xml content
	 * @param node
	 *            the xml content
	 */
	public static void appendXmlNode(final StringBuilder logMessageBuilder,
			final String title, final Node node) {

		if (node == null) {
			appendText(logMessageBuilder, title, "NULL");
		} else {
			appendText(logMessageBuilder, title, XmlUtil.getXmlString(node));
		}
	}

	/***
	 * Append a parameter map to the logMessageBuilder
	 * 
	 * @param logMessageBuilder
	 *            the string builder to append to
	 * 
	 * @param title
	 *            the title of the parameter map
	 * @param parameter
	 *            map
	 */
	public static void appendParameter(final StringBuilder logMessageBuilder,
			final String title, final Map<String, String> parameters) {
		logMessageBuilder.append(TITLE_START_LINE);
		logMessageBuilder.append(title);
		logMessageBuilder.append(TITLE_END_LINE);
		if ((parameters == null) || (parameters.size() == 0)) {
			logMessageBuilder.append(" Map is empty\n");
		} else {
			for (final Entry<String, String> parameter : parameters.entrySet()) {
				logMessageBuilder.append(String.format("%25s | %s\n",
						parameter.getKey(), parameter.getValue()));
			}
		}
		logMessageBuilder.append('\n');
	}

	/***
	 * Append a string content to the logMessageBuilder
	 * 
	 * @param logMessageBuilder
	 *            the string builder to append to
	 * @param title
	 *            the title of the string content
	 * @param text
	 *            the string content
	 */
	public static void appendText(final StringBuilder logMessageBuilder,
			final String title, final String text) {
		logMessageBuilder.append(TITLE_START_LINE);
		logMessageBuilder.append(title);
		logMessageBuilder.append(TITLE_END_LINE);

		logMessageBuilder.append(text);
		StringBuilderUtil.appendLineBreakOnlyIfNotEndWithOne(logMessageBuilder);

	}
}
