package com.langtoun.oastypes.impl;

import static com.langtoun.oastypes.util.StringExtensions.doubleQuote;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;

import java.util.List;
import java.util.stream.Collectors;

import com.langtoun.oastypes.OASIntegerType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

public class OASIntegerTypeImpl extends OASTypeImpl implements OASIntegerType {
  // members extracted from the model
  private Number minimum;
  private Number maximum;
  private Boolean exclusiveMinimum;
  private Boolean exclusiveMaximum;
  private List<Number> enums;
  // members computed from the model
  private boolean isLong;
  private boolean isExclusiveMinimum;
  private boolean isExclusiveMaximum;
  private boolean hasEnums;

  private OASIntegerTypeImpl(final Schema schema, final Reference reference) {
    super(schema, reference);
    // members computed from the model
    this.isLong = format() == null || "int64".equals(format());
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
    if (hasEnums) {
      sb.append(",").append(doubleQuote(escapeJson("enum"))).append(":")
        .append(enums.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]")));
    }

    sb.append("}");

    return sb.toString();
  }

  /**
   * Create a new builder for an {@link OASIntegerTypeImpl} object.
   *
   * @param schema  The {@link Schema} that will be used to build the {@link OASIntegerTypeImpl}.
   * @param reference  A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final Schema schema, final Reference reference) {
    return new Builder(schema, reference);
  }

  public static class Builder {
    private final OASIntegerTypeImpl oasIntegerType;
    private final Schema schema;

    private Builder(final Schema schema, final Reference reference) {
      oasIntegerType = new OASIntegerTypeImpl(schema, reference);
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