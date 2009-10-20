package com.formaliser.data;

public class SimpleFormElement implements FormElement {
    public final FieldName field;
    private final InputType type;
    public final String value;
    public final boolean required;
    
    public SimpleFormElement(FieldName field, InputType type, String value, boolean required) {
        this.field = field;
        this.type = type;
        this.value = value;
        this.required = required;
    }

    @Override
    public InputType getType() {
        return type;
    }
    
    @Override
    public String getValue() {
        return value;
    }
    
    @Override
    public FieldName getName() {
        return field;
    }
    
    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + field.hashCode();
        result = prime * result + (required ? 1231 : 1237);
        result = prime * result + type.hashCode();
        result = prime * result + value.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        SimpleFormElement other = (SimpleFormElement) obj;
        return field.equals(other.field) && required == other.required && type.equals(other.type) && value.equals(other.value);
    }

    @Override
    public String toString() {
        return "FormElement [type=" + type + ", field=" + field + ", value=" + value + " required=" + required + "]";
    }
    
}
