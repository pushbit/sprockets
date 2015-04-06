/* Copyright 2004-2014 Google Inc, International Business Machines Corporation and others */

package net.sf.sprockets.util;

/**
 * Unit such as length, mass, volume, currency, etc. Modelled after the class of the same name in <a
 * href="https://ssl.icu-project.org/apiref/icu4j/" target="_blank">ICU4J</a>.
 * 
 * @since 2.2.0
 */
public class MeasureUnit {
	/** Constant for unit of length: kilometer */
	public static final MeasureUnit KILOMETER = new MeasureUnit("length", "kilometer");

	/** Constant for unit of length: mile */
	public static final MeasureUnit MILE = new MeasureUnit("length", "mile");

	private final String type;
	private final String subType;

	private MeasureUnit(String type, String subType) {
		this.type = type;
		this.subType = subType;
	}

	/**
	 * Get the type, such as "length".
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the subType, such as “foot”.
	 */
	public String getSubtype() {
		return subType;
	}

	@Override
	public int hashCode() {
		return 31 * type.hashCode() + subType.hashCode();
	}

	@Override
	public boolean equals(Object rhs) {
		if (rhs == this) {
			return true;
		}
		if (!(rhs instanceof MeasureUnit)) {
			return false;
		}
		MeasureUnit c = (MeasureUnit) rhs;
		return type.equals(c.type) && subType.equals(c.subType);
	}

	@Override
	public String toString() {
		return type + "-" + subType;
	}
}
