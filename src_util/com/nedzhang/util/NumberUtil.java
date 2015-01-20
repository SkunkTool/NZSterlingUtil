package com.nedzhang.util;

public class NumberUtil {

	private static class NumberParseResult {
		long intPart = 0;
		long deciPart = 0;
		int deciLength = 0;
		short sign = -1;
		boolean hasUnparsedChar = false;
	}

	private NumberUtil() {

	}

	public static int parseInt(final String stringToParse) {
		long longValue = parseLong(stringToParse);
		
		if (longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE) {
			throw new IllegalArgumentException("Failed to parse "
					+ stringToParse + " as int. Too Large +-");
		} else {
			return (int) longValue;
		}
	}

	public static long parseLong(final String stringToParse) {
		
		final NumberParseResult result = parse(stringToParse);

		if (result != null && !result.hasUnparsedChar) {
			return result.sign * result.intPart;
		} else {
			throw new IllegalArgumentException("Failed to parse "
					+ stringToParse + " as long");
		}
	}

	public static double parseDouble(final String stringToParse) {

		final NumberParseResult result = parse(stringToParse);

		if (result != null && !result.hasUnparsedChar) {
			return result.sign
					* (result.intPart + result.deciPart
							/ Math.pow(10, result.deciLength));
		} else {
			throw new IllegalArgumentException("Failed to parse "
					+ stringToParse + " as double");
		}
	}
	
	public static boolean isNumeric(final String str) {
		
		NumberParseResult result = parse(str);
		
		return !result.hasUnparsedChar;
	}

	private static NumberParseResult parse(final String stringToParse) {

		final NumberParseResult result = new NumberParseResult();

		boolean afterDecimal = false;

		for (int i = 0; i < stringToParse.length(); i++) {

			final char ch = stringToParse.charAt(i);

			// ignore things are not digit or .

			if (Character.isDigit(ch)) {
				if (afterDecimal) {
					result.deciPart = result.deciPart * 10 + (ch - '0');
					result.deciLength++;
				} else {
					result.intPart = result.intPart * 10 + (ch - '0');
				}
			} else if ('.' == ch) {
				if (!afterDecimal) {
					afterDecimal = true;
				} else {
					result.hasUnparsedChar = true;
				}
			} else if ('-' == ch) {
				if (result.intPart == 0 && result.deciPart == 0
						&& result.sign == 1) {
					result.sign = -1;
				} else {
					result.hasUnparsedChar = true;
				}
			} else if (',' == ch) {
				// ignore the ,
			} else if (Character.isWhitespace(ch)) {
				// ignore whitespace
			} else {
				result.hasUnparsedChar = true;
			}
		}

		return result;
	}

	// Testing code *******************************************

	public static void main(final String[] args) {
		test("-283123");
		test("-283,123");
		test("-283,123.00");
		test("-283123.00");
		test("   -   2,831,123.00");
		test("2,832342341241243A1,123.00sdf83877643");
	}

	private static void test(final String str) {
		System.out.print(str);
		System.out.print(" is ");
		System.out.print(parseDouble(str));
		System.out.print("  ");
		System.out.println(parseInt(str));
	}

}
