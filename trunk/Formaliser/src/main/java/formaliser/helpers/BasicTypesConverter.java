package formaliser.helpers;

import static org.apache.commons.lang.StringUtils.*;
import static org.apache.commons.lang.math.NumberUtils.isDigits;

import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;

public class BasicTypesConverter {

    private static final BooleanConverter BOOLEAN_CONVERTER = new BooleanConverter();
    private static final DateConverter DATE_CONVERTER = new DateConverter();
    private static final BasicConvertibleClasses CONVERTIBLE_CLASSES = new BasicConvertibleClasses();
    
    public boolean canConvert(Class<?> classToConvert) {
        return CONVERTIBLE_CLASSES.isConvertible(classToConvert);
    }
    
    public boolean isBoolean(Class<?> classToCheck) {
        return BOOLEAN_CONVERTER.canConvert(classToCheck);
    }

    public Object convert(String value, Class<?> toClass) {
        if (String.class == toClass) return "".equals(value) ? null : value;
        if (DATE_CONVERTER.canConvert(toClass)) return DATE_CONVERTER.convert(value);
        
        if (CONVERTIBLE_CLASSES.isNumber(toClass) && isNotValidNumber(value)) return null;
        
        if (isBoolean(toClass)) return BOOLEAN_CONVERTER.convert(value);
        
        if (byte.class == toClass || Byte.class == toClass) return Byte.valueOf(value);
        if (short.class == toClass || Short.class == toClass) return Short.valueOf(value);
        if (int.class == toClass || Integer.class == toClass) return Integer.valueOf(value);
        if (long.class == toClass || Long.class == toClass) return Long.valueOf(value);
        if (float.class == toClass || Float.class == toClass) return Float.valueOf(value);
        if (double.class == toClass || Double.class == toClass) return Double.valueOf(value);
        
        throw new RuntimeException(BasicTypesConverter.class.getSimpleName() + " cannot convert " + toClass.getName());
    }

    private boolean isNotValidNumber(String value) {
        return !isDigits(removeDot(value));
    }

    private String removeDot(String value) {
        return replaceOnce(value, ".", "");
    }
    
    public String convert(Object value) {
        if (value == null) return EMPTY;
        if (String.class == value.getClass()) return (String) value;
        if (NumberUtils.isNumber(value.toString())) {
            return value.toString();
        }
        if (BOOLEAN_CONVERTER.canConvert(value.getClass())) return BOOLEAN_CONVERTER.convert((Boolean) value);
        if (DATE_CONVERTER.canConvert(value.getClass())) return DATE_CONVERTER.convert((Date) value);
        throw new RuntimeException(BasicTypesConverter.class.getSimpleName() + " cannot convert " + value.getClass().getName());
    }

}
