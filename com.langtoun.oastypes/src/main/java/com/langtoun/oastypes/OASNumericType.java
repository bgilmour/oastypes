package com.langtoun.oastypes;

import java.util.List;

/**
 * Public interface for OpenAPI specification numeric types.
 */
public interface OASNumericType extends OASType {

  /**
   * The minimum value allowed for the number type.
   *
   * @return The value of the {@code minimum} node, or {@code null} if the
   *         node was not present.
   */
  Number minimum();

  /**
   * The maximum value allowed for the number type.
   *
   * @return The value of the {@code maximum} node, or {@code null} if the
   *         node was not present.
   */
  Number maximum();

  /**
   * Determines whether the minimum value of number type is inclusive (the
   * default) or exclusive.
   *
   * @return The value of the {@code exclusiveMinimum} node, or {@code null}
   *         if the node was not present.
   */
  Boolean exclusiveMinimum();

  /**
   * Determines whether the maximum value of number type is inclusive (the
   * default) or exclusive.
   *
   * @return The value of the {@code exclusiveMaximum} node, or {@code null}
   *         if the node was not present.
   */
  Boolean exclusiveMaximum();

  /**
   * A number greater that zero that validates a numeric value only if that
   * value can be divided by this value with an integer result.
   *
   * @return The value of the {@code multipleOf} node, or {@code null}
   *         if the node was not present.
   */
  Number multipleOf();

  /**
   * The list of allowed values for the number type.
   *
   * @return A list of items defined by the {@code enum} node. The list is never
   *         {@code null}.
   */
  List<Number> enums();

  /**
   * This method computes whether or not the type is defined with an exclusive
   * minimum value.
   *
   * @return If the {@code exclusiveMinimum} node is present then its value is used,
   *         otherwise {@code false}.
   */
  boolean isExclusiveMinimum();

  /**
   * This method computes whether or not the type is defined with an exclusive
   * maximum value.
   *
   * @return If the {@code exclusiveMaximum} node is present then its value is used,
   *         otherwise {@code false}.
   */
  boolean isExclusiveMaximum();

  /**
   * This method determines whether or not the {@code enum} node was present.
   *
   * @return {code true} if the node was present, otherwise {@code false}.
   */
  boolean hasEnums();

}
