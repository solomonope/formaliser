package com.formaliser.data;

import static org.apache.commons.lang.StringUtils.*;
import static org.apache.commons.lang.WordUtils.capitalize;

import org.apache.commons.lang.StringUtils;

public class FieldName {

	private final String fieldName;
	
	public FieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return The camel cased version of {@link #toString()}
	 */
	public String toId() {
		return capitalize(toString(), new char[] {'.'}).replaceAll("\\.", "");
	}
	
	public Object toId(Long id) {
		return toId() + "-" + id;
	}
	
	/**
	 * @return The humanized {@link #toShortName() short name} 
	 */
	public String toLabel() {
		return StringUtils.capitalize(join(splitByCharacterTypeCamelCase(toShortName()), ' ').toLowerCase());
	}

	/**
	 * 
	 * @return the part of the enum's name after the _
	 */
	public String toShortName() {
		return substringAfterLast(toString(), ".");
	}

	/**
	 * Replaces the enum name's _ by a .
	 */
	@Override
	public String toString() {
		return fieldName.replaceAll("_", "\\.");
	}

	/**
	 * 
	 * @return The humanized full enum name
	 */
	public String toMessage() {
		return (toRoot() + " " + toLabel()).toLowerCase();
	}
	
	public String toRoot() {
		return StringUtils.substringBefore(fieldName, ".");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fieldName.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		FieldName other = (FieldName) obj;
		return fieldName.equals(other.fieldName);
	}
	
	
}
