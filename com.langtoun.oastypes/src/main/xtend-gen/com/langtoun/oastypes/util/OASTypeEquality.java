package com.langtoun.oastypes.util;

import com.google.common.base.Objects;
import com.langtoun.oastypes.OASType;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class OASTypeEquality {
  /**
   * Public entry point that checks whether either or both types are null, and ensures that
   * both objects are of the same type before proceeding to the recursive comparison.
   * 
   * @param td1
   *          the first type declaration object
   * @param td2
   *          the second type declaration object
   * @result {code true} if the types are equal, otherwise {@code false}
   */
  public static boolean equals(final OASType td1, final OASType td2) {
    if (((td1 == null) && (td2 == null))) {
      return true;
    }
    if ((((td1 == null) && (td2 != null)) || ((td1 != null) && (td2 == null)))) {
      return false;
    }
    String _name = td1.getClass().getName();
    String _name_1 = td2.getClass().getName();
    boolean _notEquals = (!Objects.equal(_name, _name_1));
    if (_notEquals) {
      return false;
    }
    return OASTypeEquality.equals(td1, td2, CollectionLiterals.<OASType>newHashSet());
  }
  
  private static boolean equals(final OASType td1, final OASType td2, final Set<OASType> visited) {
    return OASTypeEquality.oasTypeEquals(td1, td2, visited);
  }
  
  /**
   * catch all for types that require no further processing
   */
  private static boolean _oasTypeEquals(final OASType td1, final OASType td2, final Set<OASType> visited) {
    String _name = td1.getClass().getName();
    String _name_1 = td2.getClass().getName();
    return Objects.equal(_name, _name_1);
  }
  
  private static boolean oasTypeEquals(final OASType td1, final OASType td2, final Set<OASType> visited) {
    return _oasTypeEquals(td1, td2, visited);
  }
}
