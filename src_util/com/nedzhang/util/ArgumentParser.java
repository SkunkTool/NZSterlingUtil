package com.nedzhang.util;


public class ArgumentParser {

	private static final String PARAMETER_KEY_UNKNOWN = "unknown";

	private ArgumentParser() {

	}

	/***
	 * Parse arguments into key value pair.
	 * 
	 * @param args
	 * @return
	 */
	public static HashMapAndArray<String, String, String> parse(
			final String[] args) {

		final HashMapAndArray<String, String, String> parameterMap = new HashMapAndArray<String, String, String>();

		String parameterKey = PARAMETER_KEY_UNKNOWN;

		for (final String arg : args) {
			if (arg != null && arg.length() > 1) {
				if (arg.charAt(0) == '-') {
					if (arg.length() > 1) {
						parameterKey = arg.substring(1);

						if (parameterKey.length() > 3
								&& parameterKey.contains("=")) {
							int equalSingPos = parameterKey.indexOf("=");
							String key = parameterKey
									.substring(0, equalSingPos);
							String val = parameterKey.substring(equalSingPos);
							parameterMap.put(key, val);
							parameterKey = PARAMETER_KEY_UNKNOWN;
						} else {
							parameterMap.put(parameterKey, null);
						}
					}
				} else if (PARAMETER_KEY_UNKNOWN.equals(parameterKey)) {

					parameterMap.getArrayItems().add(arg);

				} else {

					parameterMap.put(parameterKey, arg);
					parameterKey = PARAMETER_KEY_UNKNOWN;
				}
			}
		}

		return parameterMap;
	}
	
	
	public static void main(String[] args) {
		HashMapAndArray<String, String, String>argument = parse(args);
		
		System.out.println(argument.toString());
	}
}
