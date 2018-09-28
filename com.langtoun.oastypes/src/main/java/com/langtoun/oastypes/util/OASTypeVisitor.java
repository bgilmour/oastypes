package com.langtoun.oastypes.util;

import com.langtoun.oastypes.OASArrayType;
import com.langtoun.oastypes.OASBooleanType;
import com.langtoun.oastypes.OASDateTimeType;
import com.langtoun.oastypes.OASDateType;
import com.langtoun.oastypes.OASIntegerType;
import com.langtoun.oastypes.OASNullType;
import com.langtoun.oastypes.OASNumberType;
import com.langtoun.oastypes.OASObjectType;
import com.langtoun.oastypes.OASStringType;

public interface OASTypeVisitor<T, C> {

  T visitObject(OASObjectType objectType, C context);

  T visitArray(OASArrayType arrayType, C context);

  T visitString(OASStringType stringType, C context);

  T visitNumber(OASNumberType numberType, C context);

  T visitInteger(OASIntegerType integerType, C context);

  T visitBoolean(OASBooleanType booleanType, C context);

  T visitDate(OASDateType dateType, C context);

  T visitDateTime(OASDateTimeType dateTimeType, C context);

  T visitNull(OASNullType nullType, C context);

}
