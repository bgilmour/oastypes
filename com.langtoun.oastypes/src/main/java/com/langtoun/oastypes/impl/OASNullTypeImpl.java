package com.langtoun.oastypes.impl;

import com.langtoun.oastypes.OASNullType;
import com.langtoun.oastypes.OASType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Implementation of {@link OASNullType} that extends the {@link OASType}
 * base class.
 */
// @Format-Off
public class OASNullTypeImpl extends OASUnfacetedTypeImpl implements OASNullType {

  /**
   * Private constructor for an {@link OASNullTypeImpl} base object. Objects must be
   * created using the builder.
   *
   * @param parent
   *          The parent of this type.
   * @param mappedName
   *          The name of the node that a schema is attached to. For top level
   *          schemas this is the name of the type.
   * @param schemaType
   *          The resolved schema type name.
   * @param schema
   *          The {@link Schema} that will be used to build the {@link OASNullTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   */
  private OASNullTypeImpl(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    super(parent, mappedName, schemaType, schema, reference);
  }

  /**
   * Create a new builder for an {@link OASNullTypeImpl} object.
   *
   * @param parent
   *          The parent of this type.
   * @param mappedName
   *          The name of the node that a schema is attached to. For top level
   *          schemas this is the name of the type.
   * @param schemaType
   *          The resolved schema type name.
   * @param schema
   *          The {@link Schema} that will be used to build the {@link OASNullTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    return new Builder(parent, mappedName, schemaType, schema, reference);
  }

  public static class Builder {
    private final OASNullTypeImpl oasNullType;

    private Builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
      oasNullType = new OASNullTypeImpl(parent, mappedName, schemaType, schema, reference);
    }

    public OASNullType build() {
      return oasNullType;
    }
  }

}
