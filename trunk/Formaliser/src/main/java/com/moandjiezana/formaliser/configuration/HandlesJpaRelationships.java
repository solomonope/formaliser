package com.moandjiezana.formaliser.configuration;

import java.lang.reflect.Field;
import java.util.List;

import com.moandjiezana.formaliser.forms.FormWriter.ExtendedField;


public interface HandlesJpaRelationships {

    List<ExtendedField> handleIdOnly(Field declaredField, Object entity);
}
