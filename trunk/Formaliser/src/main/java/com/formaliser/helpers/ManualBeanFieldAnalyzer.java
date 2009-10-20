package com.formaliser.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;

import com.formaliser.configuration.FieldAnalyzer;
import com.formaliser.configuration.InclusionMode;
import com.formaliser.data.InputType;

public class ManualBeanFieldAnalyzer implements FieldAnalyzer {
    
    private final Map<Class<?>, Set<String>> exclusions = new HashMap<Class<?>, Set<String>>();
    
    @Override
    public InclusionMode isIncluded(Field field, Object bean) {
        Class<?> declaringType;
        if (bean.getClass() == Class.class) {
            declaringType = (Class<?>) bean;
        } else {
            declaringType = bean.getClass();
        }
        
        if (Modifier.isTransient(field.getModifiers())) return InclusionMode.NONE;
        
        return !exclusions.containsKey(declaringType) || !exclusions.get(declaringType).contains(field.getName()) ? InclusionMode.FULL : InclusionMode.NONE;
    }

    @Override
    public boolean isRequired(Field field, Object bean) {
        return true;
    }

    @Override
    public InputType chooseType(Field field, Object bean) {
        return StandardInputTypes.choose(field);
    }
    
    @Override
    public List<Class<?>> getClassHierarchy(Class<?> beanClass) {
        List<Class<?>> classHierarchy = ClassUtils.getAllSuperclasses(beanClass);
        Collections.reverse(classHierarchy);
        classHierarchy.add(beanClass);
        return classHierarchy;
    }

    public FieldAnalyzer exclude(Class<?> beanClass, String... fields) {
        ManualBeanFieldAnalyzer newAnalyzer = new ManualBeanFieldAnalyzer();
        HashSet<String> newExclusions = new HashSet<String>();
        for (String field : fields) {
            newExclusions.add(field);
        }
        newAnalyzer.exclusions.put(beanClass, newExclusions);
        return newAnalyzer;
    }

}
