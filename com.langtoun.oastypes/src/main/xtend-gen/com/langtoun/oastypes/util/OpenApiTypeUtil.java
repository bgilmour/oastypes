package com.langtoun.oastypes.util;

import com.langtoun.oastypes.OASArrayType;
import com.langtoun.oastypes.OASObjectType;
import com.langtoun.oastypes.OASType;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.constants.Constants;

@SuppressWarnings("all")
public class OpenApiTypeUtil {
  /**
   * Convert from OpenAPI's numeric types to appropriate XML schema numeric types.
   * 
   * @param openApiNumericTypeFormat
   *          the name of a RAML numeric type
   * @return the corresponding XML schema numeric type name
   */
  public static QName schemaNumericTypeName(final String openApiNumericFormat, final QName defaultType) {
    if ((openApiNumericFormat != null)) {
      if (openApiNumericFormat != null) {
        switch (openApiNumericFormat) {
          case "int32":
            return Constants.XSD_INT;
          case "int64":
            return Constants.XSD_LONG;
          case "float":
            return Constants.XSD_FLOAT;
          case "double":
            return Constants.XSD_DOUBLE;
          default:
            return defaultType;
        }
      } else {
        return defaultType;
      }
    }
    return defaultType;
  }
  
  public static boolean isIntegerType(final String openApiNumericFormat) {
    if ((openApiNumericFormat != null)) {
      if (openApiNumericFormat != null) {
        switch (openApiNumericFormat) {
          case "float":
            return false;
          case "double":
            return false;
          default:
            return true;
        }
      } else {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isComplexType(final OASType oasType) {
    if ((oasType instanceof OASObjectType)) {
      return true;
    } else {
      if ((oasType instanceof OASArrayType)) {
        OASType _items = ((OASArrayType)oasType).items();
        return (_items instanceof OASObjectType);
      }
    }
    return false;
  }
  
  public static boolean isRequired(final OASType oasType) {
    if (((oasType.parent() != null) && (oasType.parent() instanceof OASObjectType))) {
      OASType _parent = oasType.parent();
      final OASObjectType oasObjectType = ((OASObjectType) _parent);
      return oasObjectType.isRequired(oasType.mappedName());
    }
    return false;
  }
}
