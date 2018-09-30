package com.langtoun.oastypes.util;

import org.apache.ws.commons.schema.XmlSchemaType;

import com.langtoun.oastypes.OASArrayType;
import com.langtoun.oastypes.OASBooleanType;
import com.langtoun.oastypes.OASDateTimeType;
import com.langtoun.oastypes.OASDateType;
import com.langtoun.oastypes.OASIntegerType;
import com.langtoun.oastypes.OASNullType;
import com.langtoun.oastypes.OASNumberType;
import com.langtoun.oastypes.OASObjectType;
import com.langtoun.oastypes.OASStringType;
import com.langtoun.oastypes.OASType;
import com.langtoun.oastypes.visitor.OASTypeDispatcher;
import com.langtoun.oastypes.visitor.OASTypeVisitor;

public class OASTypeToXmlSchemaVisitor implements OASTypeDispatcher<XmlSchemaType, Boolean>, OASTypeVisitor<XmlSchemaType, Boolean> {

  public OASTypeToXmlSchemaVisitor() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public XmlSchemaType visit(final OASType oasType, Boolean context) {
    if (oasType instanceof OASObjectType) {
      return visitObject((OASObjectType) oasType, context);
    } else if (oasType instanceof OASArrayType) {
      return visitArray((OASArrayType) oasType, context);
    } else if (oasType instanceof OASStringType) {
      return visitString((OASStringType) oasType, context);
    } else if (oasType instanceof OASNumberType) {
      return visitNumber((OASNumberType) oasType, context);
    } else if (oasType instanceof OASIntegerType) {
      return visitInteger((OASIntegerType) oasType, context);
    } else if (oasType instanceof OASBooleanType) {
      return visitBoolean((OASBooleanType) oasType, context);
    } else if (oasType instanceof OASDateType) {
      return visitDate((OASDateType) oasType, context);
    } else if (oasType instanceof OASDateTimeType) {
      return visitDateTime((OASDateTimeType) oasType, context);
    } else if (oasType instanceof OASNullType) {
      return visitNull((OASNullType) oasType, context);
    }
    return null;
  }

  @Override
  public XmlSchemaType visitObject(final OASObjectType objectType, final Boolean context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public XmlSchemaType visitArray(final OASArrayType arrayType, final Boolean context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public XmlSchemaType visitString(final OASStringType stringType, final Boolean context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public XmlSchemaType visitNumber(final OASNumberType numberType, final Boolean context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public XmlSchemaType visitInteger(final OASIntegerType integerType, final Boolean context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public XmlSchemaType visitBoolean(final OASBooleanType booleanType, final Boolean context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public XmlSchemaType visitDate(final OASDateType dateType, final Boolean context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public XmlSchemaType visitDateTime(final OASDateTimeType dateTimeType, final Boolean context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public XmlSchemaType visitNull(final OASNullType nullType, final Boolean context) {
    // TODO Auto-generated method stub
    return null;
  }

}
