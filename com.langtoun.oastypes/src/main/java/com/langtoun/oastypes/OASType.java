package com.langtoun.oastypes;

import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * The Schema Object allows the definition of input and output data types.
 * These types can be objects, but also primitives and arrays. This object
 * is an extended subset of the JSON Schema Specification Wright Draft 00.
 * <p>
 * For more information about the properties, see JSON Schema Core and JSON
 * Schema Validation. Unless stated otherwise, the property definitions
 * follow the JSON Schema.
 *
 * @see <a href="http://json-schema.org/">JSON Schema Specification Wright Draft 00</a>
 * @see <a href="https://tools.ietf.org/html/draft-wright-json-schema-00">JSON Schema Core</a>
 * @see <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00">JSON Schema Validation</a>
 */
public interface OASType {

  /**
   * Access the {@link Schema} object from the OpenApi3 model that was used
   * to create this {@link OASType} object.
   *
   * @return The {@link Schema} object.
   */
  Schema schema();

  /**
   * Access the {@link Reference} object from the JsonOverlay model that is
   * associated with this {@link Schema} object.
   *
   * @return The {@link Reference} object, or {@code null} if there is no
   *         reference.
   */
  Reference reference();

  /**
   * Query whether the underlying {@link Schema} is actually a reference to
   * another schema.
   *
   * @return {@code true} if the schema is a reference, otherwise {@code false}.
   */
  boolean isReference();

  /**
   * The name associated with a {@link Schema} object.
   *
   * @return The object's name.
   */
  String name();

  /**
   * Value MUST be a string. Multiple types via an array are not supported.
   *
   * @return The value of the {@code type} node.
   */
  String type();

  /**
   * While relying on JSON Schema's defined formats, the OAS offers a few
   * additional predefined formats.
   *
   * @return The value of the {@code format} node.
   *
   * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#dataTypeFormat">Data Type Formats</a>
   */
  String format();

  /**
   * CommonMark syntax MAY be used for rich text representation.
   *
   * @return The value of the {@code description} node.
   *
   * @see <a href="https://spec.commonmark.org">CommonMark syntax</a>
   */
  String description();

  /**
   * The default value represents what would be assumed by the consumer
   * of the input as the value of the schema if one is not provided. Unlike
   * JSON Schema, the value MUST conform to the defined type for the Schema
   * Object defined at the same level. For example, if {@code type} is
   * {@code string}, then {@code default} can be "foo" but cannot be 1.
   *
   * @return The value of the {@code default} node.
   */
  Object defaultValue();

  /**
   * Allows sending a null value for the defined schema. Default value is
   * {@code false}.
   *
   * @return The value of the {@code nullable} node.
   */
  Boolean nullable();

  /**
   * Relevant only for Schema "properties" definitions. Declares the property as
   * "read only". This means that it MAY be sent as part of a response but SHOULD
   * NOT be sent as part of the request. If the property is marked as readOnly
   * being true and is in the required list, the required will take effect on the
   * response only. A property MUST NOT be marked as both readOnly and writeOnly
   * being true. Default value is {@code false}.
   *
   * @return The value of the {@code readOnly} node.
   */
  Boolean readOnly();

  /**
   * Relevant only for Schema "properties" definitions. Declares the property as
   * "write only". Therefore, it MAY be sent as part of a request but SHOULD NOT
   * be sent as part of the response. If the property is marked as writeOnly being
   * true and is in the required list, the required will take effect on the request
   * only. A property MUST NOT be marked as both {@code readOnly} and {@code writeOnly}
   * being true. Default value is {@code false}.
   *
   * @return The value of the {@code writeOnly} node.
   */
  Boolean writeOnly();

  /**
   * Specifies that a schema is deprecated and SHOULD be transitioned out of usage.
   * Default value is {@code false}.
   *
   * @return The value of the {@code deprecated} node.
   */
  Boolean deprecated();

  boolean isNullable();

  boolean isReadOnly();

  boolean isWriteOnly();

  boolean isDeprecated();

  String toJson();

}