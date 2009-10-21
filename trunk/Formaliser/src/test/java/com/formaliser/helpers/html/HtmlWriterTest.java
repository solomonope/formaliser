package com.formaliser.helpers.html;

import static com.formaliser.testutils.TestUtils.*;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

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
}
