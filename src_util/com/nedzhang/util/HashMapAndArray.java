package com.nedzhang.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashMapAndArray<KeyType, ValueType, ArrayItemType> extends
		HashMap<KeyType, ValueType> {

	// public HashMapAndArray(int initialCapacity) {
	// super(initialCapacity);
	// }

	private static final long serialVersionUID = -4583180985992315925L;

	private final List<ArrayItemType> arrayItems;

	public HashMapAndArray() {
		super();
		arrayItems = new ArrayList<ArrayItemType>();
	}

	public List<ArrayItemType> getArrayItems() {
		return arrayItems;
	}

	@Override
	public String toString() {
		String superToStringResult = super.toString();

		if (arrayItems == null || arrayItems.size() == 0) {
			return superToStringResult + "\n{}";
		} else {
			StringBuilder toStringBuilder = new StringBuilder(
					superToStringResult.length() + 100);
			
			toStringBuilder.append(superToStringResult);
			toStringBuilder.append("\n{");

			for (int i=0; i<getArrayItems().size(); i++) {
				
				if (i++ > 0) {
					toStringBuilder.append(',');
				}
				toStringBuilder.append(getArrayItems().get(i));
			}
			toStringBuilder.append('}');
			
			return toStringBuilder.toString();
		}
	}
}
