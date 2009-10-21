package com.formaliser.helpers.html;

import com.formaliser.data.ChoiceElement;
import com.formaliser.data.FormElement;
import com.formaliser.helpers.StandardInputTypes;

public class HtmlWriter {

    public String write(FormElement formElement) {
        String id = formElement.getName().toId();
        String name = formElement.getName().toString();
        String value = formElement.getValue();
        String label = formElement.getName().toShortName();
        String htmlType = "text";
        String extras = "";
        
        if (formElement.getType() == StandardInputTypes.CHECKBOX) {
            ChoiceElement checkbox = (ChoiceElement) formElement;
            htmlType = "checkbox";
            value = checkbox.choices[0];
            if (checkbox.isSelected(value)) {
                extras = " checked=\"checked\"";
            }
        }
        
        return ("<label for=\"" + id + "\">" + label + (formElement.isRequired() ? "*" : "") + "</label> ")
            + "<input type=\"" + htmlType + "\" id=\"" + id + "\" name=\"" + name + "\" value=\"" + value + "\"" + extras + "/>";
    }

}
