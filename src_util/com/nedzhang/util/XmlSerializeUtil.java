package com.nedzhang.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.xml.sax.SAXParseException;

/***
 * The XmlSerializeUtil class is an utility class for serialize an object to xml
 * string and desriealize a xml string to an object.
 * 
 * @author nzhang
 * 
 */
public final class XmlSerializeUtil {

	private XmlSerializeUtil() {

	}

	/***
	 * Serialize an object into xml string.
	 * 
	 * @param obj
	 *            object to serialize
	 * @return Xml string represents the object
	 */
	public static String serializeObject(final Object obj) {
		final OutputStream bufferStream = new ByteArrayOutputStream();

		// Create XML encoder.
		final XMLEncoder xenc = new XMLEncoder(bufferStream);

		xenc.writeObject(obj);
		// Need to close the XMLEncoder to flush.
		xenc.close();

		final String serializedString = bufferStream.toString();

		// JavaDoc indicates that we will not need to close
		// a ByteArrayOutputStream. From JavaDoc:
		// Closing a ByteArrayOutputStream has no effect. The methods
		// in this class can be called after the stream has been closed
		// without generating an IOException.
		//
		// bufferStream.close();

		return serializedString;
	}

	/***
	 * Deserialize a xml string into an object
	 * 
	 * @param <T>
	 *            type of the object to deserialize to
	 * @param xmlString
	 *            xml string represents an object
	 * @param classToCastTo
	 *            class to deserialize to
	 * @return object deserialized from the xml
	 * @throws SAXParseException
	 *             if xmlString is not well formatted
	 * @throws ClassCastException
	 *             if the object is not the an instance of classToCastTo
	 */
	public static <T> T deserializeObject(final String xmlString,
			final Class<T> classToCastTo) throws SAXParseException,
			ClassCastException {

		final InputStream fis = new ByteArrayInputStream(xmlString.getBytes());

		return deserializeObject(fis, classToCastTo);

	}

	/***
	 * Deserialize a xml string into an object
	 * 
	 * @param <T>
	 *            type of the object to deserialize to
	 * @param xmlStream
	 *            xml stream represents an object
	 * @param classToCastTo
	 *            class to deserialize to
	 * @return object deserialized from the xml
	 * @throws SAXParseException
	 *             if xmlString is not well formatted
	 * @throws ClassCastException
	 *             if the object is not the an instance of classToCastTo
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserializeObject(final InputStream xmlStream,
			final Class<T> classToCastTo) throws SAXParseException,
			ClassCastException {

		// Create XML decoder.
		final XMLDecoder xmlDecoder = new XMLDecoder(xmlStream);
		
		try {

		final Object deserializedObj = xmlDecoder.readObject();

		// Will not perform cast check,
		// Let the casting throw ClassCastException if needed.
		return (T) deserializedObj;
		} finally {
			if (xmlDecoder != null) {
				xmlDecoder.close();
			}
		}

	}
	
	
	public static void serializeObjectToFile(final String filePath,
			final Serializable objectToSeralize) throws FileNotFoundException,
			IOException {

		try (OutputStream file = new FileOutputStream(filePath);
				OutputStream buffer = new BufferedOutputStream(file);
				XMLEncoder output = new XMLEncoder(buffer);) {
			output.writeObject(objectToSeralize);
		}
	}
	
	
	public static Object deserializeObjectFromFile(final String filePath) throws FileNotFoundException, IOException, ClassNotFoundException 
			 {

		try (FileInputStream file = new FileInputStream(filePath);
				BufferedInputStream buffer = new BufferedInputStream(file);
				XMLDecoder input = new XMLDecoder(buffer);) {
			
			return input.readObject();
			
		}
	}
}
