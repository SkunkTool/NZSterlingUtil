package com.nedzhang.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XsdDateFormatter {
	private static final SimpleDateFormat currentDateFormatter =
	// The date formatter for SPP data value
	new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private XsdDateFormatter() {

	}

	public static String format(final Date date) {
		String currentDateString = currentDateFormatter.format(date);

		// Hack: SPP want date in the format of
		// 2009-05-01T17:42:08-05:00
		// instead of 2009-05-01T17:42:08-0500. So I insert : at the
		// end-2 position
		currentDateString = currentDateString.substring(0,
				currentDateString.length() - 2)
				+ ":"
				+ currentDateString.substring(currentDateString.length() - 2);

		return currentDateString;
	}
}
