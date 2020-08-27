package com.langtoun.oastypes.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.langtoun.oastypes.OASType;
import com.langtoun.oastypes.impl.OASArrayTypeImpl;
import com.langtoun.oastypes.impl.OASBooleanTypeImpl;
import com.langtoun.oastypes.impl.OASDateTimeTypeImpl;
import com.langtoun.oastypes.impl.OASDateTypeImpl;
import com.langtoun.oastypes.impl.OASIntegerTypeImpl;
import com.langtoun.oastypes.impl.OASNumberTypeImpl;
import com.langtoun.oastypes.impl.OASObjectTypeImpl;
import com.langtoun.oastypes.impl.OASStringTypeImpl;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

/**
 * A factory class that creates new {@link OASType} objects from
 * OpenAPI {@link Schema} objects.
 */
// @Format-Off
public final class OASTypeFactory {

  private OASTypeFactory() {}

  private static Logger LOGGER = Logger.getLogger(OASTypeFactory.class);

  /**
   * Create a new {@link OASType} object from a {@link Schema} object.
   *
   * @param mappedName
   *          The top level or property name associated with the type definition.
   * @param schema
   *          The OpenAPI schema object.
   * @param reference
   *          The reference associated with the type, or {@code null} if there is no reference.
   * @return A new {@link OASType} object.
   */
  public static OASType createOASType(final String mappedName, final Schema schema, final Reference reference) {
    return createOASType(null, mappedName, schema, reference);
  }

  /**
   * Create a new {@link OASType} object from a {@link Schema} object.
   *
   * @param parent
   *          The parent {@link OASType} object if this is a nested type definition.
   * @param mappedName
   *          The top level or property name associated with the type definition.
   * @param schema
   *          The OpenAPI schema object.
   * @param reference
   *          The reference associated with the type, or {@code null} if there is no reference.
   * @return A new {@link OASType} object.
   */
  public static OASType createOASType(final OASType parent, final String mappedName, final Schema schema, final Reference reference) {
    return createOASType(parent, mappedName, schema, reference, new HashMap<Schema, OASType>());
  }

  /**
   * Create a new {@link OASType} object from a {@link Schema} object.
   *
   * @param parent
   *          The parent {@link OASType} object if this is a nested type definition.
   * @param mappedName
   *          The top level or property name associated with the type definition.
   * @param schema
   *          The OpenAPI schema object.
   * @param reference
   *          The reference associated with the type, or {@code null} if there is no reference.
   * @param visitedTypes
   *          A {@link Set} that tracks visited types to guard against infinite recursion.
   * @return A new {@link OASType} object.
   */
  public static OASType createOASType(final OASType parent, final String mappedName, final Schema schema, final Reference reference, final Map<Schema, OASType> visitedTypes) {
    /*
     * handle schema object cases
     *
     * - [x] anonymous objects
     * - [x] scalar types
     * - [/] arrays (partial handling - items can be any of the other options)
     * - [/] $ref
     * - [ ] allOf
     * - [ ] anyOf
     *
     */
    OASType oasType = visitedTypes.getOrDefault(schema,  null);
    if (oasType != null) {
      return oasType;
    }

    String schemaType = schema.getType();
    if (schemaType == null) {
      if (schema.getItemsSchema() != null) {
        schemaType = "array";
      } else {
        schemaType = "object";
      }
    }

    switch (schemaType) {
      case "object":
        oasType =
          OASObjectTypeImpl.builder(parent, mappedName, schemaType, schema, reference, visitedTypes)
            .required()
            .properties(visitedTypes)
            .minProperties()
            .maxProperties()
            .build();
        break;
      case "array":
        oasType =
          OASArrayTypeImpl.builder(parent, mappedName, schemaType, schema, reference, visitedTypes)
            .items(visitedTypes)
            .minItems()
            .maxItems()
            .uniqueItems()
            .build();
        break;
      case "string":
        if (schema.getFormat() != null && "date".equals(schema.getFormat())) {
          oasType =
            OASDateTypeImpl.builder(parent, mappedName, schemaType, schema, reference)
              .build();
        } else if (schema.getFormat() != null && "date-time".equals(schema.getFormat())) {
          oasType =
            OASDateTimeTypeImpl.builder(parent, mappedName, schemaType, schema, reference)
              .build();
        } else {
          oasType =
            OASStringTypeImpl.builder(parent, mappedName, schemaType, schema, reference)
              .minLength()
              .maxLength()
              .pattern()
              .enums()
              .build();
        }
        break;
      case "number":
        oasType =
          OASNumberTypeImpl.builder(parent, mappedName, schemaType, schema, reference)
            .minimum()
            .maximum()
            .exclusiveMinimum()
            .exclusiveMaximum()
            .multipleOf()
            .enums()
            .build();
        break;
      case "integer":
        oasType =
          OASIntegerTypeImpl.builder(parent, mappedName, schemaType, schema, reference)
            .minimum()
            .maximum()
            .exclusiveMinimum()
            .exclusiveMaximum()
            .multipleOf()
            .enums()
            .build();
        break;
      case "boolean":
        oasType =
          OASBooleanTypeImpl.builder(parent, mappedName, schemaType, schema, reference)
            .build();
        break;
      default:
        LOGGER.error("Failed to create OASType from: " + schema);
        oasType = null;
        break;
    }
    return oasType;
  }

}
