package com.langtoun.oastypes.util

import com.langtoun.oastypes.OASObjectType
import com.langtoun.oastypes.OASType
import com.langtoun.oastypes.OASArrayType
import javax.xml.namespace.QName
import org.apache.ws.commons.schema.constants.Constants

class OpenApiTypeUtil {

  /**
   * Convert from OpenAPI's numeric types to appropriate XML schema numeric types.
   *
   * @param openApiNumericTypeFormat
   *          The name of a RAML numeric type.
   * @return The corresponding XML schema numeric type name.
   */
  def static QName schemaNumericTypeName(String openApiNumericFormat, QName defaultType) {
    if (openApiNumericFormat !== null) {
      switch (openApiNumericFormat) {
      // signed integers
      case "int32":  return Constants.XSD_INT
      case "int64":  return Constants.XSD_LONG
      // floating point
      case "float":  return Constants.XSD_FLOAT
      case "double": return Constants.XSD_DOUBLE
      // other
      default:       return defaultType
      }
    }
    return defaultType
  }

  def static boolean isIntegerType(String openApiNumericFormat) {
    if (openApiNumericFormat !== null) {
      switch (openApiNumericFormat) {
      // non-integer formats
      case "float":  return false
      case "double": return false
      // integer formats
      default:       return true
      }
    }
    return false
  }

  def static boolean isComplexType(OASType oasType) {
    if (oasType instanceof OASObjectType) {
      return true
    } else if (oasType instanceof OASArrayType) {
      return oasType.items instanceof OASObjectType
    }
    return false
  }

  def static isRequired(OASType oasType) {
    if (oasType.parent !== null && oasType.parent instanceof OASObjectType) {
      val oasObjectType = oasType.parent as OASObjectType
      return oasObjectType.isRequired(oasType.mappedName)
    }
    return false
  }

}