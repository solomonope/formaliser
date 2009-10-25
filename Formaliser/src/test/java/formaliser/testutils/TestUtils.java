package formaliser.testutils;


import formaliser.data.ChoiceElement;
import formaliser.data.FieldName;
import formaliser.data.SimpleFormElement;
import formaliser.helpers.StandardInputTypes;

public class TestUtils {


    public static SimpleFormElement createRequiredElement(String name, StandardInputTypes type) {
        return new SimpleFormElement(new FieldName(name), type, "", true);
    }

    public static SimpleFormElement req(StandardInputTypes type, String name, String value) {
        return new SimpleFormElement(new FieldName(name), type, value, true);
    }

    public static SimpleFormElement reqText(String name) {
        return createRequiredElement(name, StandardInputTypes.TEXT);
    }
    
    public static SimpleFormElement reqText(String name, String value) {
        return new SimpleFormElement(new FieldName(name), StandardInputTypes.TEXT, value, true);
    }
    
    public static SimpleFormElement optElement(String name, StandardInputTypes type) {
        return new SimpleFormElement(new FieldName(name), type, "", false);
    }

    public static SimpleFormElement opt(StandardInputTypes type, String name, String value) {
        return new SimpleFormElement(new FieldName(name), type, value, false);
    }

    public static SimpleFormElement optText(String name) {
        return new SimpleFormElement(new FieldName(name), StandardInputTypes.TEXT, "", false);
    }

    public static SimpleFormElement optText(String name, String value) {
        return new SimpleFormElement(new FieldName(name), StandardInputTypes.TEXT, value, false);
    }

    public static ChoiceElement choice(String name, String value, String[] choices, boolean required) {
        return new ChoiceElement(new FieldName(name), value, choices, required);
    }

    public static ChoiceElement reqBoolChoice(String name, boolean value) {
        return new ChoiceElement(new FieldName(name), Boolean.toString(value), new String[] {"true"}, true);
    }
    
    private TestUtils() {}
}
