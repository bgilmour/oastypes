package com.langtoun.oastypes.util

import com.langtoun.oastypes.OASType
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
}