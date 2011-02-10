package com.moandjiezana.formaliser.helpers;

import java.lang.reflect.Field;

import com.moandjiezana.formaliser.data.InputType;


public enum StandardInputTypes implements InputType {
	TEXT, CHECKBOX, SELECT, HIDDEN, OPTION, CHOICE;
  
	public static StandardInputTypes choose(Field field) {
		if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) return CHECKBOX;
		if (Enum.class.isAssignableFrom(field.getType())) return SELECT;
		return StandardInputTypes.TEXT;
	}

}