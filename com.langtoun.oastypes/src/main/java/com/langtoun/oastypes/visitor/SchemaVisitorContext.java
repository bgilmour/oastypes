package com.langtoun.oastypes.visitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import com.langtoun.oastypes.OASType;

/**
 * Context object to be passed to the {@link OASTypeToXmlSchemaVisitor} object. This version
 * contains a stack of topLevel indicators. The context will be expanded in future to track
 * the visited state of type declarations to avoid stack overflow when handling recursive
 * type definitions.
 */
public final class SchemaVisitorContext {

  private final Deque<Boolean> topLevelStack;

  private final Set<String> visitedTypes;

  private final String targetNamespace;

  /**
   * Factory method to create a new context object.
   *
   * @param topLevel
   *          Is the visitor acting on a top level schema type?
   * @param targetNamespace
   *          The target namespace that scopes aliases from the uses node.
   * @return A new context object.
   */
  public static SchemaVisitorContext newContext(final boolean topLevel, final String targetNamespace) {
    return new SchemaVisitorContext(topLevel, targetNamespace);
  }

  private SchemaVisitorContext(final boolean topLevel, final String targetNamespace) {
    this.topLevelStack = new ArrayDeque<>();
    this.topLevelStack.push(topLevel);
    this.visitedTypes = new HashSet<>();
    this.targetNamespace = targetNamespace;
  }

  public boolean isTopLevel() {
    return topLevelStack.peek();
  }

  /**
   * Push a new top level value onto the stack.
   *
   * @param topLevel
   *          The new top level value.
   */
  public void pushTopLevel(final Boolean topLevel) {
    topLevelStack.push(topLevel);
  }

  /**
   * Remove the top level value from the top of the stack.
   *
   * @return The current top level value.
   */
  public Boolean popTopLevel() {
    return topLevelStack.pop();
  }

  /**
   * Check whether a type declaration has already been visited.
   *
   * @param typeDecl
   *          The type declaration that is to be checked.
   * @return {@code true} if the type has been visited, otherwise {@code false}.
   */
  public boolean isVisitedType(final OASType oasType) {
    return visitedTypes.contains(oasType.type() /* resolvedTypeName(oasType) */);
  }

  /**
   * Add a type declaration to the set of those already visited.
   *
   * @param typeDecl
   *          The type declaration to be added.
   */
  public void addVisitedType(final OASType oasType) {
    visitedTypes.add(oasType.type() /* resolvedTypeName(oasType) */);
  }

  /**
   * Get the target namespace that defines the scope of the aliases defined in the uses node.
   *
   * @return The target namesapce.
   */
  public String getTargetNamespace() {
    return targetNamespace;
  }

}
