package com.langtoun.oastypes.impl;

import static com.langtoun.oastypes.util.StringExtensions.doubleQuote;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;

import java.util.List;
import java.util.stream.Collectors;

import com.langtoun.oastypes.OASNumberType;
import com.langtoun.oastypes.OASType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Implementation of {@link OASNumberType} that extends the {@link OASType}
 * base class.
 */
// @Format-Off
public class OASNumberTypeImpl extends OASNumericTypeImpl implements OASNumberType {
  // members computed from the model
  private boolean isDouble;

  /**
   * Private constructor for an {@link OASNumberTypeImpl} base object. Objects must be
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
   *          The {@link Schema} that will be used to build the {@link OASNumberTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   */
  private OASNumberTypeImpl(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    super(parent, mappedName, schemaType, schema, reference);
    // members computed from the model
    this.isDouble = format() == null || "double".equals(format());
  }

  @Override
  public boolean isDouble() {
    return isDouble;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append(super.toString());

    if (reference() == null) {
      if (multipleOf != null) {
        sb.append(",").append(doubleQuote(escapeJson("multipleOf"))).append(":")
        .append(isDouble ? multipleOf.doubleValue() : multipleOf.floatValue());
      }
      if (minimum != null) {
        sb.append(",").append(doubleQuote(escapeJson("minimum"))).append(":")
          .append(isDouble ? minimum.doubleValue() : minimum.floatValue());
      }
      if (maximum != null) {
        sb.append(",").append(doubleQuote(escapeJson("maximum"))).append(":")
          .append(isDouble ? maximum.doubleValue() : maximum.floatValue());
      }
      sb.append(toStringCommon(sb));
    }

    sb.append("}");
    return sb.toString();
  }

  /**
   * Create a new builder for an {@link OASNumberTypeImpl} object.
   *
   * @param parent
   *          The parent of this type.
   * @param mappedName
   *          The name of the node that a schema is attached to. For top level
   *          schemas this is the name of the type.
   * @param schemaType
   *          The resolved schema type name.
   * @param schema
   *          The {@link Schema} that will be used to build the {@link OASNumberTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    return new Builder(parent, mappedName, schemaType, schema, reference);
  }

  public static class Builder {
    private final OASNumberTypeImpl oasNumberType;
    private final Schema schema;

    private Builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
      oasNumberType = new OASNumberTypeImpl(parent, mappedName, schemaType, schema, reference);
      this.schema = schema;
    }

    public Builder minimum() {
      final Number minimum = schema.getMinimum();
      if (minimum != null) {
        oasNumberType.minimum = oasNumberType.isDouble ? Double.valueOf(minimum.doubleValue()) : Float.valueOf(minimum.floatValue());
      }
      return this;
    }

    public Builder maximum() {
      final Number maximum = schema.getMaximum();
      if (maximum != null) {
        oasNumberType.maximum = oasNumberType.isDouble ? Double.valueOf(maximum.doubleValue()) : Float.valueOf(maximum.floatValue());
      }
      return this;
    }

    public Builder exclusiveMinimum() {
      oasNumberType.exclusiveMinimum = schema.getExclusiveMinimum();
      return this;
    }

    public Builder exclusiveMaximum() {
      oasNumberType.exclusiveMaximum = schema.getExclusiveMaximum();
      return this;
    }

    public Builder multipleOf() {
      final Number multipleOf = schema.getMultipleOf();
      if (multipleOf != null) {
        oasNumberType.multipleOf = oasNumberType.isDouble ? Double.valueOf(multipleOf.doubleValue()) : Float.valueOf(multipleOf.floatValue());
      }
      return this;
    }

    public Builder enums() {
      List<Object> enums = schema.getEnums();
      oasNumberType.enums =
        enums.stream()
          .map(n -> oasNumberType.isDouble ? Double.valueOf(n.toString()) : Float.valueOf(n.toString()))
          .collect(Collectors.toList());
      return this;
    }

    public OASNumberType build() {
      return oasNumberType;
    }
  }

}
