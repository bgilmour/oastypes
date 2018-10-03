package com.langtoun.oastypes;

/**
 * Public interface for OpenAPI specification integer types. Objects of this
 * type are derived from YAML like this:
 * <pre><code>
 *   type: number
 *   format: int32
 *
 *   type: number
 *   format: int64
 * </code></pre>
 */
public interface OASIntegerType extends OASNumericType {

  /**
   * Find out whether this integer type maps to a {@link Long} or an {@link Integer}.
   *
   * @return {@code true} if the type is a {@link Long}, otherwise {@code false}.
   */
  boolean isLong();

}
