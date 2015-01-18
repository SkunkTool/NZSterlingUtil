package com.nedzhang.util;

public class KeyValueSet<KeyType, ValueType> {

	public KeyType key;
	public ValueType value;

	public KeyValueSet() {

	}

	public KeyValueSet(final KeyType key, final ValueType value) {
		this.key = key;
		this.value = value;
	}
	
	
	@Override
	public String toString() {
		return value.toString();
	}

}
