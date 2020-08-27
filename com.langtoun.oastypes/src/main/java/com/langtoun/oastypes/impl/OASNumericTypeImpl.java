package com.langtoun.oastypes.impl;

import static com.langtoun.oastypes.util.StringExtensions.doubleQuote;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;

import java.util.List;
import java.util.stream.Collectors;

import com.langtoun.oastypes.OASNumericType;
import com.langtoun.oastypes.OASType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Implementation of {@link OASNumericType} that extends the {@link OASType}
 * base class and is a superclass of the numeric types.
 */
// @Format-Off
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

  protected StringBuilder toStringCommon(final StringBuilder sb) {
    if (reference() == null) {
      if (hasEnums) {
        sb.append(',').append(doubleQuote(escapeJson("enum"))).append(':') //$NON-NLS-1$
          .append(enums.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
      if (exclusiveMinimum != null) {
        sb.append(',').append(doubleQuote(escapeJson("exclusiveMinimum"))).append(':') //$NON-NLS-1$
          .append(exclusiveMinimum.toString());
      }
      if (exclusiveMaximum != null) {
        sb.append(',').append(doubleQuote(escapeJson("exclusiveMaximum"))).append(':') //$NON-NLS-1$
          .append(exclusiveMaximum.toString());
      }
    }

    return sb;
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other);
  }

  @Override
  public int hashCode() {
    int hash = super.hashCode();
    hash = 37 * hash + (minimum != null ? minimum.hashCode() : 0);
    hash = 37 * hash + (maximum != null ? maximum.hashCode() : 0);
    hash = 37 * hash + (exclusiveMinimum != null ? exclusiveMinimum.hashCode() : 0);
    hash = 37 * hash + (exclusiveMaximum != null ? exclusiveMaximum.hashCode() : 0);
    hash = 37 * hash + (multipleOf != null ? multipleOf.hashCode() : 0);
    hash = 37 * hash + enums.hashCode();
    return hash;
  }

}
