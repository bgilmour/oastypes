package com.langtoun.oastypes;

public interface OASArrayType extends OASType {

  /**
   * Value MUST be an object and not an array. Inline or referenced schema
   * MUST be of a Schema Object and not a standard JSON Schema. items MUST
   * be present if the type is array.
   *
   * @return The value of the {@code items} node.
   */
  OASType items();

  /**
   * The minimum number of array elements that may be present.
   *
   * @return The value of the {@code minItems} node.
   */
  Integer minItems();

  /**
   * The maximum number of array elements that may be present.
   *
   * @return The value of the {@code maxItems} node.
   */
  Integer maxItems();

  /**
   * An indication that the array items must all be unique if the value is
   * {code true}..
   *
   * @return The value of the {@code minItems} node.
   */
  Boolean uniqueItems();

  boolean isUniqueItems();

}
