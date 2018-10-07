package com.langtoun.oastypes.impl;

import com.langtoun.oastypes.OASType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Base class that extends {@link OASTypeImpl} for simple types with no additional
 * facets.
 */
// @Format-Off
public class OASUnfacetedTypeImpl extends OASTypeImpl {

  /**
   * Private constructor for an {@link OASUnfacetedTypeImpl} base object. Objects must be
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
   *          The {@link Schema} that will be used to build the {@link OASUnfacetedTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   */
  protected OASUnfacetedTypeImpl(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
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

}
