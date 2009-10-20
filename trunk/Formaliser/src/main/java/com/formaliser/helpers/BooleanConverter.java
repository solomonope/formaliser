package com.formaliser.helpers;

import static java.lang.Boolean.FALSE;

public class BooleanConverter {

    public boolean canConvert(Class<?> entityClass) {
        return entityClass == boolean.class || entityClass == Boolean.class;
    }

    /**
     * @param value is nullable.
     * @return
     */
    public String convert(Boolean value) {
        if (value == null) return FALSE.toString();
        return value.toString();
    }

    /**
     * @param value is nullable.
     * @return
     */
    public Boolean convert(String value) {
        return Boolean.valueOf(value);
    }
}
