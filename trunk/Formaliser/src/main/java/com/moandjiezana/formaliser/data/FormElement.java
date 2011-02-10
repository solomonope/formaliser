package com.moandjiezana.formaliser.data;

public interface FormElement {

    InputType getType();
    String getValue();
    FieldName getName();
    boolean isRequired();
}
