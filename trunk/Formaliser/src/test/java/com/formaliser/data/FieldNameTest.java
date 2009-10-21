package com.formaliser.data;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FieldNameTest {

	@Test 
	public void toString_replaces_underscore_with_dot() {
		assertEquals("Dish.name", new FieldName("Dish.name").toString());
		
	}
	
	@Test
	public void toShortName_removes_class_name() {
		assertEquals("name", new FieldName("Venue.name").toShortName());
	}
    
    @Test
    public void toShortName_returns_full_name_if_class_name_not_present() {
        assertEquals("name", new FieldName("name").toShortName());
    }
	
	@Test
	public void toLabel_pretty_prints_the_field() {
		assertEquals("Display name", new FieldName("User.displayName").toLabel());
	}

	@Test
	public void toId_removes_dot_and_capitalises_following_letter() {
		assertEquals("VenueAddress", new FieldName("Venue.address").toId());
	}
	
	@Test
	public void toId_with_argument_appends_dash_id() {
		assertEquals("UserDisplayName-1", new FieldName("User.displayName").toId(1L));
	}
	
	@Test
	public void toMessage_pretty_prints_class_and_field() {
		assertEquals("user display name", new FieldName("User.displayName").toMessage());
	}
	
	@Test
    public void toRoot_gets_prefix() {
        assertThat(new FieldName("testEntity.id").toRoot()).isEqualTo("testEntity");
    }
}
