package formaliser.html;

import static org.apache.commons.lang.StringUtils.*;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang.text.StrSubstitutor;

import formaliser.data.ChoiceElement;
import formaliser.data.FormElement;
import formaliser.helpers.StandardInputTypes;

public class HtmlWriter {
    
    private HtmlTemplates htmlTemplates = new PlainHtmlTemplates(null);

    public String write(List<? extends FormElement> formElements) {
        StrBuilder formBuilder = new StrBuilder();
        for (FormElement formElement : formElements) {
            formBuilder.append(write(formElement)).append("\n");
        }
        return formBuilder.delete(formBuilder.length() - 1, formBuilder.length()).toString();
    }

    public String write(FormElement formElement) {
        HashMap<String, String> templateValues = new HashMap<String, String>();
        StrSubstitutor strSubstitutor = new StrSubstitutor(templateValues);
        
        templateValues.put("id", formElement.getName().toId());
        templateValues.put("name", formElement.getName().toString());
        templateValues.put("value", formElement.getValue());
        templateValues.put("label", formElement.getName().toShortName());
        templateValues.put("type", "text");
        templateValues.put("extras", EMPTY);
        templateValues.put("required", formElement.isRequired() ? htmlTemplates.getRequiredToken() : EMPTY);
        String labelText = strSubstitutor.replace(htmlTemplates.getLabelTemplate());
        
        if (formElement.getType() == StandardInputTypes.CHECKBOX) {
            ChoiceElement checkbox = (ChoiceElement) formElement;
            templateValues.put("type", "checkbox");
            templateValues.put("value", checkbox.choices[0]);
            if (checkbox.isSelected(checkbox.choices[0])) {
                templateValues.put("extras", " checked=\"checked\"");
            }
        }
        if (formElement.getType() == StandardInputTypes.HIDDEN) {
            labelText = EMPTY;
            templateValues.put("type", "hidden");
        }
        String inputText = strSubstitutor.replace(htmlTemplates.getTextInputTemplate());
        
        if (formElement.getType() == StandardInputTypes.CHOICE) {
            StringBuilder options = new StringBuilder();
            ChoiceElement choiceElement = (ChoiceElement) formElement;
            for (String choice : choiceElement.choices) {
                templateValues.put("selectedOption", choiceElement.isSelected(choice) ? " selected=\"selected\"" : EMPTY);
                templateValues.put("value", choice);
                options.append(strSubstitutor.replace(htmlTemplates.getOptionTemplate()));
            }
            templateValues.put("options", options.toString());
            inputText = strSubstitutor.replace(htmlTemplates.getSelectTemplate());
        }
        
        String formElementHtml = labelText + inputText;
        if (isNotEmpty(htmlTemplates.getLineDecorator())) {
            templateValues.put("formElement", formElementHtml);
            formElementHtml = strSubstitutor.replace(htmlTemplates.getLineDecorator());
        }
        return formElementHtml;
    }
    
    public HtmlWriter setHtmlTemplates(HtmlTemplates htmlTemplates) {
        this.htmlTemplates = htmlTemplates;
        return this;
    }

}
