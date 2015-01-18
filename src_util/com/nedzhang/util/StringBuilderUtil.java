package com.nedzhang.util;

import java.io.IOException;
import java.io.Reader;

public final class StringBuilderUtil {

	private static final int DECODE_READ_BLOCK_SIZE = 4096;

	private StringBuilderUtil() {

	}

	public static void upperCaseAndStripSpace(final StringBuilder stringBuilder) {
		for (int i = stringBuilder.length() - 1; i >= 0; i--) {
			final char currentChar = stringBuilder.charAt(i);
			if (' ' == currentChar) {
				stringBuilder.deleteCharAt(i);
			} else if (Character.isLowerCase(currentChar)) {
				stringBuilder.setCharAt(i, Character.toUpperCase(currentChar));
			}
		}
	}

	public static void appendWhenNotNull(final StringBuilder stringBuilder,
			final String stringToAppend) {
		if ((stringToAppend != null) && (stringToAppend.length() > 0)) {
			stringBuilder.append(stringToAppend);
		}
	}

	public static void appendLineBreakOnlyIfNotEndWithOne(
			final StringBuilder stringBuilder) {
		final char lastChar = stringBuilder.charAt(stringBuilder.length() - 1);

		if (lastChar != '\n') {
			stringBuilder.append('\n');
		}
	}

	public static String readerToString(final Reader reader) throws IOException {

		final StringBuilder out = new StringBuilder();
		final char[] b = new char[DECODE_READ_BLOCK_SIZE];

		try {
			for (int n; (n = reader.read(b)) != -1;) {
				out.append(b, 0, n);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return out.toString();
	}

}
