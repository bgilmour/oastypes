package com.langtoun.oastypes.visitor;

import com.langtoun.oastypes.OASArrayType;
import com.langtoun.oastypes.OASBooleanType;
import com.langtoun.oastypes.OASDateTimeType;
import com.langtoun.oastypes.OASDateType;
import com.langtoun.oastypes.OASIntegerType;
import com.langtoun.oastypes.OASNullType;
import com.langtoun.oastypes.OASNumberType;
import com.langtoun.oastypes.OASObjectType;
import com.langtoun.oastypes.OASStringType;

/**
 * A visitor interface that defines methods for each of the types
 * derived from {@link OASType}.
 *
 * @param <T>
 *          The type of the objects returned from the visitor.
 * @param <C>
 *          The type of the context object that is passed to the
 *          visitor.
 */
public interface OASTypeVisitor<T, C> {

  /**
   * Visit an object of type {@link OASObjectType} and create a new object
   * of type T.
   *
   * @param objectType
   *          The object type being visited.
   * @param context
   *          This visiting context.
   * @return The new object of type T.
   */
  T visitObject(OASObjectType objectType, C context);

  /**
   * Visit an object of type {@link OASArrayType} and create a new object
   * of type T.
   *
   * @param arrayType
   *          The array type being visited.
   * @param context
   *          This visiting context.
   * @return The new object of type T.
   */
  T visitArray(OASArrayType arrayType, C context);

  /**
   * Visit an object of type {@link OASStringType} and create a new object
   * of type T.
   *
   * @param stringType
   *          The string type being visited.
   * @param context
   *          This visiting context.
   * @return The new object of type T.
   */
  T visitString(OASStringType stringType, C context);

  /**
   * Visit an object of type {@link OASNumberType} and create a new object
   * of type T.
   *
   * @param numberType
   *          The number type being visited.
   * @param context
   *          This visiting context.
   * @return The new object of type T.
   */
  T visitNumber(OASNumberType numberType, C context);

  /**
   * Visit an object of type {@link OASIntegerType} and create a new object
   * of type T.
   *
   * @param integerType
   *          The integer type being visited.
   * @param context
   *          This visiting context.
   * @return The new object of type T.
   */
  T visitInteger(OASIntegerType integerType, C context);

  /**
   * Visit an object of type {@link OASBooleanType} and create a new object
   * of type T.
   *
   * @param booleanType
   *          The boolean type being visited.
   * @param context
   *          This visiting context.
   * @return The new object of type T.
   */
  T visitBoolean(OASBooleanType booleanType, C context);

  /**
   * Visit an object of type {@link OASDateType} and create a new object
   * of type T.
   *
   * @param dateType
   *          The date type being visited.
   * @param context
   *          This visiting context.
   * @return The new object of type T.
   */
  T visitDate(OASDateType dateType, C context);

  /**
   * Visit an object of type {@link OASDateTimeType} and create a new object
   * of type T.
   *
   * @param dateTimeType
   *          The date-time type being visited.
   * @param context
   *          This visiting context.
   * @return The new object of type T.
   */
  T visitDateTime(OASDateTimeType dateTimeType, C context);

  /**
   * Visit an object of type {@link OASNullType} and create a new object
   * of type T.
   *
   * @param nullType
   *          The null type being visited.
   * @param context
   *          This visiting context.
   * @return The new object of type T.
   */
  T visitNull(OASNullType nullType, C context);

}
