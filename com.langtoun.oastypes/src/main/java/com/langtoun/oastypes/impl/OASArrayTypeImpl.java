package com.langtoun.oastypes.impl;

import static com.langtoun.oastypes.util.StringExtensions.doubleQuote;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;

import com.langtoun.oastypes.OASArrayType;
import com.langtoun.oastypes.OASType;
import com.langtoun.oastypes.util.OASTypeFactory;
import com.langtoun.oastypes.util.OverlayUtil;
import com.reprezen.jsonoverlay.Overlay;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

public class OASArrayTypeImpl extends OASTypeImpl implements OASArrayType {
  // members extracted from the model
  private OASType items;
  private Integer minItems;
  private Integer maxItems;
  private Boolean uniqueItems;
  // members computed from the model
  private boolean isUniqueItems;

  private OASArrayTypeImpl(final Schema schema, final Reference reference) {
    super(schema, reference);
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
    sb.append("}");

    return sb.toString();
  }

  /**
   * Create a new builder for an {@link OASArrayTypeImpl} object.
   *
   * @param schema  The {@link Schema} that will be used to build the {@link OASArrayTypeImpl}.
   * @param reference  A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final Schema schema, final Reference reference) {
    return new Builder(schema, reference);
  }

  public static class Builder {
    private final OASArrayTypeImpl oasArrayType;
    private final Schema schema;

    private Builder(final Schema schema, final Reference reference) {
      oasArrayType = new OASArrayTypeImpl(schema, reference);
      this.schema = schema;
    }

    public Builder items() {
      final Schema itemsSchema = schema.getItemsSchema();

      Overlay<Schema> itemsOverlay = Overlay.of(schema, "itemsSchema", Schema.class);
      Reference reference = OverlayUtil.getReference(itemsOverlay);

      oasArrayType.items = OASTypeFactory.createOASType(itemsSchema, reference);
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
