package com.moandjiezana.formaliser.forms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.moandjiezana.formaliser.helpers.BasicConvertibleClasses;
import com.moandjiezana.formaliser.helpers.BasicTypesConverter;


/**
 * All reading methods can return null, leaving validation to another class.
 */
public class FormReader {
    private final BasicTypesConverter converter = new BasicTypesConverter();

    public <T> T readParameter(String parameterName, Class<T> targetClass, Map<String, String[]> requestParameters, int index) {
        if (!requestParameters.containsKey(parameterName) && converter.isBoolean(targetClass)) return (T) converter.convert("", Boolean.class) ;
        if (!requestParameters.containsKey(parameterName)) return null;
        return (T) converter.convert(requestParameters.get(parameterName)[index], targetClass);
    }
    
    public <T> T read(String filter, Class<T> targetClass, Map<String, String[]> requestParameters, int index) {
        
        if (new BasicConvertibleClasses().isConvertible(targetClass)) {
            return readParameter(filter, targetClass, requestParameters, index);
        } else if (isCollection(targetClass)) {
            throw new UnsupportedOperationException("Please call readCollection() directly");
        }
        return readClass(filter, targetClass, requestParameters, index);
    }
    
    public <T> T readClass(String filter, Class<T> targetClass, Map<String, String[]> requestParameters, int index) {
        try {
            Map<String, String[]> filteredParameters = filterParameters(filter, requestParameters);
            
            if (filteredParameters.isEmpty()) return null;
            
            Constructor<T> noArgsConstructor = targetClass.getDeclaredConstructor();
            noArgsConstructor.setAccessible(true);
            T newInstance = noArgsConstructor.newInstance();
            
            for (Field field : targetClass.getDeclaredFields()) {
                field.setAccessible(true);
                String extendedFilter = extendFilter(filter, field);
                if (converter.canConvert(field.getType())) {
                    Object parameterValue = readParameter(extendedFilter, field.getType(), requestParameters, index);
                    if (parameterValue != null) {
                        field.set(newInstance, parameterValue);
                    }
                } else if (isCollection(field.getType())) {
                    Class<? extends Collection<?>> targetCollection = (Class<? extends Collection<?>>) field.getType();
                    Collection<Object> result = readCollection(extendedFilter, targetCollection, (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0], filteredParameters, index);
                    field.set(newInstance, result);
                } else {
                    field.set(newInstance, readClass(extendedFilter, field.getType(), requestParameters, index));
                }
            }
            return newInstance;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isCollection(Class<?> targetClass) {
        return Collection.class.isAssignableFrom(targetClass);
    }
    
    public Collection<Object> readCollection(String filter, Class<? extends Collection> targetCollection, Class<?> targetClass, Map<String, String[]> requestParameters, int index) {
        try {
            Map<String, String[]> filteredParameters = new HashMap<String, String[]>();
            for (Entry<String, String[]> entry : requestParameters.entrySet()) {
                if (entry.getKey().startsWith(filter)) filteredParameters.put(entry.getKey(), entry.getValue());
            }

            Collection<Object> results;
            if (targetCollection == Set.class) results = new HashSet<Object>();
            else results = new ArrayList<Object>();
            
            int count = 0;
            
            for (String[] values : filteredParameters.values()) {
                count = values.length;
                break;
            }
            
            for (int i = 0; i < count; i++) {
                HashMap<String, String[]> parameter = new HashMap<String, String[]>();
                
                for (Entry<String, String[]> entry : filteredParameters.entrySet()) {
                    parameter.put(entry.getKey(), new String[] { entry.getValue()[i]});
                }
                results.add(read(filter, targetClass, parameter, 0));
                
            }
            return results;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String[]> filterParameters(String filter, Map<String, String[]> requestParameters) {
        Map<String, String[]> filteredParameters = new HashMap<String, String[]>();
        for (Entry<String, String[]> entry : requestParameters.entrySet()) {
            if (StringUtils.isEmpty(filter) || entry.getKey().startsWith(filter)) filteredParameters.put(entry.getKey(), entry.getValue());
        }
        return filteredParameters;
    }
    
    private String extendFilter(String oldFilter, Field field) {
        return (StringUtils.isNotEmpty(oldFilter) ? oldFilter + "." : "") + field.getName();
    }
}
