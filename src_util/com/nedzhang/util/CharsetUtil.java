package com.nedzhang.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/***
 * CharsetUtil class is a charset helper class to provide methods for encoding
 * and decoding strings.
 * 
 * @author nzhang
 * 
 */
public final class CharsetUtil {

	/***
	 * The read block size while decoding a stream into a string
	 */
	private static final int DECODE_READ_BLOCK_SIZE = 4096;

//	private static Log logger = LogFactory.getLog(CharsetUtil.class);

	private CharsetUtil() {

	}

	/***
	 * Encode a string into byte array.
	 * 
	 * @param str
	 *            the string to be encoded
	 * @param charsetName
	 *            the charset to use for the encoding. The method uses default
	 *            charset if this value is null, empty string, or not valid
	 * @return
	 */
	public static byte[] encodeString(final String str, final String charsetName) {
		final Charset charset = getCharset(charsetName);

		final ByteBuffer byteBuffer = charset.encode(str);

		return byteBuffer.array();
	}

	/***
	 * Decode an inputstream into string
	 * 
	 * @param stream
	 *            the input stream
	 * @param charsetName
	 *            the charset to use for the encoding. The method uses default
	 *            charset if this value is null, empty string, or not valid
	 * 
	 * @return The decoded string
	 * 
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public static String decodeStream(final InputStream stream,
			final String charsetName) throws IOException {

		final Charset charset = getCharset(charsetName);

		final CharsetDecoder decoder = charset.newDecoder();

		final InputStreamReader reader = new InputStreamReader(stream, decoder);

		final BufferedReader bufferedReader = new BufferedReader(reader);

		final StringBuilder out = new StringBuilder();
		final char[] b = new char[DECODE_READ_BLOCK_SIZE];
		for (int n; (n = bufferedReader.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	private static Charset getCharset(final String charsetName) {
		Charset charset;

		if ((charsetName == null) || (charsetName.length() == 0)) {
			charset = Charset.defaultCharset();
		} else {
			try {
				charset = Charset.forName(charsetName);
			} catch (final UnsupportedCharsetException e) {
				charset = Charset.defaultCharset();
//				if (logger.isWarnEnabled()) {
//					final String logMessage = String
//							.format("Unsupported Charset \"%s\" specified. Use default charset \"%s\" instead",
//									charsetName, charset.name());
//					logger.warn(logMessage, e);
//				}
			} catch (final IllegalCharsetNameException e) {
				charset = Charset.defaultCharset();
//				if (logger.isWarnEnabled()) {
//					final String logMessage = String
//							.format("Illeagel Charset \"%s\" specified. Use default charset \"%s\" instead",
//									charsetName, charset.name());
//					logger.warn(logMessage, e);
//				}

			}
		}
		return charset;
	}

}
