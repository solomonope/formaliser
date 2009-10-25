package formaliser.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BasicConvertibleClasses implements Iterable<Class<?>> {
	
	private static final Set<Class<?>> CONVERTIBLE_CLASSES = new HashSet<Class<?>>();
	private static final Class<?>[] PRIMITIVE_NUMBERS = new Class<?>[] { byte.class, short.class, int.class, long.class, float.class, double.class };
	
	static {
		CONVERTIBLE_CLASSES.add(String.class);
		CONVERTIBLE_CLASSES.add(Date.class);
		CONVERTIBLE_CLASSES.add(Number.class);
		CONVERTIBLE_CLASSES.add(Boolean.class);
		for (Class<?> primitiveNumber : PRIMITIVE_NUMBERS) {
            CONVERTIBLE_CLASSES.add(primitiveNumber);
        }
		CONVERTIBLE_CLASSES.add(boolean.class);
	}
	
	

	public boolean isConvertible(Class<?> aClass) {
		for (Class<?> convertibleClass : CONVERTIBLE_CLASSES) {
			if (convertibleClass.isAssignableFrom(aClass)) return true;
		}
		return false;
	}
	
	public boolean isNumber(Class<?> aClass) {
	    for (Class<?> primitiveNumber : PRIMITIVE_NUMBERS) {
            if (primitiveNumber.equals(aClass)) return true;
        }
	    Class<?> superclass = aClass.getSuperclass();
        return superclass != null && superclass.equals(Number.class);
	}
	
	public boolean isCollection(Class<?> aClass) {
	    return Collection.class.isAssignableFrom(aClass);
	}
	
	public Class<?> getGenericTypeOfCollection(Field genericField) {
	    return (Class<?>) ((ParameterizedType) genericField.getGenericType()).getActualTypeArguments()[0];
	}
	
	public Set<Class<? extends Number>> getNumericWrappers() {
	    HashSet<Class<? extends Number>> numbers = new HashSet<Class<? extends Number>>();
	    numbers.add(Byte.class);
        numbers.add(Short.class);
        numbers.add(Integer.class);
        numbers.add(Long.class);
        numbers.add(Float.class);
        numbers.add(Double.class);
        return numbers;
	}

    @Override
    public Iterator<Class<?>> iterator() {
        return CONVERTIBLE_CLASSES.iterator();
    }
	
}
