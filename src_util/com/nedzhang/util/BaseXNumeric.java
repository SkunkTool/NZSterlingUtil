package com.nedzhang.util;

/***
 * The BaseXNumeric class represents a base x numbering system.
 * 
 * @author nzhang
 * 
 */
public class BaseXNumeric {

	private final String baseCharacters;

	/***
	 * The number of values in a digit
	 */
	private final int baseLength;

	/***
	 * Create a new instance of BaseXNumeric class.
	 * 
	 * @param baseCharacters
	 *            The characters to use in the numeric system
	 */
	public BaseXNumeric(final String baseCharacters) {
		if ((baseCharacters == null) || (baseCharacters.length() == 0)) {
			throw new IllegalArgumentException(
					"baseCharacters cannot be null or zero length");
		}
		this.baseCharacters = baseCharacters;
		this.baseLength = baseCharacters.length();
	}

	/***
	 * 
	 * Convert a positive number to a BaseXNumeric string
	 * 
	 * @param positiveNumber
	 *            a positive number to convert
	 * @return The BaseXNumeric presentation of the input value
	 */
	public String convertToBaseX(final long positiveNumber) {
		return convertToBaseX(positiveNumber, 0);
	}

	/***
	 * 
	 * Convert a positive number to a BaseXNumeric string
	 * 
	 * @param positiveNumber
	 *            a positive number to convert
	 * @param baseXStringMinimumLength
	 *            Minimum length of the return string
	 * @return A BaseXNumeric string at least baseXStringMinimumLength long
	 */
	public String convertToBaseX(final long positiveNumber,
			final int baseXStringMinimumLength) {

		if (positiveNumber < 0) {
			throw new IllegalArgumentException(
					"value can be only a positive number");
		}

		long left = positiveNumber;

		final StringBuilder resultBuilder = new StringBuilder();

		do {
			final long remainder = left % this.baseLength;

			left = left / this.baseLength;

			resultBuilder.append(this.baseCharacters.charAt((int) remainder));
		} while (left > 0);

		while (resultBuilder.length() < baseXStringMinimumLength) {
			resultBuilder.append(this.baseCharacters.charAt(0));
		}

		return resultBuilder.reverse().toString();
	}

	/****
	 * Convert a baseXNumber to long
	 * 
	 * @param baseXNumber
	 *            a baseXNumber string
	 * @return the long value
	 */
	public long convertFromBaseX(final String baseXNumber) {
		if ((baseXNumber == null) || (baseXNumber.length() == 0)) {
			return 0;
		} else {
			long result = 0;

			final int baseXNumberLength = baseXNumber.length();
			for (int i = 0; i < baseXNumberLength; i++) {
				final char ch = baseXNumber.charAt(i);
				final int valOfCh = this.baseCharacters.indexOf(ch, 0);

				if (valOfCh < 0) {
					final String exceptionMessage = String
							.format("\"%s\" is not valid. \'%c\' is out of range. Valid characters are \"%s\"",
									baseXNumber, ch, this.baseCharacters);

					throw new IndexOutOfBoundsException(exceptionMessage);
				}

				result = (result * this.baseLength) + valOfCh;
			}

			return result;
		}
	}

}
