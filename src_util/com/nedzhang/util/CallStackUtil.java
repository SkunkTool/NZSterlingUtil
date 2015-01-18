package com.nedzhang.util;

/***
 * The CallStackUtil class is an utility class to provide Call Stack
 * information. <br>
 * Common usage is:
 * CallStackUtil.showCallStack(Thread.currentThread().getStackTrace())
 * 
 * @author nzhang
 * 
 */
public final class CallStackUtil {

	private CallStackUtil() {

	}

	/***
	 * Get the current caller
	 * 
	 * @param stackTraceElements
	 * @return
	 */
	public static String whoCalledMe(
			final StackTraceElement[] stackTraceElements) {
		final StackTraceElement caller = stackTraceElements[4];
		final String classname = caller.getClassName();
		final String methodName = caller.getMethodName();
		final int lineNumber = caller.getLineNumber();
		return classname + "." + methodName + ":" + lineNumber;
	}

	/***
	 * Get the complete call stack
	 * 
	 * @param stackTraceElements
	 * @return
	 */
	public static String showCallStack(
			final StackTraceElement[] stackTraceElements) {
		final StringBuilder stackMessageBuilder = new StringBuilder();

		for (int i = 2; i < stackTraceElements.length; i++) {
			final StackTraceElement ste = stackTraceElements[i];
			final String classname = ste.getClassName();
			final String methodName = ste.getMethodName();
			final int lineNumber = ste.getLineNumber();
			stackMessageBuilder.append(classname);
			stackMessageBuilder.append('.');
			stackMessageBuilder.append(methodName);
			stackMessageBuilder.append('.');
			stackMessageBuilder.append(lineNumber);
			stackMessageBuilder.append('\n');
		}

		return stackMessageBuilder.toString();
	}
}
