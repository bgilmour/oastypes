package com.langtoun.oastypes.impl;

import java.util.List;

import com.langtoun.oastypes.OASNumericType;
import com.langtoun.oastypes.OASType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

public class OASNumericTypeImpl extends OASTypeImpl implements OASNumericType {
  // members extracted from the model
  protected Number minimum;
  protected Number maximum;
  protected Boolean exclusiveMinimum;
  protected Boolean exclusiveMaximum;
  protected Number multipleOf;
  protected List<Number> enums;
  // members computed from the model
  protected boolean isExclusiveMinimum;
  protected boolean isExclusiveMaximum;
  protected boolean hasEnums;

  /**
   * Protected constructor for an {@link OASNumericTypeImpl} base object.
   *
   * @param parent
   *          The parent of this type.
   * @param mappedName
   *          The name of the node that a schema is attached to. For top level
   *          schemas this is the name of the type.
   * @param schemaType
   *          The resolved schema type name.
   * @param schema
   *          The {@link Schema} that will be used to build the {@link OASNumericTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   */
  protected OASNumericTypeImpl(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    super(parent, mappedName, schemaType, schema, reference);
    // members computed from the model
    this.isExclusiveMinimum = schema.isExclusiveMinimum();
    this.isExclusiveMaximum = schema.isExclusiveMaximum();
    this.hasEnums = schema.hasEnums();
  }

  @Override
  public Number minimum() {
    return minimum;
  }

  @Override
  public Number maximum() {
    return maximum;
  }

  @Override
  public Boolean exclusiveMinimum() {
    return exclusiveMinimum;
  }

  @Override
  public Boolean exclusiveMaximum() {
    return exclusiveMaximum;
  }

  @Override
  public Number multipleOf() {
    return multipleOf;
  }

  @Override
  public List<Number> enums() {
    return enums;
  }

  @Override
  public boolean isExclusiveMinimum() {
    return isExclusiveMinimum;
  }

  @Override
  public boolean isExclusiveMaximum() {
    return isExclusiveMaximum;
  }

  @Override
  public boolean hasEnums() {
    return hasEnums;
  }

}
