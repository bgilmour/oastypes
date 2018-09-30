package com.langtoun.oastypes;

import java.util.List;

/**
 * Public interface for OpenAPI specification object types. Objects of this
 * type are derived from YAML like this:
 * <pre><code>
 *   type: string
 * </code></pre>
 */
public interface OASStringType extends OASType {

  /**
   * The minimum allowed length of the string.
   *
   * @return The value of the {@code minLength} node, or {@code null} if the
   *         node was not present.
   */
  Integer minLength();

  /**
   * The maximum allowed length of the string.
   *
   * @return The value of the {@code maxLength} node, or {@code null} if the
   *         node was not present.
   */
  Integer maxLength();

  /**
   * The regular expression pattern that validates the string.
   *
   * @return The value of the {@code pattern} node, or {@code null} if the
   *         node was not present.
   */
  String pattern();

  /**
   * The list of allowed values for the string type.
   *
   * @return A list of items defined by the {@code enum} node. The list is never
   *         {@code null}.
   */
  List<String> enums();

  /**
   * This method determines whether or not the {@code enum} node was present.
   *
   * @return {code true} if the node was present, otherwise {@code false}.
   */
  boolean hasEnums();

}
