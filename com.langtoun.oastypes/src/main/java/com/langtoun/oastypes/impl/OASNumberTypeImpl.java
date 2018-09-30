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
public class OASNumberTypeImpl extends OASTypeImpl implements OASNumberType {
  // members extracted from the model
  private Number minimum;
  private Number maximum;
  private Boolean exclusiveMinimum;
  private Boolean exclusiveMaximum;
  private List<Number> enums;
  // members computed from the model
  private boolean isDouble;
  private boolean isExclusiveMinimum;
  private boolean isExclusiveMaximum;
  private boolean hasEnums;

  private OASNumberTypeImpl(final Schema schema, final Reference reference) {
    super(schema, reference);
    // members computed from the model
    this.isDouble = format() == null || "double".equals(format());
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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();

    sb.append("{");
    sb.append(super.toString());

    if (minimum != null) {
      sb.append(",").append(doubleQuote(escapeJson("minimum"))).append(":")
        .append(isDouble ? minimum.doubleValue() : minimum.floatValue());
    }
    if (maximum != null) {
      sb.append(",").append(doubleQuote(escapeJson("maximum"))).append(":")
        .append(isDouble ? maximum.doubleValue() : maximum.floatValue());
    }
    if (exclusiveMinimum != null) {
      sb.append(",").append(doubleQuote(escapeJson("exclusiveMinimum"))).append(":")
        .append(exclusiveMinimum.toString());
    }
    if (exclusiveMaximum != null) {
      sb.append(",").append(doubleQuote(escapeJson("exclusiveMaximum"))).append(":")
        .append(exclusiveMaximum.toString());
    }
    if (hasEnums) {
      sb.append(",").append(doubleQuote(escapeJson("enum"))).append(":")
        .append(enums.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]")));
    }

    sb.append("}");

    return sb.toString();
  }

  /**
   * Create a new builder for an {@link OASNumberTypeImpl} object.
   *
   * @param schema  The {@link Schema} that will be used to build the {@link OASNumberTypeImpl}.
   * @param reference  A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final Schema schema, final Reference reference) {
    return new Builder(schema, reference);
  }

  public static class Builder {
    private final OASNumberTypeImpl oasNumberType;
    private final Schema schema;

    private Builder(final Schema schema, final Reference reference) {
      oasNumberType = new OASNumberTypeImpl(schema, reference);
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
