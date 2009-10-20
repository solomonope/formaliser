package com.formaliser.configuration;

import java.lang.reflect.Field;
import java.util.List;

import com.formaliser.data.InputType;

public interface FieldAnalyzer {

    boolean isRequired(Field field, Object entity);
    InclusionMode isIncluded(Field field, Object entity);
    InputType chooseType(Field field, Object entity);
    List<Class<?>> getClassHierarchy(Class<?> beanClass);
}
