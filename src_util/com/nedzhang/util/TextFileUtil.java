package com.nedzhang.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class TextFileUtil {

	private TextFileUtil() {

	}

	public static void createParentFolderIfNeeded(final File file)
			throws IOException {

		final File parent = file.getParentFile();

		if ((parent != null) && !parent.exists()) {
			createParentFolderIfNeeded(parent);
		} else {
			file.mkdir();
		}
	}

	public static void writeTextFile(final File fileToWrite,
			final String encoding, final boolean append, final String content)
			throws IOException {

		if (!fileToWrite.exists()) {
			createParentFolderIfNeeded(fileToWrite.getParentFile());
		}

		final FileOutputStream outputStream = new FileOutputStream(fileToWrite,
				append);

		try {

			final OutputStreamWriter writer = new OutputStreamWriter(
					outputStream, encoding);
			final BufferedWriter bufferedWriter = new BufferedWriter(writer);

			bufferedWriter.write(content);

			bufferedWriter.close();
		} finally {
			closeFileStreamSuppressException(outputStream);
		}
	}

	public static String readResourceTextFile(final String resourcePath,
			final String encoding) throws IOException {
		
		final InputStream resourceStream = TextFileUtil.class.getResourceAsStream(resourcePath);
		
		if (resourceStream != null) {
			return readStream(resourceStream, encoding);
		} else {
			return null;
		}
	}
	
	public static String readTextFile(final File fileToRead,
			final String encoding) throws IOException {

		if ((fileToRead != null) & fileToRead.exists()) {

			final InputStream fileStream = new FileInputStream(fileToRead);

			return readStream(fileStream, encoding);

		} else {
			return null;
		}
	}

	private static String readStream(
			final InputStream fileStream, final String encoding) throws IOException {
		try {

			final String content = CharsetUtil.decodeStream(fileStream,
					encoding);
			return content;

		} finally {

			closeFileStreamSuppressException(fileStream);
		}
	}

	private static void closeFileStreamSuppressException(
			final Closeable fileStream) {

		if (fileStream != null) {
			try {
				fileStream.close();
			} catch (final IOException e1) {
				// The callers are in catch block. Cannot throw another
				// exception
				// to mask the previous exception. Just log the exception.
				e1.printStackTrace();
			}
		}
	}
}
