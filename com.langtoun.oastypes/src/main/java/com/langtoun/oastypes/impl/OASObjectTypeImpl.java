package com.langtoun.oastypes.impl;

import static com.langtoun.oastypes.util.CollectorUtil.toLinkedMap;
import static com.langtoun.oastypes.util.OverlayUtil.getReference;
import static com.langtoun.oastypes.util.StringExtensions.doubleQuote;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.langtoun.oastypes.OASObjectType;
import com.langtoun.oastypes.OASType;
import com.langtoun.oastypes.util.OASTypeFactory;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Implementation of {@link OASObjectType} that extends the {@link OASType}
 * base class.
 */
public class OASObjectTypeImpl extends OASTypeImpl implements OASObjectType {
  // members extracted from the model
  private List<String> required;
  private Map<String, OASType> properties;
  private Integer minProperties;
  private Integer maxProperties;
  // members computed from the model
  private boolean hasRequired;
  private boolean hasProperties;

  /**
   * Private constructor for an {@link OASObjectTypeImpl} base object. Objects must be
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
   *          The {@link Schema} that will be used to build the {@link OASObjectTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   */
  private OASObjectTypeImpl(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    super(parent, mappedName, schemaType, schema, reference);
    // members computed from the model
    this.hasRequired = schema.hasRequiredFields();
    this.hasProperties = schema.hasProperties();
  }

  @Override
  public List<String> required() {
    return required;
  }

  @Override
  public boolean isRequired(final String propertyName) {
    return required.contains(propertyName);
  }

  @Override
  public Map<String, OASType> properties() {
    return properties;
  }

  @Override
  public Integer minProperties() {
    return minProperties;
  }

  @Override
  public Integer maxProperties() {
    return maxProperties;
  }

  @Override
  public boolean hasRequired() {
    return hasRequired;
  }

  @Override
  public boolean hasProperties() {
    return hasProperties;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append(super.toString());

    if (reference() == null) {
      if (hasRequired) {
        sb.append(",").append(doubleQuote(escapeJson("required"))).append(":");
        sb.append(required.stream().map(s -> doubleQuote(escapeJson(s))).collect(Collectors.joining(",", "[", "]")));
      }
      if (hasProperties) {
        sb.append(",").append(doubleQuote(escapeJson("properties"))).append(":");
        sb.append(properties.entrySet().stream().map(e -> String.format("\"%s\":%s", e.getKey(), e.getValue())).collect(Collectors.joining(",", "{", "}")));
      }
      if (minProperties != null) {
        sb.append(",").append(doubleQuote(escapeJson("minProperties"))).append(":")
          .append(minProperties.toString());
      }
      if (maxProperties != null) {
        sb.append(",").append(doubleQuote(escapeJson("maxProperties"))).append(":")
          .append(maxProperties.toString());
      }
    }

    sb.append("}");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    int hash = super.hashCode();
    hash = 37 * hash + required.hashCode();
    hash = 37 * hash + properties.hashCode();
    hash = 37 * hash + (minProperties != null ? minProperties.hashCode() : 0);
    hash = 37 * hash + (maxProperties != null ? maxProperties.hashCode() : 0);
    return hash;
  }

  /**
   * Create a new builder for an {@link OASObjectTypeImpl} object.
   *
   * @param parent
   *          The parent of this type.
   * @param mappedName
   *          The name of the node that a schema is attached to. For top level
   *          schemas this is the name of the type.
   * @param schemaType
   *          The resolved schema type name.
   * @param schema
   *          The {@link Schema} that will be used to build the {@link OASObjectTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   * @param visitedTypes
   *          A {@link Map} that tracks the visited schemas and the types that were created.
   *          This is used to guard against infinite recursion.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference, final Map<Schema, OASType> visitedTypes) {
    return new Builder(parent, mappedName, schemaType, schema, reference, visitedTypes);
  }

  public static class Builder {
    private final OASObjectTypeImpl oasObjectType;
    private final Schema schema;

    private Builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference, final Map<Schema, OASType> visitedTypes) {
      oasObjectType = new OASObjectTypeImpl(parent, mappedName, schemaType, schema, reference);
      this.schema = schema;
      visitedTypes.put(schema,  oasObjectType);
    }

    public Builder required() {
      oasObjectType.required =
        schema.getRequiredFields()
          .stream()
          .collect(Collectors.toList());
      return this;
    }

    public Builder properties(final Map<Schema, OASType> visitedTypes) {
      final Map<String, Schema> properties = schema.getProperties();
      oasObjectType.properties =
        properties.entrySet().stream()
          .map(e ->
            new AbstractMap.SimpleEntry<>(
              e.getKey(),
              OASTypeFactory.createOASType(oasObjectType, e.getKey(), e.getValue(), getReference(properties, e.getKey()), visitedTypes)
            )
          )
          .collect(toLinkedMap(Entry::getKey, Entry::getValue));
      return this;
    }

    public Builder minProperties() {
      oasObjectType.minProperties = schema.getMinProperties();
      return this;
    }

    public Builder maxProperties() {
      oasObjectType.maxProperties = schema.getMaxProperties();
      return this;
    }

    public OASObjectType build() {
      return oasObjectType;
    }
  }

}
