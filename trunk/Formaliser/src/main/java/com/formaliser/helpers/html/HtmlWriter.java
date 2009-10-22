package com.formaliser.helpers.html;

import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang.text.StrSubstitutor;

import com.formaliser.data.ChoiceElement;
import com.formaliser.data.FormElement;
import com.formaliser.helpers.StandardInputTypes;

public class HtmlWriter {

    private String labelTemplate = "<label for=\"${id}\">${label}${required}</label> ";
    private String selectTemplate = "<select id=\"${id}\" name=\"${name}\">${options}</select>";
    private String requiredToken = "*";
    private String inputTextTemplate = "<input type=\"${type}\" id=\"${id}\" name=\"${name}\" value=\"${value}\"${extras}/>";
    private String optionTemplate = "<option${selectedOption}>${value}</option>";
    private String resultDecorationTemplate;

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
        templateValues.put("required", formElement.isRequired() ? getRequiredToken() : EMPTY);
        String labelText = strSubstitutor.replace(getLabeltemplate());
        
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
        String inputText = strSubstitutor.replace(getInputTextTemplate());
        
        if (formElement.getType() == StandardInputTypes.CHOICE) {
            StringBuilder options = new StringBuilder();
            ChoiceElement choiceElement = (ChoiceElement) formElement;
            for (String choice : choiceElement.choices) {
                templateValues.put("selectedOption", choiceElement.isSelected(choice) ? " selected=\"selected\"" : EMPTY);
                templateValues.put("value", choice);
                options.append(strSubstitutor.replace(getOptionTemplate()));
            }
            templateValues.put("options", options.toString());
            inputText = strSubstitutor.replace(getSelecttemplate());
        }
        
        String formElementHtml = labelText + inputText;
        if (resultDecorationTemplate != null) {
            templateValues.put("formElement", formElementHtml);
            formElementHtml = strSubstitutor.replace(resultDecorationTemplate);
        }
        return formElementHtml;
    }

    private String getRequiredToken() {
        return requiredToken;
    }
    
    private String getInputTextTemplate() {
        return inputTextTemplate;
    }

    private String getSelecttemplate() {
        return selectTemplate;
    }

    private String getLabeltemplate() {
        return labelTemplate;
    }

    private String getOptionTemplate() {
        return optionTemplate;
    }

    public HtmlWriter setRequiredToken(String customRequiredToken) {
        this.requiredToken  = customRequiredToken;
        return this;
    }

    public HtmlWriter setInputTextTemplate(String template) {
        this.inputTextTemplate  = template;
        return this;
    }

    /**
     * Use ${formElement} to represent the output of the full conversion.
     * @param template null by default.
     */
    public HtmlWriter setResultDecorationTemplate(String template) {
        this.resultDecorationTemplate = template;
        return this;
    }

}
