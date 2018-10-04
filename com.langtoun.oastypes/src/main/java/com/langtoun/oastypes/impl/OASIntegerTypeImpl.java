package com.langtoun.oastypes.impl;

import static com.langtoun.oastypes.util.StringExtensions.doubleQuote;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;

import java.util.List;
import java.util.stream.Collectors;

import com.langtoun.oastypes.OASIntegerType;
import com.langtoun.oastypes.OASType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Implementation of {@link OASIntegerType} that extends the {@link OASType}
 * base class.
 */
public class OASIntegerTypeImpl extends OASNumericTypeImpl implements OASIntegerType {
  // members computed from the model
  private boolean isLong;

  /**
   * Private constructor for an {@link OASIntegerTypeImpl} base object. Objects must be
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
   *          The {@link Schema} that will be used to build the {@link OASIntegerTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   */
  private OASIntegerTypeImpl(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    super(parent, mappedName, schemaType, schema, reference);
    // members computed from the model
    this.isLong = format() == null || "int64".equals(format());
  }

  @Override
  public boolean isLong() {
    return isLong;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();

    sb.append("{");
    sb.append(super.toString());

    if (minimum != null) {
      sb.append(",").append(doubleQuote(escapeJson("minimum"))).append(":")
        .append(isLong ? minimum.longValue() : minimum.intValue());
    }
    if (maximum != null) {
      sb.append(",").append(doubleQuote(escapeJson("maximum"))).append(":")
        .append(isLong ? maximum.longValue() : maximum.intValue());
    }
    if (exclusiveMinimum != null) {
      sb.append(",").append(doubleQuote(escapeJson("exclusiveMinimum"))).append(":")
        .append(exclusiveMinimum.toString());
    }
    if (exclusiveMaximum != null) {
      sb.append(",").append(doubleQuote(escapeJson("exclusiveMaximum"))).append(":")
        .append(exclusiveMaximum.toString());
    }
    if (multipleOf != null) {
      sb.append(",").append(doubleQuote(escapeJson("multipleOf"))).append(":")
        .append(isLong ? multipleOf.longValue() : multipleOf.intValue());
    }
    if (hasEnums) {
      sb.append(",").append(doubleQuote(escapeJson("enum"))).append(":")
        .append(enums.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]")));
    }

    sb.append("}");

    return sb.toString();
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Create a new builder for an {@link OASIntegerTypeImpl} object.
   *
   * @param parent
   *          The parent of this type.
   * @param mappedName
   *          The name of the node that a schema is attached to. For top level
   *          schemas this is the name of the type.
   * @param schemaType
   *          The resolved schema type name.
   * @param schema
   *          The {@link Schema} that will be used to build the {@link OASIntegerTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    return new Builder(parent, mappedName, schemaType, schema, reference);
  }

  public static class Builder {
    private final OASIntegerTypeImpl oasIntegerType;
    private final Schema schema;

    private Builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
      oasIntegerType = new OASIntegerTypeImpl(parent, mappedName, schemaType, schema, reference);
      this.schema = schema;
    }

    public Builder minimum() {
      final Number minimum = schema.getMinimum();
      if (minimum != null) {
        oasIntegerType.minimum = oasIntegerType.isLong ? Long.valueOf(minimum.longValue()) : Integer.valueOf(minimum.intValue());
      }
      return this;
    }

    public Builder maximum() {
      final Number maximum = schema.getMaximum();
      if (maximum != null) {
        oasIntegerType.maximum = oasIntegerType.isLong ? Long.valueOf(maximum.longValue()) : Integer.valueOf(maximum.intValue());
      }
      return this;
    }

    public Builder exclusiveMinimum() {
      oasIntegerType.exclusiveMinimum = schema.getExclusiveMinimum();
      return this;
    }

    public Builder exclusiveMaximum() {
      oasIntegerType.exclusiveMaximum = schema.getExclusiveMaximum();
      return this;
    }

    public Builder multipleOf() {
      final Number multipleOf = schema.getMultipleOf();
      if (multipleOf != null) {
        oasIntegerType.multipleOf = oasIntegerType.isLong ? Long.valueOf(multipleOf.longValue()) : Integer.valueOf(multipleOf.intValue());
      }
      return this;
    }

    public Builder enums() {
      final List<Object> enums = schema.getEnums();
      oasIntegerType.enums = enums.stream().map(n -> oasIntegerType.isLong ? Long.valueOf(n.toString()) : Integer.valueOf(n.toString())).collect(Collectors.toList());
      return this;
    }

    public OASIntegerType build() {
      return oasIntegerType;
    }
  }

}
