package com.langtoun.oastypes.util

import com.langtoun.oastypes.OASArrayType
import com.langtoun.oastypes.OASIntegerType
import com.langtoun.oastypes.OASNumberType
import com.langtoun.oastypes.OASObjectType
import com.langtoun.oastypes.OASStringType
import com.langtoun.oastypes.OASType
import java.util.HashSet
import java.util.List
import java.util.Set

class OASTypeEquality {

  /*
   * Public entry point that checks whether either or both types are null, and ensures that
   * both objects are of the same type before proceeding to the recursive comparison.
   *
   * @param td1
   *          the first type declaration object
   * @param td2
   *          the second type declaration object
   * @result {code true} if the types are equal, otherwise {@code false}
   */
  static def boolean equals(OASType td1, OASType td2) {
    if (td1 === null && td2 === null) return true

    if (td1 === null && td2 !== null || td1 !== null && td2 === null) return false

    if (td1.class.name != td2.class.name) return false

    return equals(td1, td2, newHashSet)
  }

  private static def boolean equals(OASType td1, OASType td2, Set<OASType> visited) {
    return oasTypeEquals(td1, td2, visited)
  }

  /*
   * catch all for types that require no further processing
   */

  private static def dispatch boolean oasTypeEquals(OASType td1, OASType td2, Set<OASType> visited) {

    return td1.class.name == td2.class.name
  }

  /*
   * object type
   */

  private static def dispatch boolean oasTypeEquals(OASObjectType td1, OASObjectType td2, Set<OASType> visited) {
    if (!visited.contains(td1)) {
      visited.add(td1)
      if (td1.properties.size != td2.properties.size) return false

      if (td1.properties.keySet != td2.properties.keySet) return false

      for (tdProp1 : td1.properties.entrySet) {
        if (tdProp1.value != td2.properties.get(tdProp1.key)) return false
      }
    }

    return true
  }

  /*
   * array type
   */

  private static def dispatch boolean oasTypeEquals(OASArrayType td1, OASArrayType td2, Set<OASType> visited) {
    if (td1.minItems != td2.minItems) return false

    if (td1.maxItems != td2.maxItems) return false

    if (td1.uniqueItems() != td2.uniqueItems()) return false

    return oasTypeEquals(td1.items, td2.items, visited)
  }

  /*
   * scalar types
   */

  private static def dispatch boolean oasTypeEquals(OASStringType td1, OASStringType td2, Set<OASType> visited) {
    if (td1.minLength != td2.minLength) return false

    if (td1.maxLength != td2.maxLength) return false

    if (td1.pattern != td2.pattern) return false

    return facetListEquals(td1.enums, td2.enums)
  }

  private static def dispatch boolean oasTypeEquals(OASNumberType td1, OASNumberType td2, Set<OASType> visited) {
    if (td1.format != td2.format) return false

    if (td1.minimum != td2.minimum) return false

    if (td1.maximum != td2.maximum) return false

    if (td1.exclusiveMinimum() != td2.exclusiveMinimum()) return false

    if (td1.exclusiveMaximum() != td2.exclusiveMaximum()) return false

    if (td1.multipleOf != td2.multipleOf) return false

    return facetListEquals(td1.enums, td2.enums)
  }

  private static def dispatch boolean oasTypeEquals(OASIntegerType td1, OASIntegerType td2, Set<OASType> visited) {
    if (td1.format != td2.format) return false

    if (td1.minimum != td2.minimum) return false

    if (td1.maximum != td2.maximum) return false

    if (td1.exclusiveMinimum() != td2.exclusiveMinimum()) return false

    if (td1.exclusiveMaximum() != td2.exclusiveMaximum()) return false

    if (td1.multipleOf != td2.multipleOf) return false

    return facetListEquals(td1.enums, td2.enums)
  }

  /*
   * facet list equality
   */

  private static def <T> boolean facetListEquals(List<T> l1, List<T> l2) {
    if (l1.size == l2.size) {
      val Set<T> s1 = new HashSet<T>(l1)
      val Set<T> s2 = new HashSet<T>(l2)
      return s1 == s2
    }
    return false
  }

}