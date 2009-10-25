package formaliser.html;

public class PlainHtmlTemplates implements HtmlTemplates {

    private final String lineDecorator;
    
    public PlainHtmlTemplates(String lineDecorator) {
        this.lineDecorator = lineDecorator;
    }
    
    public PlainHtmlTemplates() {
        this.lineDecorator = null;
    }

    @Override
    public String getLabelTemplate() {
        return "<label for=\"${id}\">${label}${required}</label> ";
    }

    @Override
    public String getLineDecorator() {
        return lineDecorator;
    }

    @Override
    public String getOptionTemplate() {
        return "<option${selectedOption}>${value}</option>";
    }

    @Override
    public String getRequiredToken() {
        return "*";
    }

    @Override
    public String getSelectTemplate() {
        return "<select id=\"${id}\" name=\"${name}\">${options}</select>";
    }

    @Override
    public String getTextInputTemplate() {
        return "<input type=\"${type}\" id=\"${id}\" name=\"${name}\" value=\"${value}\"${extras}/>";
    }
}
