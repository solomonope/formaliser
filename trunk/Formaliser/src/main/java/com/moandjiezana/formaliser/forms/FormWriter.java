package com.moandjiezana.formaliser.forms;

import static java.lang.Boolean.FALSE;
import static org.apache.commons.lang.StringUtils.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.moandjiezana.formaliser.configuration.FieldAnalyzer;
import com.moandjiezana.formaliser.configuration.HandlesJpaRelationships;
import com.moandjiezana.formaliser.configuration.InclusionMode;
import com.moandjiezana.formaliser.data.ChoiceElement;
import com.moandjiezana.formaliser.data.FieldName;
import com.moandjiezana.formaliser.data.FormElement;
import com.moandjiezana.formaliser.data.SimpleFormElement;
import com.moandjiezana.formaliser.helpers.BasicTypesConverter;
import com.moandjiezana.formaliser.helpers.BooleanConverter;
import com.moandjiezana.formaliser.helpers.JpaFieldAnalyzer;
import com.moandjiezana.formaliser.helpers.ManualBeanFieldAnalyzer;
import com.moandjiezana.formaliser.helpers.StandardInputTypes;


public class FormWriter {
    
    private final BooleanConverter booleanConverter = new BooleanConverter();
    private final BasicTypesConverter typesConverter;
    
    private final FieldAnalyzer fieldAnalyzer;

    private FormWriter(BasicTypesConverter basicConverter, FieldAnalyzer fieldConfiguration) {
        this.typesConverter = basicConverter;
        this.fieldAnalyzer = fieldConfiguration;
    }

    public FormWriter forJpa() {
        return new FormWriter(typesConverter, new JpaFieldAnalyzer());
    }
    
    public FormWriter analyzeFieldsWith(FieldAnalyzer fieldAnalyzer) {
        return new FormWriter(typesConverter, fieldAnalyzer);
    }
    
    public FormWriter() {
        this(new BasicTypesConverter(), new ManualBeanFieldAnalyzer());
    }

    public List<FormElement> toForm(String prefix, Class<?> entityClass) {
        return toForm(prefix, entityClass, null);
    }

    public List<FormElement> toForm(String prefix, Object root) {
            return toForm(prefix, root.getClass(), root);
    }
    
    private List<FormElement> toForm(final String prefix, Class<?> entityClass, Object root) {
        try {
            ArrayList<FormElement> form = new ArrayList<FormElement>();
            
            for (ExtendedField extendedField : getFields(entityClass, root)) {
                final Field field = extendedField.field;
                field.setAccessible(true);
                if (typesConverter.canConvert(field.getType())) {
                    form.add(convertBasicType(prefix, root, extendedField));
                } else if (Collection.class.isAssignableFrom(field.getType()) && root != null) {
                    String extendedPrefix = extendPrefix(prefix, extendedField);
                    for (Object collectionObject : (Collection<?>) field.get(root)) {
                        form.addAll(toForm(extendedPrefix, collectionObject));
                    }
                } else if (Enum.class.isAssignableFrom(field.getType())) {
                    form.add(convertEnum(prefix, root, extendedField));
                } else {
                    String extendedPrefix = extendPrefix(prefix, extendedField);
                    form.addAll(toForm(extendedPrefix, field.getType(), root != null ? field.get(root) : null));
                }
            }
            return form;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e); 
        }
    }

    public FormElement toFormElement(String prefix, Object value) {
        Class<?> valueClass;
        String convertedValue = "";
        
        if (value.getClass() == Class.class) {
            valueClass = (Class<?>) value;
        } else {
            valueClass = value.getClass();
            convertedValue = typesConverter.convert(value);
        }
        
        if (!typesConverter.canConvert(valueClass)) throw new RuntimeException("Use the toForm() methods to convert " + valueClass);
        
        FormElement formElement;
        if (booleanConverter.canConvert(valueClass)) {
            formElement = new ChoiceElement(new FieldName(prefix), convertedValue, new String[] {"true"}, true);
        } else {
            formElement = new SimpleFormElement(new FieldName(prefix), StandardInputTypes.TEXT, convertedValue, false);
        }
        return formElement;
    }

    private ChoiceElement convertEnum(final String prefix, Object root, ExtendedField extendedField) throws Exception {
        Field field = extendedField.field;
        Enum<?>[] enumValues = (Enum[]) field.getType().getMethod("values").invoke(null);
        String[] choices = new String[enumValues.length];
        
        for (int i = 0; i < enumValues.length; i++) {
            choices[i] = enumValues[i].toString();
        }
        
        String selectedChoice = EMPTY;
        if (root != null && field.get(root) != null) {
            selectedChoice = field.get(root).toString();
        }
        String extendedPrefix = extendPrefix(prefix, extendedField);
        
        return new ChoiceElement(new FieldName(extendedPrefix), selectedChoice, choices, fieldAnalyzer.isRequired(field, root));
    }

    private FormElement convertBasicType(final String prefix, Object root, ExtendedField extendedField) throws IllegalAccessException {
        Field field = extendedField.field;
        String extendedPrefix = extendPrefix(prefix, extendedField);
        String convertedValue = convertValue(field, extendedField.fieldEntity);
        if (booleanConverter.canConvert(field.getType())) {
            return new ChoiceElement(new FieldName(extendedPrefix), convertedValue, new String[] {"true"}, fieldAnalyzer.isRequired(field, root));
        }
        return new SimpleFormElement(new FieldName(extendedPrefix), fieldAnalyzer.chooseType(field, root), convertedValue, fieldAnalyzer.isRequired(field, root));
    }

    private String convertValue(Field field, Object root) throws IllegalAccessException {
        if ((root == null || field.get(root) == null) && typesConverter.isBoolean(field.getType())) return typesConverter.convert(FALSE);
        if (root == null) return EMPTY;
        return typesConverter.convert(field.get(root));
    }

    private List<ExtendedField> getFields(Class<? extends Object> entityClass, Object entity) {
        try {
            List<Class<?>> classHierarchy = fieldAnalyzer.getClassHierarchy(entityClass);
            List<ExtendedField> fields = new ArrayList<ExtendedField>();
            for (Class<?> classInHierarchy : classHierarchy) {
                for (Field declaredField : classInHierarchy.getDeclaredFields()) {
                    InclusionMode inclusionMode = fieldAnalyzer.isIncluded(declaredField, entity != null ? entity : entityClass);
                    if (inclusionMode == InclusionMode.FULL) {
                        fields.add(new ExtendedField(declaredField, entity));
                    } else if (inclusionMode == InclusionMode.ID_ONLY) {
                        HandlesJpaRelationships idOnlyFieldAnalyzer = (HandlesJpaRelationships) fieldAnalyzer;
                        fields.addAll(idOnlyFieldAnalyzer.handleIdOnly(declaredField, entity));
                    }
                }
            }
            return fields;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private String extendPrefix(String oldFilter, ExtendedField extendedField) {
        return (isNotEmpty(oldFilter) ? oldFilter + "." : "") + (isNotEmpty(extendedField.prefix) ? extendedField.prefix + "." : "") + extendedField.field.getName();
    }
    
    public static class ExtendedField {
        public final Field field;
        public final String prefix;
        public final Object fieldEntity;
        
        private ExtendedField(Field field, Object fieldEntity) {
            this(field, fieldEntity, "");
        }
        
        public ExtendedField(Field field, Object fieldEntity, String prefix) {
            this.field = field;
            this.prefix = prefix;
            this.fieldEntity = fieldEntity;
        }
    }
}
