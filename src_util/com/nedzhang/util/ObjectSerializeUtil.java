package com.nedzhang.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class ObjectSerializeUtil {

	public static void serializeObjectToFile(final String filePath,
			final Serializable objectToSeralize) throws FileNotFoundException,
			IOException {

		try (OutputStream file = new FileOutputStream(filePath);
				OutputStream buffer = new BufferedOutputStream(file);
				ObjectOutput output = new ObjectOutputStream(buffer);) {
			output.writeObject(objectToSeralize);
		}
	}
	
	
	public static Object deserializeObjectFromFile(final String filePath) throws FileNotFoundException, IOException, ClassNotFoundException 
			 {

		try (FileInputStream file = new FileInputStream(filePath);
				BufferedInputStream buffer = new BufferedInputStream(file);
				ObjectInputStream input = new ObjectInputStream(buffer);) {
			
			return input.readObject();
			
		}
	}
}
