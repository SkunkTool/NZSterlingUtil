package com.nedzhang.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/***
 * The ResourceBundleUtil class is an utility class for reading property files.
 * 
 * @author nzhang
 * 
 */
public final class ResourceBundleUtil {

	public static String getStringResource(
			final Class<? extends Object> callerClass, final String baseName,
			final String key, final boolean exceptionIfNotExist) {
		return getStringResource(callerClass, baseName, key,
				Locale.getDefault(), exceptionIfNotExist);
	}

	public static String getStringResource(
			final Class<? extends Object> callerClass, final String baseName,
			final String key) {
		return getStringResource(callerClass, baseName, key,
				Locale.getDefault());
	}

	public static String getStringResource(
			final Class<? extends Object> callerClass, final String baseName,
			final String key, final Locale locale) {
		return getStringResource(callerClass, baseName, key, locale, true);
	}

	public static String getStringResource(
			final Class<? extends Object> callerClass, final String baseName,
			final String key, final Locale locale,
			final boolean exceptionIfNotExist) {

		final ResourceBundle bundle = ResourceBundle.getBundle(baseName,
				locale, callerClass.getClassLoader());

		if (bundle != null) {
			try {
				return bundle.getString(key);
			} catch (final MissingResourceException e) {
				if (exceptionIfNotExist) {
					throw e;
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}
	
//	public static void main(String[] args) {
//		
//		final ResourceBundle bundle = ResourceBundle.getBundle("skunkwerk_support_tool",
//				Locale.getDefault(), ResourceBundleUtil.class.getClassLoader());
//
//		if (bundle != null) {
//			
//			System.out.println(bundle.getString("STERLING_INTEROP_URL"));
//			
//		} else {
//			System.out.println("bundle is null");
//		}
//	}
}
