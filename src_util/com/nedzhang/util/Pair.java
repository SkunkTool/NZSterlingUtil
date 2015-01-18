package com.nedzhang.util;

/***
 * The Pair class represents a pair
 * 
 * @author nzhang
 * 
 * @param <Type1>
 *            The type of the value1
 * @param <Type2>
 *            The type of the value2
 */
public class Pair<Type1, Type2> {

	private Type1 value1;

	private Type2 value2;

	public Pair() {
	}

	public Pair(final Type1 value1, final Type2 value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	public Type1 getValue1() {
		return this.value1;
	}

	public void setValue1(final Type1 value1) {
		this.value1 = value1;
	}

	public Type2 getValue2() {
		return this.value2;
	}

	public void setValue2(final Type2 value2) {
		this.value2 = value2;
	}

}
