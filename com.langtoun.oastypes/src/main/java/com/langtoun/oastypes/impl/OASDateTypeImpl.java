package com.langtoun.oastypes.impl;

import com.langtoun.oastypes.OASDateType;
import com.langtoun.oastypes.OASType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Implementation of {@link OASDateType} that extends the {@link OASType}
 * base class.
 */
public class OASDateTypeImpl extends OASTypeImpl implements OASDateType {

  /**
   * Private constructor for an {@link OASDateTypeImpl} base object. Objects must be
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
   *          The {@link Schema} that will be used to build the {@link OASDateTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   */
  private OASDateTypeImpl(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    super(parent, mappedName, schemaType, schema, reference);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();

    sb.append("{");
    sb.append(super.toString());

    sb.append("}");

    return sb.toString();
  }

  /**
   * Create a new builder for an {@link OASDateTypeImpl} object.
   *
   * @param parent
   *          The parent of this type.
   * @param mappedName
   *          The name of the node that a schema is attached to. For top level
   *          schemas this is the name of the type.
   * @param schemaType
   *          The resolved schema type name.
   * @param schema
   *          The {@link Schema} that will be used to build the {@link OASDateTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    return new Builder(parent, mappedName, schemaType, schema, reference);
  }

  public static class Builder {
    private final OASDateTypeImpl oasDateType;

    private Builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
      oasDateType = new OASDateTypeImpl(parent, mappedName, schemaType, schema, reference);
    }

    public OASDateType build() {
      return oasDateType;
    }
  }

}
