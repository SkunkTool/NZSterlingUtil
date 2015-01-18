package com.nedzhang.util;

import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathUtil {

	private ClassPathUtil() {

	}

	public static String getClassPath() {

		final ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

		final StringBuilder pathBuilder = new StringBuilder();

		// Get the URLs
		final URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();

		for (int i = 0; i < urls.length; i++) {
			pathBuilder.append(urls[i].getFile());
			pathBuilder.append(';');
		}

		return pathBuilder.toString();
	}

}
