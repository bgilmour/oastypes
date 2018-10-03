package com.langtoun.oastypes;

/**
 * Public interface for OpenAPI specification number types. Objects of this
 * type are derived from YAML like this:
 * <pre><code>
 *   type: number
 *   format: float
 *
 *   type: number
 *   format: double
 * </code></pre>
 */
public interface OASNumberType extends OASNumericType {

  /**
   * Find out whether this number type maps to a {@link Double} or a {@link Float}.
   *
   * @return {@code true} if the type is a {@link Double}, otherwise {@code false}.
   */
  boolean isDouble();

}
