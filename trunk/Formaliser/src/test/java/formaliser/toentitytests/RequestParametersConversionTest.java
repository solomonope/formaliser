package formaliser.toentitytests;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.reflect.core.Reflection.field;

import java.text.DateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;


import formaliser.forms.FormReader;
import formaliser.testutils.TestClasses.BasicTypesEntity;
import formaliser.testutils.TestClasses.CompositeGraphRoot;
import formaliser.testutils.TestClasses.GraphRoot;
import formaliser.testutils.TestClasses.NullableEntity;
import formaliser.testutils.TestClasses.OptionalEntity;
import formaliser.testutils.TestClasses.WithPrimitives;
import formaliser.testutils.TestClasses.WithPrivateFields;
import formaliser.testutils.TestClasses.WithPrivateNoArgsConstructor;
import formaliser.testutils.TestClasses.WithRelationships;

public class RequestParametersConversionTest {

    private final FormReader converter = new FormReader();
    private final HashMap<String, String[]> parameters = new HashMap<String, String[]>();

    @Test
    public void fills_simple_object_without_prefix() {
        addParameter("name", "my name");
        addParameter("number", "15");
        
        NullableEntity nullableEntity = converter.readClass("", NullableEntity.class, parameters, 0);
        
        assertThat(nullableEntity.name).isEqualTo("my name");
        assertThat(nullableEntity.number).isEqualTo(15);
    }
    
    @Test
    public void fills_list_without_prefix() {
        addParameter("name", "n1", "n2", "n3", "n4");
        addParameter("number", "1", "2", "3", "4");
        
        Collection<Object> entities = converter.readCollection("", List.class, NullableEntity.class, parameters, 0);
        
        assertThat(entities).hasSize(4);
    }

    @Test
    public void fills_simple_object_with_prefix() {
        addParameter("NullableEntity.name", "my other name");
        addParameter("NullableEntity.number", "32");
        
        NullableEntity nullableEntity = converter.readClass("NullableEntity", NullableEntity.class, parameters, 0);
        
        assertThat(nullableEntity.name).isEqualTo("my other name");
        assertThat(nullableEntity.number).isEqualTo(32);
    }
    
    @Test
    public void gets_simple_object_from_request_parameters() throws Exception {
        addParameter("BasicTypesEntity.age", "5");
        addParameter("BasicTypesEntity.birthdate", "09/10/2009");
        addParameter("BasicTypesEntity.hasCar", "false");
        addParameter("BasicTypesEntity.married", "true");
        addParameter("BasicTypesEntity.name", "entity name");
        
        BasicTypesEntity entity = converter.readClass("BasicTypesEntity", BasicTypesEntity.class, parameters, 0);
        
        assertThat(entity.age).isEqualTo(5);
        assertThat(entity.birthdate).isEqualTo(DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).parse("09/10/2009"));
        assertThat(entity.hasCar).isEqualTo(false);
        assertThat(entity.married).isEqualTo(true);
        assertThat(entity.name).isEqualTo("entity name");
    }

    @Test
    public void fills_simple_object_with_custom_prefix() {
        addParameter("myThing.name", "my other name");
        addParameter("myThing.number", "32");
        
        NullableEntity nullableEntity = converter.readClass("myThing", NullableEntity.class, parameters, 0);
        
        assertThat(nullableEntity.name).isEqualTo("my other name");
        assertThat(nullableEntity.number).isEqualTo(32);
    }
    
    @Test
    public void fills_collection_of_objects_with_prefix() {
        addParameter("NullableEntity.name", "name1", "name 2", "name 3");
        addParameter("NullableEntity.number", "32", "16", "8");
        
        List<Object> entities = (List<Object>) converter.readCollection("NullableEntity", List.class, NullableEntity.class, parameters, 0);
        
        assertThat(entities).hasSize(3);
        assertThat(field("name").ofType(String.class).in(entities.get(0)).get()).isEqualTo("name1");
        assertThat(field("number").ofType(Long.class).in(entities.get(0)).get()).isEqualTo(32);
        
        assertThat(field("name").ofType(String.class).in(entities.get(1)).get()).isEqualTo("name 2");
        assertThat(field("number").ofType(Long.class).in(entities.get(1)).get()).isEqualTo(16);
        
        assertThat(field("name").ofType(String.class).in(entities.get(2)).get()).isEqualTo("name 3");
        assertThat(field("number").ofType(Long.class).in(entities.get(2)).get()).isEqualTo(8);
    }
    
    @Test
    public void fills_collection_in_element() {
        addParameter("GraphRoot.rootName", "root name");
        addParameter("GraphRoot.rootElements.name", "name 1", "name2", "name  3");
        addParameter("GraphRoot.atRoot.name", "element at root");
        
        GraphRoot entity = converter.readClass("GraphRoot", GraphRoot.class, parameters, 0);
        
        assertThat(entity.rootName).isEqualTo("root name");
        assertThat(entity.rootElements).hasSize(3);
        assertThat(entity.rootElements.get(0).name).isEqualTo("name 1");
        assertThat(entity.rootElements.get(1).name).isEqualTo("name2");
        assertThat(entity.rootElements.get(2).name).isEqualTo("name  3");
        assertThat(entity.atRoot.name).isEqualTo("element at root");
    }

    @Test
    public void fills_single_and_multi_valued_relationships_with_field_naming_scheme() {
        addParameter("manyToOne.name", "a name");
        addParameter("manyToOne.number", "3");
        
        addParameter("oneToMany.id", "1", "2", "3");
        addParameter("oneToMany.notInsertable", "value 1", "value 2", "value 3");

        addParameter("manyToMany.id", "4", "5");
        addParameter("manyToMany.name", "name 4", "name 5");
        
        WithRelationships entity = converter.readClass("", WithRelationships.class, parameters, 0);
        
        assertThat(entity.manyToOne.name).isEqualTo("a name");
        assertThat(entity.manyToOne.number).isEqualTo(3);
        
        assertThat(entity.oneToMany).hasSize(3);
        
        assertThat(entity.manyToMany).hasSize(2);
    }

    @Test
    @Ignore
    public void fills_single_and_multi_valued_relationships_with_class_naming_scheme() {
        addParameter("NullableEntity.name", "a name");
        addParameter("NullableEntity.number", "3");
        
        addParameter("NonInsertableEntity.id", "1", "2", "3");
        addParameter("NonInsertableEntity.notInsertable", "value 1", "value 2", "value 3");

        addParameter("WithGeneratedId.id", "4", "5");
        addParameter("WithGeneratedId.name", "name 4", "name 5");
        
        WithRelationships entity = converter.readClass("", WithRelationships.class, parameters, 0);
        
        assertThat(entity.manyToOne.name).isEqualTo("a name");
        assertThat(entity.manyToOne.number).isEqualTo(3);
        
        assertThat(entity.oneToMany).hasSize(3);
        
        assertThat(entity.manyToMany).hasSize(2);
    }

    @Test
    public void fills_graph_to_any_depth() {
        addParameter("CompositeGraphRoot.graphRoot.rootName", "a");
        addParameter("CompositeGraphRoot.graphRoot.atRoot.name", "sub-root element name");
        addParameter("CompositeGraphRoot.composite.graphRoot.rootName", "b", "c");
        addParameter("CompositeGraphRoot.composite.graphRoot.atRoot.name", "leaf b", "leaf c");
        
        CompositeGraphRoot composite = converter.readClass("CompositeGraphRoot", CompositeGraphRoot.class, parameters, 0);
        
        assertThat(composite.graphRoot.rootName).isEqualTo("a");
        assertThat(composite.graphRoot.atRoot.name).isEqualTo("sub-root element name");
        assertThat(composite.composite).hasSize(2);
        assertThat(composite.composite.get(0).graphRoot.rootName).isEqualTo("b");
        assertThat(composite.composite.get(0).graphRoot.atRoot.name).isEqualTo("leaf b");
        assertThat(composite.composite.get(1).graphRoot.rootName).isEqualTo("c");
        assertThat(composite.composite.get(1).graphRoot.atRoot.name).isEqualTo("leaf c");
    }
    
    @Test
    public void sets_missing_multi_valued_parameters_to_empty_collection() {
        addParameter("manyToOne.name", "name of mTO");
        
        WithRelationships entity = converter.readClass("", WithRelationships.class, parameters, 0);
        
        assertThat(entity.manyToOne.name).isEqualTo("name of mTO");
        assertThat((Object) entity.manyToOne.number).isNull();
        assertThat(entity.oneToMany).isEmpty();
        assertThat(entity.manyToMany).isEmpty();
    }
    
    @Test
    public void sets_missing_object_to_null() {
        NullableEntity entity = converter.readClass("", NullableEntity.class, parameters, 0);
        
        assertThat(entity).isNull();
    }
    
    @Test
    public void sets_missing_single_valued_parameters_to_null() {
        addParameter("OptionalEntity.notOptionalBasic", "");
        addParameter("OptionalEntity.notOptionalColumn", "");

        OptionalEntity entity = converter.readClass("", OptionalEntity.class, parameters, 0);
        
        assertThat(entity.explicitlyOptionalBasic).isNull();
        assertThat(entity.explicitlyOptionalColumn).isNull();
        assertThat(entity.implicitlyOptional).isNull();
        assertThat(entity.notOptionalBasic).isNull();
        assertThat(entity.notOptionalColumn).isNull();
    }
    
    @Test
    public void sets_missing_primitives_to_default_values() {
        addParameter("aString", "");
        
        WithPrimitives entity = converter.readClass("", WithPrimitives.class, parameters, 0);
        
        assertThat(entity.bool).isFalse();
        assertThat(entity.aByte).isEqualTo((byte) 0);
        assertThat(entity.aShort).isEqualTo((short) 0);
        assertThat(entity.anInt).isEqualTo(0);
        assertThat(entity.aLong).isEqualTo(0);
        assertThat(entity.aFloat).isEqualTo(0.0f);
        assertThat(entity.aDouble).isEqualTo(0.0f);
    }
    
    @Test
    public void fills_primitives() {
        addParameter("bool", "true");
        addParameter("aByte", "1");
        addParameter("aShort", "2");
        addParameter("anInt", "3");
        addParameter("aLong", "4");
        addParameter("aFloat", "5.1");
        addParameter("aDouble", "6.2");
        
        WithPrimitives entity = converter.readClass("", WithPrimitives.class, parameters, 0);
        
        assertThat(entity.bool).isTrue();
        assertThat(entity.aByte).isEqualTo((byte) 1);
        assertThat(entity.aShort).isEqualTo((short) 2);
        assertThat(entity.anInt).isEqualTo(3);
        assertThat(entity.aLong).isEqualTo(4);
        assertThat(entity.aFloat).isEqualTo(5.1f);
        assertThat(entity.aDouble).isEqualTo(6.2d);
    }
    
    @Test
    public void missing_boolean_field_set_to_false() {
        Boolean bool = (Boolean) converter.readParameter("bool", boolean.class, parameters, 0);
        
        assertThat(bool).isFalse();
    }
    
    @Test
    public void non_true_boolean_field_set_to_false() {
        addParameter("bool", "not true");
        
        Boolean bool = (Boolean) converter.readParameter("bool", Boolean.class, parameters, 0);
        assertThat(bool).isFalse();
    }
    
    @Test
    public void read_redirects_for_basic_types() {
        addParameter("aString", "a string");
        addParameter("aBool", "true");
        addParameter("aLong", "3");
        
        String value = converter.read("aString", String.class, parameters, 0);
        Boolean boolValue = converter.read("aBool", boolean.class, parameters, 0);
        Long longValue = converter.read("aLong", Long.class, parameters, 0);
        
        assertThat(value).isEqualTo("a string");
        assertThat(boolValue).isTrue();
        assertThat(longValue).isEqualTo(3);
    }
    
    @Test
    public void can_instantiate_from_private_no_args_constructor() {
        addParameter("name", "a name");
        
        WithPrivateNoArgsConstructor entity = converter.readClass("", WithPrivateNoArgsConstructor.class, parameters, 0);
        
        assertThat(entity.name).isEqualTo("a name");
    }
    
    @Test
    public void accesses_private_fields() {
        addParameter("WithPrivateFields.name", "a name");
        
        WithPrivateFields entity = converter.readClass("WithPrivateFields", WithPrivateFields.class, parameters, 0);
        
        assertThat(field("name").ofType(String.class).in(entity).get()).isEqualTo("a name");
    }
    
    public static class CollectionHolder {
        public String name;
        public List<NullableEntity> entities;
    }
    
    private void addParameter(String key, String... values) {
        parameters.put(key, values);
    }

}
