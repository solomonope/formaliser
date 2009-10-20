package com.formaliser.testutils;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

public class TestClasses {
    
    public static class BasicTypesEntity {
        @Column(nullable = false)
        public String name;
        @Column(nullable = false)
        public Long age;
        @Column(nullable = false)
        public Date birthdate;
        @Column(nullable = false)
        public boolean married;
        @Column(nullable = false)
        public Boolean hasCar;
    }
    
    public static class OptionalEntity {
        public String implicitlyOptional;
        
        @Column
        public String explicitlyOptionalColumn;
        @Basic
        public String explicitlyOptionalBasic;

        @Column(nullable = false)
        public String notOptionalColumn;
        @Basic(optional = false)
        public String notOptionalBasic;
    }
    
    public static class NonInsertableEntity {
        @Id @GeneratedValue
        public Long id;
        
        @Column(insertable = false)
        public String notInsertable;
        
        
    }
    
    public static class NullableEntity {
        @Id
        public String name;
        public Long number;
    }
    
    public static class WithRelationships {
        @Id @GeneratedValue
        public Long withRelationshipsId;
        @ManyToOne
        public NullableEntity manyToOne;
        @OneToMany
        public Set<NonInsertableEntity> oneToMany;
        @ManyToMany
        public List<WithGeneratedId> manyToMany;
    }
    
    public static class WithEnum {
        public enum AnEnum { FIRST, SECOND }
        
        public AnEnum anEnum;
    }
    
    public static class WithGeneratedId {
        @Id @GeneratedValue
        public Long id;
        
        @Column(nullable = false)
        public String name;
    }
    
    public static class GraphRoot {
        public GraphElement atRoot;
        public List<GraphElement> rootElements;
        public String rootName;
    }
    
    public static class SimpleGraph {
        public GraphElement element;
        public String name;
    }
    
    public static class GraphElement {
        public String name;
    }
    
    public static class CompositeGraphRoot {
        public GraphRoot graphRoot;
        public List<CompositeGraphRoot> composite;
    }
    
    public static class WithPrivateNoArgsConstructor {
        public String name;
        
        private WithPrivateNoArgsConstructor() {}
    }
    
    public static class WithPrimitives {
        public String aString;
        public boolean bool;
        public byte aByte;
        public short aShort;
        public int anInt;
        public long aLong;
        public float aFloat;
        public double aDouble;
    }
    
    public static class WithPrivateFields {
        private String name;
    }

    public static class WithTransient {
        public transient String name;
    }
    
    @Entity
    public static class WithEmbeddable {
        public String name;
        public AnEmbeddable embedded;
        public WithPrivateFields aPrivate;
    }
    
    @Embeddable
    public static class AnEmbeddable {
        public String embeddedName;
        public Long embeddedLong;
    }
    
    @Entity
    public static class WithTwoLevelsOfRelationships {
        @Id @GeneratedValue
        public Long id;
        @Column(nullable = false)
        public String name;
        @ManyToOne
        public WithRelationships withRelationships;
    }
    
    private TestClasses() {}
}
