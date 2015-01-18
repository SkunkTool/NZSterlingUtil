package com.nedzhang.util;

import java.util.UUID;

/***
 * The UuidUtil class is an utility class for creating compressed UUID string.
 * 
 * @author nzhang
 * 
 */
public final class UuidUtil {

	private UuidUtil() {

	}

	/***
	 * Generate a random UUID and convert it to a 22 character Base64 string.
	 * 
	 * @return a 22 character Base64 string.
	 */
	public static String getCompressedUuidString() {
		return getCompressedUuidString(UUID.randomUUID());
	}

	/***
	 * Convert UUID to a Base64 string of 22 characters.
	 * 
	 * @param id
	 *            The UUID to convert from
	 * @return a 22 character string
	 */
	public static String getCompressedUuidString(final UUID id) {

		final long leastBits = id.getLeastSignificantBits();
		final long mostBits = id.getMostSignificantBits();

		final String mostBitsCode = Base64EncoderUtil.encode(mostBits);
		final String leastBitsCode = Base64EncoderUtil.encode(leastBits);

		return mostBitsCode + leastBitsCode;
	}

	/***
	 * Get an UUID from a compressed Base64 string
	 * 
	 * @param compressedUuidString
	 *            a 22 character Base64 string
	 * @return an UUID
	 */
	public static UUID getUuidFromCompressedUuidString(
			final String compressedUuidString) {
		final String mostBitsCode = compressedUuidString.substring(0, 11);
		final String leastBitsCode = compressedUuidString.substring(11, 22);

		final long leastBits = Base64EncoderUtil.decode(leastBitsCode);
		final long mostBits = Base64EncoderUtil.decode(mostBitsCode);

		final UUID id = new UUID(mostBits, leastBits);

		return id;

	}

}
