package formaliser.toformtests;

import static formaliser.testutils.TestUtils.optText;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.moandjiezana.formaliser.data.FormElement;
import com.moandjiezana.formaliser.forms.FormWriter;


import formaliser.testutils.TestClasses.BasicTypesEntity;

public class FormElementWriterTest {
    
    private FormWriter writer = new FormWriter();
    
    @Test
    public void writes_basic_types_directly() {
        FormElement formElement = writer.toFormElement("basic", "value");
        
        assertThat(formElement).isEqualTo(optText("basic", "value"));
    }
    
    @Test
    public void writes_empty_field_when_given_a_class() {
        FormElement formElement = writer.toFormElement("empty", String.class);
        
        assertThat(formElement).isEqualTo(optText("empty"));
    }
    
    @Test(expected = RuntimeException.class)
    public void fails_if_not_basic_type() {
        writer.toFormElement("notBasic", new BasicTypesEntity());
    }

}
