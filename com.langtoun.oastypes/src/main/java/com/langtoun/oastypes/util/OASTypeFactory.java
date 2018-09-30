package com.langtoun.oastypes.util;

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

public class OASTypeFactory {

  public static OASType createOASType(final Schema schema, final Reference reference) {
    OASType oasType = null;
    /*
     * handle Schema Object cases
     *
     * - [x] anonymous objects
     * - [x] scalar types
     * - [/] arrays (partial handling - items can be any of the other options)
     * - [ ] $ref
     * - [ ] allOf
     * - [ ] anyOf
     */
    System.out.printf("    --> OASTypeFactory: %s:%s from %s\n", schema.getName(), schema.getType(), schema);
    if (reference != null) {
      System.out.printf("        [$ref: %s]\n", reference.getRefString());
    } else {
      System.out.println("        [$ref: null]");
    }
    final String schemaType = schema.getType() != null ? schema.getType() : "object";
    switch (schemaType) {
      case "object":
        oasType =
          OASObjectTypeImpl.builder(schema, reference)
            .required()
            .properties()
            .minProperties()
            .maxProperties()
            .build();
        break;
      case "array":
        oasType =
          OASArrayTypeImpl.builder(schema, reference)
            .items()
            .minItems()
            .maxItems()
            .uniqueItems()
            .build();
        break;
      case "string":
        if (schema.getFormat() != null && "date".equals(schema.getFormat())) {
          oasType =
            OASDateTypeImpl.builder(schema, reference)
              .build();
        } else if (schema.getFormat() != null && "date-time".equals(schema.getFormat())) {
          oasType =
            OASDateTimeTypeImpl.builder(schema, reference)
              .build();
        } else {
          oasType =
            OASStringTypeImpl.builder(schema, reference)
              .minLength()
              .maxLength()
              .pattern()
              .enums()
              .build();
        }
        break;
      case "number":
        oasType =
          OASNumberTypeImpl.builder(schema, reference)
            .minimum()
            .maximum()
            .exclusiveMinimum()
            .exclusiveMaximum()
            .enums()
            .build();
        break;
      case "integer":
        oasType =
          OASIntegerTypeImpl.builder(schema, reference)
            .minimum()
            .maximum()
            .exclusiveMinimum()
            .exclusiveMaximum()
            .enums()
            .build();
        break;
      case "boolean":
        oasType =
            OASBooleanTypeImpl.builder(schema, reference)
              .build();
        break;
      default:
        break;
    }
    System.out.printf("    <-- OASTypeFactory: %s\n", (oasType != null ? oasType.getClass().getSimpleName() : "null"));
    return oasType;
  }

}
