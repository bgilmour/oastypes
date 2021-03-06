package com.langtoun.oastypes.impl;

import static com.langtoun.oastypes.util.OverlayUtil.getReference;
import static com.langtoun.oastypes.util.StringExtensions.doubleQuote;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;

import java.util.Map;

import com.langtoun.oastypes.OASArrayType;
import com.langtoun.oastypes.OASType;
import com.langtoun.oastypes.util.OASTypeFactory;
import com.reprezen.jsonoverlay.Overlay;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Implementation of {@link OASArrayType} that extends the {@link OASType}
 * base class.
 */
// @Format-Off
public class OASArrayTypeImpl extends OASTypeImpl implements OASArrayType {
  // members extracted from the model
  private OASType items;
  private Integer minItems;
  private Integer maxItems;
  private Boolean uniqueItems;
  // members computed from the model
  private boolean isUniqueItems;

  /**
   * Private constructor for an {@link OASArrayTypeImpl} base object. Objects must be
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
   *          The {@link Schema} that will be used to build the {@link OASArrayTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   */
  private OASArrayTypeImpl(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    super(parent, mappedName, schemaType, schema, reference);
    // members computed from the model
    this.isUniqueItems = schema.isUniqueItems();
  }

  @Override
  public OASType items() {
    return items;
  }

  @Override
  public Integer minItems() {
    return minItems;
  }

  @Override
  public Integer maxItems() {
    return maxItems;
  }

  @Override
  public Boolean uniqueItems() {
    return uniqueItems;
  }

  @Override
  public boolean isUniqueItems() {
    return isUniqueItems;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append(super.toString());

    if (reference() == null) {
      if (items != null) {
        sb.append(",").append(doubleQuote(escapeJson("items"))).append(":")
          .append(items);
      }
      if (minItems != null) {
        sb.append(",").append(doubleQuote(escapeJson("minItems"))).append(":")
          .append(minItems.toString());
      }
      if (maxItems != null) {
        sb.append(",").append(doubleQuote(escapeJson("maxItems"))).append(":")
          .append(maxItems.toString());
      }
      if (uniqueItems != null) {
        sb.append(",").append(doubleQuote(escapeJson("uniqueItems"))).append(":")
          .append(uniqueItems.toString());
      }
    }

    sb.append("}");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    int hash = super.hashCode();
    hash = 37 * hash + (items != null  ? items.schema().hashCode() : 0);
    hash = 37 * hash + (minItems != null ? minItems.hashCode() : 0);
    hash = 37 * hash + (maxItems != null ? maxItems.hashCode() : 0);
    hash = 37 * hash + (uniqueItems != null ? uniqueItems.hashCode() : 0);
    return hash;
  }

  /**
   * Create a new builder for an {@link OASArrayTypeImpl} object.
   *
   * @param parent
   *          The parent of this type.
   * @param mappedName
   *          The name of the node that a schema is attached to. For top level
   *          schemas this is the name of the type.
   * @param schemaType
   *          The resolved schema type name.
   * @param schema
   *          The {@link Schema} that will be used to build the {@link OASArrayTypeImpl}.
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
    private final OASArrayTypeImpl oasArrayType;
    private final Schema schema;

    private Builder(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference, final Map<Schema, OASType> visitedTypes) {
      oasArrayType = new OASArrayTypeImpl(parent, mappedName, schemaType, schema, reference);
      this.schema = schema;
      visitedTypes.put(schema,  oasArrayType);
    }

    public Builder items(final Map<Schema, OASType> visitedTypes) {
      final Schema itemsSchema = schema.getItemsSchema();

      Overlay<Schema> itemsOverlay = Overlay.of(schema, "itemsSchema", Schema.class);
      Reference reference = getReference(itemsOverlay);

      oasArrayType.items = OASTypeFactory.createOASType(oasArrayType, "items", itemsSchema, reference, visitedTypes);
      return this;
    }

    public Builder minItems() {
      oasArrayType.minItems = schema.getMinItems();
      return this;
    }

    public Builder maxItems() {
      oasArrayType.maxItems = schema.getMaxItems();
      return this;
    }

    public Builder uniqueItems() {
      oasArrayType.uniqueItems = schema.getUniqueItems();
      return this;
    }

    public OASArrayType build() {
      return oasArrayType;
    }
  }

}
