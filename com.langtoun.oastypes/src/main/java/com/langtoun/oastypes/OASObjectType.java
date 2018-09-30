package com.langtoun.oastypes;

import java.util.List;
import java.util.Map;

import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * Public interface for OpenAPI specification object types. Objects of this
 * type are derived from YAML like this:
 * <pre><code>
 *   type: object
 *   required: ...
 *   properties: ...
 * </code></pre>
 */
public interface OASObjectType extends OASType {

  /**
   * A list of property names corresponding to those that have been designated
   * required.
   *
   * @return The list of {@code required} property names. This list is never
   *         {@code null}.
   */
  List<String> required();

  /**
   * Check whether or not a named property is in the required list.
   *
   * @param propertyName The name of the property whose status is to be checked.
   * @return {@code true} if the named property is required, otherwise {@code false}.
   */
  boolean isRequired(String propertyName);

  /**
   * Property definitions MUST be a {@link Schema} Object and not a standard
   * JSON Schema (inline or referenced).
   *
   * @return A map of property names and {@link OASType} objects built from the
   *         entries in the {@code properties} node. This map is never {@code null}.
   */
  Map<String, OASType> properties();

  /**
   * The minimum number of properties that may be present in an object.
   *
   * @return The value of the {@code minProperties} node.
   */
  Integer minProperties();

  /**
   * The maximum number of properties that are allowed in an object.
   *
   * @return The value of the {@code maxProperties} node.
   */
  Integer maxProperties();

  /**
   * This method determines whether or not the {@code required} node was present.
   *
   * @return {code true} if the node was present, otherwise {@code false}.
   */
  boolean hasRequired();

  /**
   * This method determines whether or not the {@code properties} node was present.
   *
   * @return {code true} if the node was present, otherwise {@code false}.
   */
  boolean hasProperties();

}
