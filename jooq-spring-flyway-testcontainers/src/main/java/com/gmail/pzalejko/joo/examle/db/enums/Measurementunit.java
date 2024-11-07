/*
 * This file is generated by jOOQ.
 */
package com.gmail.pzalejko.joo.examle.db.enums;


import com.gmail.pzalejko.joo.examle.db.Dev;

import org.jooq.Catalog;
import org.jooq.EnumType;
import org.jooq.Schema;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public enum Measurementunit implements EnumType {

    CM("CM");

    private final String literal;

    private Measurementunit(String literal) {
        this.literal = literal;
    }

    @Override
    public Catalog getCatalog() {
        return getSchema().getCatalog();
    }

    @Override
    public Schema getSchema() {
        return Dev.DEV;
    }

    @Override
    public String getName() {
        return "measurementunit";
    }

    @Override
    public String getLiteral() {
        return literal;
    }

    /**
     * Lookup a value of this EnumType by its literal. Returns
     * <code>null</code>, if no such value could be found, see {@link
     * EnumType#lookupLiteral(Class, String)}.
     */
    public static Measurementunit lookupLiteral(String literal) {
        return EnumType.lookupLiteral(Measurementunit.class, literal);
    }
}