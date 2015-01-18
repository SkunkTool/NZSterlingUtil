package com.nedzhang.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/***
 * The ThrowableUtil class is an utility class for exception process
 * 
 * @author nzhang
 * 
 */
public final class ThrowableUtil {

	private ThrowableUtil() {

	}

	/***
	 * Get the stack trace of the error
	 * 
	 * @param error
	 * @return the string result like error.printStackTrace
	 */
	private static String getStackTrace(final Throwable error) {
		if (error == null) {
			return null;
		}

		final Writer stackWriter = new StringWriter();

		error.printStackTrace(new PrintWriter(stackWriter));

		return stackWriter.toString();
	}

	/***
	 * Get a descriptive message of the throwable,
	 * 
	 * @param e
	 *            The throwable
	 * @param includeStackTrace
	 *            if true, stack trace will be included
	 * @return
	 */
	public static String getCompleteMessage(Throwable e,
			final boolean includeStackTrace) {
		if (e == null) {
			return null;
		}

		final StringBuilder messageBuilder = new StringBuilder();

		if (includeStackTrace) {
			messageBuilder.append(getStackTrace(e));
		} else {
			// Print out e then e.getCause() then e.getCause().getCause() ...
			// until there is not more cause.
			do {
				if (messageBuilder.length() > 0) {
					messageBuilder.append("\nCaused by: ");
				}
				messageBuilder.append(e.getClass().getName());
				messageBuilder.append(": ");
				messageBuilder.append(e.getMessage());
			} while ((e = e.getCause()) != null);
		}

		return messageBuilder.toString();
	}

}
