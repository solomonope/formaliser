package formaliser.data;

import java.util.Arrays;

import formaliser.helpers.StandardInputTypes;

public class ChoiceElement implements FormElement {

    private static final String[] CHECKBOX_CHOICES = new String[] {"true"};
    
    public final FieldName name;
    public final String value;
    public final String[] choices;
    public final boolean required;

    public ChoiceElement(FieldName name, String value, String[] choices, boolean required) {
        this.name = name;
        this.value = value;
        this.required = required;
        this.choices = choices;
    }
    
    public ChoiceElement(FieldName name, String value, boolean required) {
        this.name = name;
        this.value = value;
        this.required = required;
        this.choices = CHECKBOX_CHOICES;
    }
    
    public boolean isSelected(String choice) {
        return value.equals(choice);
    }

    @Override
    public InputType getType() {
        if (Arrays.equals(choices, CHECKBOX_CHOICES)) return StandardInputTypes.CHECKBOX;
        return StandardInputTypes.CHOICE;
    }

    @Override
    public String getValue() {
        return value;
    }
    
    @Override
    public FieldName getName() {
        return name;
    }
    
    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public String toString() {
        return "ChoiceElement [name=" + name + ", value=" + value + ", choices=" + Arrays.toString(choices) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(choices);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ChoiceElement other = (ChoiceElement) obj;
        return required == other.required && name.equals(other.name) && value.equals(other.value) && Arrays.equals(choices, other.choices);
    }
    
    
    
}
