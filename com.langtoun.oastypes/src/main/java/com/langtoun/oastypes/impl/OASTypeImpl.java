package com.langtoun.oastypes.impl;

import static com.langtoun.oastypes.util.StringExtensions.doubleQuote;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;

import com.langtoun.oastypes.OASType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Base class for OpenAPI Specification types derived from the
 * {@link Schema} object provided by the KaiZen openapi-parser
 * library.
 *
 * @see <a href="https://github.com/RepreZen/KaiZen-OpenApi-Parser">KaiZen-OpenApi-Parser</a>
 */
public class OASTypeImpl implements OASType {
  protected final OASType parent;
  // cached schema and reference objects
  protected final Schema schema;
  protected final Reference reference;
  protected final String mappedName;
  // members extracted from the model
  private final String name;
  private final String type;
  private final String format;
  private final String title;
  private final String description;
  private final Object defaultValue;
  private final Boolean nullable;
  private final Boolean readOnly;
  private final Boolean writeOnly;
  private final Boolean deprecated;
  // members computed from the model
  private final boolean isNullable;
  private final boolean isReadOnly;
  private final boolean isWriteOnly;
  private final boolean isDeprecated;

  /**
   * Protected constructor for an {@link OASTypeImpl} base object.
   *
   * @param parent
   *          The parent of this type.
   * @param mappedName
   *          The name of the node that a schema is attached to. For top level
   *          schemas this is the name of the type.
   * @param schemaType
   *          The resolved schema type name.
   * @param schema
   *          The {@link Schema} that will be used to build the {@link OASTypeImpl}.
   * @param reference
   *          A {@link Reference} associated with the schema which may be {@code null}.
   */
  protected OASTypeImpl(final OASType parent, final String mappedName, final String schemaType, final Schema schema, final Reference reference) {
    this.parent = parent;
    // cached schema object
    this.schema = schema;
    this.reference = reference;
    this.mappedName = mappedName;
    // members extracted from the model
    this.name = schema.getName();
    this.type = schemaType;
    this.format = schema.getFormat();
    this.title = schema.getTitle();
    this.description = schema.getDescription();
    this.defaultValue = schema.getDefault();
    this.nullable = schema.getNullable();
    this.readOnly = schema.getReadOnly();
    this.writeOnly = schema.getWriteOnly();
    this.deprecated = schema.getDeprecated();
    // members computed from the model
    this.isNullable = schema.isNullable();
    this.isReadOnly = schema.isReadOnly();
    this.isWriteOnly = schema.isWriteOnly();
    this.isDeprecated = schema.isDeprecated();
  }

  @Override
  public OASType parent() {
    return parent;
  }

  @Override
  public Schema schema() {
    return schema;
  }

  @Override
  public Reference reference() {
    return reference;
  }

  @Override
  public boolean isReference() {
    return reference != null;
  }

  @Override
  public String mappedName() {
    return mappedName;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String type() {
    return type;
  }

  @Override
  public String format() {
    return format;
  }

  @Override
  public String title() {
    return title;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public Object defaultValue() {
    return defaultValue;
  }

  @Override
  public Boolean nullable() {
    return nullable;
  }

  @Override
  public Boolean readOnly() {
    return readOnly;
  }

  @Override
  public Boolean writeOnly() {
    return writeOnly;
  }

  @Override
  public Boolean deprecated() {
    return deprecated;
  }

  @Override
  public boolean isNullable() {
    return isNullable;
  }

  @Override
  public boolean isReadOnly() {
    return isReadOnly;
  }

  @Override
  public boolean isWriteOnly() {
    return isWriteOnly;
  }

  @Override
  public boolean isDeprecated() {
    return isDeprecated;
  }

  @Override
  public String toJson() {
    return schema != null ? schema.toString() : "{}";
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();

    sb.append(doubleQuote("class")).append(":")
      .append(doubleQuote(escapeJson(getClass().getSimpleName())));
    if (reference != null) {
      sb.append(",").append(doubleQuote("$ref")).append(":")
        .append(doubleQuote(reference.getRefString()));
    }

    sb.append(",").append(doubleQuote("name")).append(":")
      .append(name != null ? doubleQuote(escapeJson(name)) : "null")
      .append(",").append(doubleQuote("type")).append(":")
      .append(type != null ? doubleQuote(escapeJson(type)) : "null");

    if (format != null) {
      sb.append(",").append(doubleQuote("format")).append(":")
        .append(doubleQuote(escapeJson(format)));
    }
    if (title != null) {
      sb.append(",").append(doubleQuote("title")).append(":")
        .append(doubleQuote(escapeJson(title)));
    }
    if (description != null) {
      sb.append(",").append(doubleQuote("description")).append(":")
        .append(doubleQuote(escapeJson(description)));
    }
    if (defaultValue != null) {
      sb.append(",").append(doubleQuote("default")).append(":")
        .append(doubleQuote(escapeJson(defaultValue.toString())));
    }
    if (nullable != null) {
      sb.append(",").append(doubleQuote("nullable")).append(":")
        .append(nullable.toString());
    }
    if (readOnly != null) {
      sb.append(",").append(doubleQuote("readOnly")).append(":")
        .append(readOnly.toString());
    }
    if (writeOnly != null) {
      sb.append(",").append(doubleQuote("writeOnly")).append(":")
        .append(writeOnly.toString());
    }
    if (deprecated != null) {
      sb.append(",").append(doubleQuote("deprecated")).append(":")
        .append(deprecated.toString());
    }

    return sb.toString();
  }

  @Override
  public int hashCode() {
    int hash = 13;
    hash = 37 * hash + (name != null ? name.hashCode() : 0);
    hash = 37 * hash + (type != null ? type.hashCode() : 0);
    hash = 37 * hash + (format != null ? format.hashCode() : 0);
    hash = 37 * hash + (title != null ? title.hashCode() : 0);
    hash = 37 * hash + (description != null ? description.hashCode() : 0);
    hash = 37 * hash + (defaultValue != null ? defaultValue.hashCode() : 0);
    hash = 37 * hash + (nullable != null ? nullable.hashCode() : 0);
    hash = 37 * hash + (readOnly != null ? readOnly.hashCode() : 0);
    hash = 37 * hash + (writeOnly != null ? writeOnly.hashCode() : 0);
    hash = 37 * hash + (deprecated != null ? deprecated.hashCode() : 0);
    return hash;
  }

}
