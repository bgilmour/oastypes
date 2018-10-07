package com.langtoun.oastypes.impl;

import static com.langtoun.oastypes.util.StringExtensions.doubleQuote;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;

import java.util.List;
import java.util.stream.Collectors;

import com.langtoun.oastypes.OASStringType;
import com.langtoun.oastypes.OASType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Implementation of {@link OASStringType} that extends the {@link OASType}
 * base class.
 */
// @Format-Off
public class OASStringTypeImpl extends OASTypeImpl implements OASStringType {
  // members extracted from the model
  private Integer minLength;
  private Integer maxLength;
  private String pattern;
  private List<String> enums;
  // members computed from the model
  private boolean hasEnums;

  /**
   * Private constructor for an {@link OASStringTypeImpl} base object. Objects must be
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
   *          The {@link Schema} that will be used to build the {@link OASStringTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   */
  private OASStringTypeImpl(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    super(parent, mappedName, schemaType, schema, reference);
    // members computed from the model
    this.hasEnums = schema.hasEnums();
  }

  @Override
  public Integer minLength() {
    return minLength;
  }

  @Override
  public Integer maxLength() {
    return maxLength;
  }

  @Override
  public String pattern() {
    return pattern;
  }

  @Override
  public List<String> enums() {
    return enums;
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

    if (reference() == null) {
      if (minLength != null) {
        sb.append(",").append(doubleQuote(escapeJson("minLength"))).append(":")
          .append(minLength.toString());
      }
      if (maxLength != null) {
        sb.append(",").append(doubleQuote(escapeJson("maxLength"))).append(":")
          .append(maxLength.toString());
      }
      if (pattern != null) {
        sb.append(",").append(doubleQuote(escapeJson("pattern"))).append(":")
          .append(doubleQuote(escapeJson(pattern.toString())));
      }
      if (hasEnums) {
        sb.append(",").append(doubleQuote(escapeJson("enum"))).append(":")
          .append(enums.stream().map(s -> doubleQuote(escapeJson(s))).collect(Collectors.joining(",", "[", "]")));
      }
    }

    sb.append("}");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    int hash = super.hashCode();
    hash = 37 * hash + (minLength != null ? minLength.hashCode() : 0);
    hash = 37 * hash + (maxLength != null ? maxLength.hashCode() : 0);
    hash = 37 * hash + (pattern != null ? pattern.hashCode() : 0);
    hash = 37 * hash + enums.hashCode();
    return hash;
  }

  /**
   * Create a new builder for an {@link OASStringTypeImpl} object.
   *
   * @param parent
   *          The parent of this type.
   * @param mappedName
   *          The name of the node that a schema is attached to. For top level
   *          schemas this is the name of the type.
   * @param schemaType
   *          The resolved schema type name.
   * @param schema
   *          The {@link Schema} that will be used to build the {@link OASStringTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    return new Builder(parent, mappedName, schemaType, schema, reference);
  }

  public static class Builder {
    private final OASStringTypeImpl oasStringType;
    private final Schema schema;

    private Builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
      oasStringType = new OASStringTypeImpl(parent, mappedName, schemaType, schema, reference);
      this.schema = schema;
    }

    public Builder minLength() {
      oasStringType.minLength = schema.getMinLength();
      return this;
    }

    public Builder maxLength() {
      oasStringType.maxLength = schema.getMaxLength();
      return this;
    }

    public Builder pattern() {
      oasStringType.pattern = schema.getPattern();
      return this;
    }

    public Builder enums() {
      final List<Object> enums = schema.getEnums();
      oasStringType.enums =
        enums.stream()
          .map(Object::toString)
          .collect(Collectors.toList());
      return this;
    }

    public OASStringType build() {
      return oasStringType;
    }
  }

}
