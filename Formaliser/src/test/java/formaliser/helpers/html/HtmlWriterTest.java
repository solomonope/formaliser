package formaliser.helpers.html;

import static formaliser.testutils.TestUtils.*;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.moandjiezana.formaliser.data.ChoiceElement;
import com.moandjiezana.formaliser.data.FieldName;
import com.moandjiezana.formaliser.helpers.StandardInputTypes;
import com.moandjiezana.formaliser.html.HtmlWriter;
import com.moandjiezana.formaliser.html.PlainHtmlTemplates;


public class HtmlWriterTest {
    private HtmlWriter writer = new HtmlWriter();

    @Test
    public void writes_empty_optional_text_field() {
        String html = writer.write(optText("field.name"));
        
        assertThat(html).isEqualTo("<label for=\"fieldName\">name</label> <input type=\"text\" id=\"fieldName\" name=\"field.name\" value=\"\"/>");
    }

    @Test
    public void writes_optional_text_field_with_value() {
        String html = writer.write(optText("field.name2", "a value"));
        
        assertThat(html).isEqualTo("<label for=\"fieldName2\">name2</label> <input type=\"text\" id=\"fieldName2\" name=\"field.name2\" value=\"a value\"/>");
    }
    
    @Test
    public void writes_empty_required_text_field() {
        String html = writer.write(reqText("field.reqName"));
        
        assertThat(html).isEqualTo("<label for=\"fieldReqName\">reqName*</label> <input type=\"text\" id=\"fieldReqName\" name=\"field.reqName\" value=\"\"/>");
    }
    
    @Test
    public void writes_unchecked_checkbox() {
        String html = writer.write(reqBoolChoice("chk", false));
        
        assertThat(html).isEqualTo("<label for=\"chk\">chk*</label> <input type=\"checkbox\" id=\"chk\" name=\"chk\" value=\"true\"/>");
    }
    
    @Test
    public void writes_checked_checkbox() {
        String html = writer.write(reqBoolChoice("chk", true));
        
        assertThat(html).isEqualTo("<label for=\"chk\">chk*</label> <input type=\"checkbox\" id=\"chk\" name=\"chk\" value=\"true\" checked=\"checked\"/>");
    }
    
    @Test
    public void writes_hidden_field_with_value() {
        String html = writer.write(req(StandardInputTypes.HIDDEN, "h.i", "i.h"));
        
        assertThat(html).isEqualTo("<input type=\"hidden\" id=\"hI\" name=\"h.i\" value=\"i.h\"/>");
    }
    
    @Test
    public void writes_multiple_choice_as_select() {
        String html = writer.write(new ChoiceElement(new FieldName("choice.name"), "", new String[] {"c1", "c2", "c3"}, true));
        
        assertThat(html).isEqualTo("<label for=\"choiceName\">name*</label> <select id=\"choiceName\" name=\"choice.name\"><option>c1</option><option>c2</option><option>c3</option></select>");
    }
    
    @Test
    public void writes_multiple_choice_with_selected_value() {
        String html = writer.write(new ChoiceElement(new FieldName("choice.name"), "c2", new String[] {"c1", "c2", "c3"}, true));
        
        assertThat(html).isEqualTo("<label for=\"choiceName\">name*</label> <select id=\"choiceName\" name=\"choice.name\"><option>c1</option><option selected=\"selected\">c2</option><option>c3</option></select>");
    }
    
    @Test
    public void writes_several_elements_at_once() {
        String html = writer.write(Arrays.asList(reqText("f1"), reqText("f2", "2")));
        
        assertThat(html).isEqualTo(writer.write(reqText("f1")) + "\n" + writer.write(reqText("f2", "2")));
    }
    
    @Test
    public void req_token_can_be_customised() {
        String expectedForm = writer.write(reqText("reqField")).replace("*", " -*-");
        writer.setHtmlTemplates(new PlainHtmlTemplates() {
            @Override
            public String getRequiredToken() {
                return " -*-";
            }
        });
        String html = writer.write(reqText("reqField"));
        
        assertThat(html).isEqualTo(expectedForm);
    }
    
    @Test
    public void input_text_template_can_be_customised() {
        PlainHtmlTemplates customHtmlTemplates = new PlainHtmlTemplates() {
            @Override
            public String getTextInputTemplate() {
                return "<${name} input>";
            }
        };

        String html = writer.setHtmlTemplates(customHtmlTemplates).write(reqText("weird"));
        assertThat(html).endsWith("<weird input>");
    }
    
    @Test
    public void can_decorate_full_line() {
        PlainHtmlTemplates customHtmlTemplates = new PlainHtmlTemplates("startExtra${formElement}endExtra");

        String expected = "startExtra" + writer.write(reqText("decorated")) + "endExtra";
        
        String html = writer.setHtmlTemplates(customHtmlTemplates).write(reqText("decorated"));
        
        assertThat(html).isEqualTo(expected);
    }
}
