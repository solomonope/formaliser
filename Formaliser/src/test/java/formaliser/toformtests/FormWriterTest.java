package formaliser.toformtests;

import static formaliser.testutils.TestUtils.*;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;


import formaliser.data.ChoiceElement;
import formaliser.data.FieldName;
import formaliser.data.FormElement;
import formaliser.data.SimpleFormElement;
import formaliser.forms.FormWriter;
import formaliser.helpers.DateConverter;
import formaliser.helpers.ManualBeanFieldAnalyzer;
import formaliser.testutils.TestClasses.BasicTypesEntity;
import formaliser.testutils.TestClasses.GraphElement;
import formaliser.testutils.TestClasses.GraphRoot;
import formaliser.testutils.TestClasses.NullableEntity;
import formaliser.testutils.TestClasses.SimpleGraph;
import formaliser.testutils.TestClasses.WithEnum;
import formaliser.testutils.TestClasses.WithRelationships;
import formaliser.testutils.TestClasses.WithTransient;
import formaliser.testutils.TestClasses.WithEnum.AnEnum;

public class FormWriterTest {
    
    private FormWriter writer = new FormWriter();

    @Test
    public void writes_simple_class() {
        List<FormElement> form = writer.toForm("", NullableEntity.class);
        
        assertThat(form).containsExactly(reqText("name"), reqText("number"));
    }
    
    @Test
    public void writes_empty_form() {
        List<FormElement> form = writer.toForm("", BasicTypesEntity.class);
        
        
        SimpleFormElement nameElement = reqText("name");
        SimpleFormElement ageElement = reqText("age");
        SimpleFormElement birthDateElement = reqText("birthdate");
        ChoiceElement marriedElement = reqBoolChoice("married", false);
        ChoiceElement hasCarElement = reqBoolChoice("hasCar", false);
        
        assertThat(form).containsOnly(nameElement, ageElement, birthDateElement, marriedElement, hasCarElement);
    }
    
    @Test
    public void fills_values() {
        final Date now = new Date();
        BasicTypesEntity entity = new BasicTypesEntity();
        entity.birthdate = now;
        entity.age = 52L;
        entity.hasCar = Boolean.FALSE;
        entity.married = true;
        entity.name = "his name";
        
        List<FormElement> form = writer.toForm("", entity);
        
        assertThat(form).containsExactly(reqText("name", "his name"), reqText("age", "52"), reqText("birthdate", new DateConverter().convert(now)), reqBoolChoice("married", true), reqBoolChoice("hasCar", false));
        assertThat(((ChoiceElement) form.get(3)).isSelected("true")).isTrue();
        assertThat(((ChoiceElement) form.get(4)).isSelected("true")).isFalse();
    }

    @Test
    public void converts_enum_to_choice() {
        List<FormElement> form = writer.toForm("", WithEnum.class);
        
        assertThat(form).containsExactly(new ChoiceElement(new FieldName("anEnum"), "", new String[] { "FIRST", "SECOND"}, true));
        
    }
    
    @Test
    public void uses_enum_value_as_selected_value() {
        WithEnum withEnum = new WithEnum();
        withEnum.anEnum = WithEnum.AnEnum.SECOND;
        
        List<FormElement> form = writer.toForm("aPrefix", withEnum);
        
        assertThat(form).containsExactly(new ChoiceElement(new FieldName("aPrefix.anEnum"), WithEnum.AnEnum.SECOND.toString(), new String[] { "FIRST", "SECOND"}, true));
        ChoiceElement choice = (ChoiceElement) form.get(0);
        assertThat((choice).isSelected("SECOND")).isTrue();
        assertThat(choice.isSelected("FIRST")).isFalse();
    }
    
    @Test
    public void no_selected_value_if_enum_is_null() {
        List<FormElement> form = writer.toForm("", new WithEnum());

        ChoiceElement choice = (ChoiceElement) form.get(0);
        
        for (AnEnum anEnum : AnEnum.values()) {
            assertThat(choice.isSelected(anEnum.name())).isFalse();
        }
        assertThat(choice.value).isEqualTo(EMPTY);
    }
    
    @Test
    public void uses_initial_prefix() {
        List<FormElement> form = writer.toForm("myPrefix", NullableEntity.class);
        
        assertThat(form).containsExactly(reqText("myPrefix.name"), reqText("myPrefix.number"));
    }
    
    @Test
    public void uses_parent_field_as_prefix() {
        List<FormElement> form = writer.toForm("", SimpleGraph.class);
        
        assertThat(form).containsExactly(reqText("element.name"), reqText("name"));
    }
    
    @Test
    public void skips_collections_for_empty_form() {
        List<FormElement> form = writer.toForm("", WithRelationships.class);
        
        assertThat(form).containsExactly(reqText("withRelationshipsId", ""), reqText("manyToOne.name"), reqText("manyToOne.number"));
    }
    
    @Test
    public void replaces_null_fields_with_empty_string() {
        NullableEntity entity = new NullableEntity();
        
        List<FormElement> elements = writer.toForm("", entity);
        
        for (FormElement element : elements) {
            assertThat(((SimpleFormElement) element).value).isEmpty();
        }
    }
    
    @Test
    public void navigates_graph_of_instance() {
        GraphRoot root = new GraphRoot();
        root.rootName = "root name";
        root.atRoot = new GraphElement();
        root.atRoot.name = "single element name";
        
        root.rootElements = new ArrayList<GraphElement>();
        GraphElement listElement1 = new GraphElement();
        listElement1.name = "list element 1";
        root.rootElements.add(listElement1);
        GraphElement listElement2 = new GraphElement();
        listElement2.name = "listElement 2";
        root.rootElements.add(listElement2);
        
        List<FormElement> form = writer.toForm("", root);
        
        assertThat(form).containsExactly(reqText("atRoot.name", root.atRoot.name), reqText("rootElements.name", listElement1.name), reqText("rootElements.name", listElement2.name), reqText("rootName", root.rootName));
    }
    
    @Test
    public void includes_superclass_fields() {
        ExtendedGraphElement root = new ExtendedGraphElement();
        root.name = "super name";
        root.extendedName = "subclass name";
        
        List<FormElement> form = writer.toForm("", root);
        
        assertThat(form).containsExactly(reqText("name", "super name"), reqText("extendedName", "subclass name"));
    }
    
    @Test
    public void excludes_specific_fields() {
        writer = writer.analyzeFieldsWith(new ManualBeanFieldAnalyzer().exclude(BasicTypesEntity.class, "name", "birthdate", "married", "hasCar"));
        
        List<FormElement> form = writer.toForm("", BasicTypesEntity.class);
        
        assertThat(form).containsExactly(reqText("age"));
    }
    
    @Test
    public void excludes_specific_collection() {
        GraphRoot root = new GraphRoot();
        root.rootName = "root name";
        root.atRoot = new GraphElement();
        root.atRoot.name = "single element name";
        
        root.rootElements = new ArrayList<GraphElement>();
        GraphElement listElement1 = new GraphElement();
        listElement1.name = "list element 1";
        root.rootElements.add(listElement1);
        GraphElement listElement2 = new GraphElement();
        listElement2.name = "listElement 2";
        root.rootElements.add(listElement2);
        
        List<FormElement> form = writer.analyzeFieldsWith(new ManualBeanFieldAnalyzer().exclude(GraphRoot.class, "rootElements")).toForm("", root);
        
        assertThat(form).containsExactly(reqText("atRoot.name", root.atRoot.name), reqText("rootName", root.rootName));
    }
    
    @Test
    public void excludes_transient_fields() {
        WithTransient withTransient = new WithTransient();
        withTransient.name = "something";
        
        List<FormElement> form = writer.toForm("", WithTransient.class);
        form.addAll(writer.toForm("", withTransient));
        
        assertThat(form).isEmpty();
    }
    
    private static class ExtendedGraphElement extends GraphElement {
        public String extendedName; 
    }
}
