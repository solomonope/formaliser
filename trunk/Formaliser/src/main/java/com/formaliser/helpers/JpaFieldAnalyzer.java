package com.formaliser.helpers;

import static java.util.Collections.singletonList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.formaliser.configuration.FieldAnalyzer;
import com.formaliser.configuration.HandlesJpaRelationships;
import com.formaliser.configuration.InclusionMode;
import com.formaliser.data.InputType;
import com.formaliser.forms.FormWriter.ExtendedField;


public class JpaFieldAnalyzer implements FieldAnalyzer, HandlesJpaRelationships {
    
    private ManualBeanFieldAnalyzer manualAnalyzer = new ManualBeanFieldAnalyzer();
    
    private final Set<Class<?>> idOnly = new HashSet<Class<?>>();
    
    private final static BasicConvertibleClasses basicConvertibleClasses = new BasicConvertibleClasses();
    private final static Set<Class<? extends Annotation>> relationshipAnnotations = new HashSet<Class<? extends Annotation>>();
    
    static {
        relationshipAnnotations.add(OneToMany.class);
        relationshipAnnotations.add(ManyToMany.class);
        relationshipAnnotations.add(ManyToOne.class);
    }
    
    public JpaFieldAnalyzer includeIdOnlyFor(Class<?>... relatedEntities) {
        return new JpaFieldAnalyzer(relatedEntities);
    }
    
    public JpaFieldAnalyzer() {
        this(new Class<?>[0]);
    }
    
    private JpaFieldAnalyzer(Class<?>[] idOnly) {
        for (Class<?> aRelationship : idOnly) {
            this.idOnly.add(aRelationship);
        }
    }

    @Override
    public InclusionMode isIncluded(Field field, Object entity) {
        if (formNotEmpty(entity)) {
            if (basicConvertibleClasses.isConvertible(field.getType())) return InclusionMode.FULL;
            if (field.getType().isAnnotationPresent(Embeddable.class)) return InclusionMode.FULL;
            if (hasRelationshipAnnotation(field)) return InclusionMode.ID_ONLY;
            
            return InclusionMode.NONE;
        }
        if (field.isAnnotationPresent(GeneratedValue.class)) return InclusionMode.NONE;
        if (field.isAnnotationPresent(Transient.class)) return InclusionMode.NONE;
        if (hasRelationshipAnnotation(field)) return InclusionMode.NONE;
        if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).insertable()) return InclusionMode.NONE;
        
        return manualAnalyzer.isIncluded(field, entity);
    }

    private boolean formNotEmpty(Object entity) {
        return entity.getClass() != Class.class;
    }

    @Override
    public boolean isRequired(Field field, Object entity) {
        if (entity != null && isId(field)) return true;
        return field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).nullable()
                || field.isAnnotationPresent(Basic.class) && !field.getAnnotation(Basic.class).optional();
    }

    @Override
    public InputType chooseType(Field field, Object entity) {
        if (field.isAnnotationPresent(GeneratedValue.class)) return StandardInputTypes.HIDDEN;
        return StandardInputTypes.choose(field);
    }

    @Override
    public List<Class<?>> getClassHierarchy(Class<?> beanClass) {
        List<Class<?>> fullHierarchy = manualAnalyzer.getClassHierarchy(beanClass);
        List<Class<?>> filteredHierarchy = new ArrayList<Class<?>>();
        for (Class<?> potentialClass : fullHierarchy) {
            if (potentialClass == beanClass || isMapped(potentialClass)) filteredHierarchy.add(potentialClass);
        }
        return filteredHierarchy;
    }
    
    @Override
    public List<ExtendedField> handleIdOnly(Field entityField, Object entity) {
        try {
            entityField.setAccessible(true);
            Class<?> entityFieldType = entityField.getType();
            if (basicConvertibleClasses.isCollection(entityFieldType)) {
                return extractIds(entityField, entity);
            }
            return singletonList(new ExtendedField(getId(entityFieldType), entityField.get(entity), entityField.getName()));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<ExtendedField> extractIds(Field idOnlyEntitiesField, Object entity) throws IllegalAccessException {
        Collection<?> idOnlyEntities = (Collection<?>) idOnlyEntitiesField.get(entity);
        if (idOnlyEntities == null) idOnlyEntities = Collections.emptyList();
        
        Field idField = getId(basicConvertibleClasses.getGenericTypeOfCollection(idOnlyEntitiesField));
        ArrayList<ExtendedField> ids = new ArrayList<ExtendedField>();
        for (Object idOnlyEntity : idOnlyEntities) {
            ids.add(new ExtendedField(idField, idOnlyEntity, idOnlyEntitiesField.getName()));
        }
        return ids;
    }
    
    private Field getId(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (isId(field)) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new RuntimeException("No @Id found in " + entityClass.getName());
    }
    
    private boolean isId(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private boolean isMapped(Class<?> potentialClass) {
        return potentialClass.isAnnotationPresent(MappedSuperclass.class) || potentialClass.isAnnotationPresent(Entity.class);
    }

    private boolean hasRelationshipAnnotation(Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            if (relationshipAnnotations.contains(annotation.annotationType())) return true;
        }
        return false;
    }
}
