package formaliser.toformtests;

import static formaliser.helpers.StandardInputTypes.*;
import static formaliser.testutils.TestUtils.*;
import static org.fest.assertions.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;


import formaliser.data.FormElement;
import formaliser.forms.FormWriter;
import formaliser.testutils.TestClasses;
import formaliser.testutils.TestClasses.AnEmbeddable;
import formaliser.testutils.TestClasses.NonInsertableEntity;
import formaliser.testutils.TestClasses.NullableEntity;
import formaliser.testutils.TestClasses.OptionalEntity;
import formaliser.testutils.TestClasses.WithEmbeddable;
import formaliser.testutils.TestClasses.WithGeneratedId;
import formaliser.testutils.TestClasses.WithRelationships;
import formaliser.testutils.TestClasses.WithTwoLevelsOfRelationships;

public class FormWriterInJpaConfigurationTest {

    private FormWriter writer = new FormWriter().forJpa();
    
    @Test
    public void ignores_generated_value_field_for_empty_form() {
        List<FormElement> form = writer.toForm("", WithGeneratedId.class);
        
        assertThat(form).containsExactly(reqText("name"));
    }
    
    @Test
    public void generated_value_field_for_filled_in_form_written_to_hidden_field() {
        WithGeneratedId entity = new WithGeneratedId();
        entity.id = 5L;
        entity.name = "my name";
        
        List<FormElement> form = writer.toForm("myEntity", entity);
        
        
        assertThat(form).containsExactly(req(HIDDEN, "myEntity.id", "5"), req(TEXT, "myEntity.name", "my name"));
    }
    
    @Test
    public void recognises_jpa_optional_and_required_fields() {
        List<FormElement> form = writer.toForm("", OptionalEntity.class);
        
        assertThat(form.size()).isEqualTo(5);
        assertThat(form).contains(optText("implicitlyOptional"), optText("explicitlyOptionalColumn"), optText("explicitlyOptionalBasic"));
        assertThat(form).contains(reqText("notOptionalColumn"), reqText("notOptionalBasic"));
    }
    
    @Test
    public void excludes_non_insertable_fields() {
        List<FormElement> form = writer.toForm("", NonInsertableEntity.class);
        
        assertThat(form).isEmpty();
    }
    
    @Test
    public void includes_embeddables() {
        AnEmbeddable embeddable = new TestClasses.AnEmbeddable();
        embeddable.embeddedName = "e name";
        embeddable.embeddedLong = 52L;
        
        WithEmbeddable withEmbeddable = new TestClasses.WithEmbeddable();
        withEmbeddable.name = "top name";
        withEmbeddable.embedded = embeddable;
        
        List<FormElement> form = writer.toForm("", withEmbeddable);
        
        assertThat(form).containsExactly(optText("name", "top name"), optText("embedded.embeddedName", "e name"), optText("embedded.embeddedLong", "52"));
    }
    
    @Test
    public void includes_class_and_id_of_relationship() {
        WithRelationships withRelationships = new WithRelationships();
        withRelationships.withRelationshipsId = 1L;
        withRelationships.manyToOne = new NullableEntity();
        withRelationships.manyToOne.name = "name ID";
        withRelationships.oneToMany = new HashSet<NonInsertableEntity>();
        NonInsertableEntity inCollection = new NonInsertableEntity();
        inCollection.id = 2L;
        withRelationships.oneToMany.add(inCollection);
        
        List<FormElement> relationships = writer.toForm("", withRelationships);
        
        assertThat(relationships).containsExactly(req(HIDDEN, "withRelationshipsId", "1"), reqText("manyToOne.name", "name ID"), req(HIDDEN, "oneToMany.id", "2"));
    }
    
    @Test
    public void only_includes_top_level_relationships() {
        WithTwoLevelsOfRelationships root = new WithTwoLevelsOfRelationships();
        
        root.id = 1L;
        root.name = "root name";
        root.withRelationships = new WithRelationships();
        root.withRelationships.withRelationshipsId = 3L;
        root.withRelationships.manyToOne = new NullableEntity();
        root.withRelationships.manyToOne.name = "2nd level";
        
        List<FormElement> form = writer.toForm("", root);
        
        assertThat(form).containsExactly(req(HIDDEN, "id", "1"), reqText("name", "root name"), req(HIDDEN, "withRelationships.withRelationshipsId", "3"));
    }

}
