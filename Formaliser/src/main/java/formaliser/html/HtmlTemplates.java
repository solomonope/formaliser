package formaliser.html;

import org.apache.commons.lang.text.StrSubstitutor;

/**
 * The templates {@link HtmlWriter} uses to generate HTML.
 * 
 * The templates must use the syntax of Apache Commons Lang's {@link StrSubstitutor}.
 */
public interface HtmlTemplates {

    String getLabelTemplate();
    String getRequiredToken();
    String getTextInputTemplate();
    String getSelectTemplate();
    String getOptionTemplate();
    
    /**
     * Used to decorate the result of the transformation to HTML.
     * The template can use ${formElement}
     * Example: put the form element in a list or a table row. 
     * @return a template, or an empty String if none is used.
     */
    String getLineDecorator();
    
}
