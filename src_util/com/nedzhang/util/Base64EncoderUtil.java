package com.nedzhang.util;

/***
 * The Base64EncoderUtil class is an Utility class to encode and decode long to
 * Base64 string. <br>
 * Base 64 characters are:
 * <strong>0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
 * +-</strong>
 * 
 * @author nzhang
 * 
 */
final class Base64EncoderUtil {

	private static final String codes = "0123456789"
			+ "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "+-";

	private static final int MASK_6_BITS = 0x3f;

	/***
	 * Decode a base64 string to a long value
	 * 
	 * @param code
	 * @return
	 */
	public static long decode(final String code) {
		long value = 0;

		for (int i = 0; i < code.length(); i++) {
			final int num = codes.indexOf(code.charAt(i));

			if (num < 0) {
				final String errorMessage = String
						.format("Code is not a Base64 string. Valid characters are \"%s\". Input: \"%s\"",
								codes, code);
				throw new IllegalArgumentException(errorMessage);
			} else {
				value = (value << 6) + num;
			}
		}

		return value;
	}

	/***
	 * Encode a long value to a Base64 string
	 * 
	 * @param value
	 * @return
	 */
	public static String encode(final long value) {

		final char[] code = new char[11];

		for (int i = 0; i < 11; i++) {
			final int bitsToShiftRight = 6 * (10 - i);

			final int index = (int) (value >> bitsToShiftRight) & MASK_6_BITS;

			code[i] = codes.charAt(index);
		}

		return new String(code);
	}

	/***
	 * private constructor to avoid user from instantiate this class.
	 */
	private Base64EncoderUtil() {
	}

	// public static String encode(byte[] byteArray) {
	//
	// StringBuilder strbuilder = new StringBuilder(byteArray.length);
	//
	// for (long i=0; i<byteArray.length; i++) {
	//
	// }
	// }

}
