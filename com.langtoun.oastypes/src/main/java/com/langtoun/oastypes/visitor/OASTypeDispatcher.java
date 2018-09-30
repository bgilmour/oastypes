package com.langtoun.oastypes.visitor;

import com.langtoun.oastypes.OASType;

/**
 * A dispatcher interface that invokes the appropriate method in
 * a class the implements the {@link OASTypeVisitor} interface.
 *
 * @param <T>  The type of the objects returned from the visitor.
 * @param <C>  The type of the context object that is passed to the
 *             visitor.
 */
public interface OASTypeDispatcher<T, C> {

  /**
   * Dispatch the {@link OASType} object to the appropriate visitor
   * method.
   *
   * @param oasType  The object that is to be visited.
   * @param context  The context for visiting.
   * @return An object of type T created by the visitor.
   */
  T visit(OASType oasType, C context);

}
