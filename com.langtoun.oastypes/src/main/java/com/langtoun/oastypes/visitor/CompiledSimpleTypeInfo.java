package com.langtoun.oastypes.visitor;

import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * Utility class to help during the compilation of simple types.
 */
public class CompiledSimpleTypeInfo {

  private final String namespaceURI;
  private final String typeName;
  private final XmlSchemaType schemaType;
  private final XmlSchemaAnnotation annotation;

  protected CompiledSimpleTypeInfo(final String namespaceURI, final String typeName, final XmlSchemaType schemaType, final XmlSchemaAnnotation annotation) {
    this.namespaceURI = namespaceURI;
    this.typeName = typeName;
    this.schemaType = schemaType;
    this.annotation = annotation;
  }

  public String getNamespaceURI() {
    return namespaceURI;
  }

  public String getTypeName() {
    return typeName;
  }

  public XmlSchemaType getSchemaType() {
    return schemaType;
  }

  public XmlSchemaAnnotation getAnnotation() {
    return annotation;
  }

  /**
   * Builder class to create {@link CompiledSimpleTypeInfo} objects.
   */
  public static class Builder {
    private final String namespaceURI;
    private final String typeName;
    private XmlSchemaType schemaType;
    private XmlSchemaAnnotation annotation;

    public Builder(final String namespaceURI, final String typeName) {
      this.namespaceURI = namespaceURI;
      this.typeName = typeName;
    }

    /**
     * Complete the building process.
     *
     * @return a new {@link CompiledSimpleTypeInfo} object
     */
    public CompiledSimpleTypeInfo build() {
      return new CompiledSimpleTypeInfo(namespaceURI, typeName, schemaType, annotation);
    }

    /**
     * Add a schema type to the builder.
     *
     * @param schemaType
     *          the schema type
     * @return the builder
     */
    public Builder setSchemaType(final XmlSchemaType schemaType) {
      this.schemaType = schemaType;
      return this;
    }

    /**
     * Add an annotation to the builder.
     *
     * @param annotation
     *          the annotation
     * @return the builder
     */
    public Builder setAnnotation(final XmlSchemaAnnotation annotation) {
      this.annotation = annotation;
      return this;
    }
  }

}
