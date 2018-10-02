package com.langtoun.oastypes.util;

import com.google.common.base.Objects;
import com.langtoun.oastypes.OASArrayType;
import com.langtoun.oastypes.OASIntegerType;
import com.langtoun.oastypes.OASNumberType;
import com.langtoun.oastypes.OASObjectType;
import com.langtoun.oastypes.OASStringType;
import com.langtoun.oastypes.OASType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
  
  /**
   * object type
   */
  private static boolean _oasTypeEquals(final OASObjectType td1, final OASObjectType td2, final Set<OASType> visited) {
    boolean _contains = visited.contains(td1);
    boolean _not = (!_contains);
    if (_not) {
      visited.add(td1);
      int _size = td1.properties().size();
      int _size_1 = td2.properties().size();
      boolean _notEquals = (_size != _size_1);
      if (_notEquals) {
        return false;
      }
      Set<String> _keySet = td1.properties().keySet();
      Set<String> _keySet_1 = td2.properties().keySet();
      boolean _notEquals_1 = (!Objects.equal(_keySet, _keySet_1));
      if (_notEquals_1) {
        return false;
      }
      Set<Map.Entry<String, OASType>> _entrySet = td1.properties().entrySet();
      for (final Map.Entry<String, OASType> tdProp1 : _entrySet) {
        OASType _value = tdProp1.getValue();
        OASType _get = td2.properties().get(tdProp1.getKey());
        boolean _notEquals_2 = (!Objects.equal(_value, _get));
        if (_notEquals_2) {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * array type
   */
  private static boolean _oasTypeEquals(final OASArrayType td1, final OASArrayType td2, final Set<OASType> visited) {
    Integer _minItems = td1.minItems();
    Integer _minItems_1 = td2.minItems();
    boolean _notEquals = (!Objects.equal(_minItems, _minItems_1));
    if (_notEquals) {
      return false;
    }
    Integer _maxItems = td1.maxItems();
    Integer _maxItems_1 = td2.maxItems();
    boolean _notEquals_1 = (!Objects.equal(_maxItems, _maxItems_1));
    if (_notEquals_1) {
      return false;
    }
    Boolean _uniqueItems = td1.uniqueItems();
    Boolean _uniqueItems_1 = td2.uniqueItems();
    boolean _notEquals_2 = (!Objects.equal(_uniqueItems, _uniqueItems_1));
    if (_notEquals_2) {
      return false;
    }
    return OASTypeEquality.oasTypeEquals(td1.items(), td2.items(), visited);
  }
  
  /**
   * scalar types
   */
  private static boolean _oasTypeEquals(final OASStringType td1, final OASStringType td2, final Set<OASType> visited) {
    Integer _minLength = td1.minLength();
    Integer _minLength_1 = td2.minLength();
    boolean _notEquals = (!Objects.equal(_minLength, _minLength_1));
    if (_notEquals) {
      return false;
    }
    Integer _maxLength = td1.maxLength();
    Integer _maxLength_1 = td2.maxLength();
    boolean _notEquals_1 = (!Objects.equal(_maxLength, _maxLength_1));
    if (_notEquals_1) {
      return false;
    }
    String _pattern = td1.pattern();
    String _pattern_1 = td2.pattern();
    boolean _notEquals_2 = (!Objects.equal(_pattern, _pattern_1));
    if (_notEquals_2) {
      return false;
    }
    return OASTypeEquality.<String>facetListEquals(td1.enums(), td2.enums());
  }
  
  private static boolean _oasTypeEquals(final OASNumberType td1, final OASNumberType td2, final Set<OASType> visited) {
    String _format = td1.format();
    String _format_1 = td2.format();
    boolean _notEquals = (!Objects.equal(_format, _format_1));
    if (_notEquals) {
      return false;
    }
    Number _minimum = td1.minimum();
    Number _minimum_1 = td2.minimum();
    boolean _notEquals_1 = (!Objects.equal(_minimum, _minimum_1));
    if (_notEquals_1) {
      return false;
    }
    Number _maximum = td1.maximum();
    Number _maximum_1 = td2.maximum();
    boolean _notEquals_2 = (!Objects.equal(_maximum, _maximum_1));
    if (_notEquals_2) {
      return false;
    }
    Boolean _exclusiveMinimum = td1.exclusiveMinimum();
    Boolean _exclusiveMinimum_1 = td2.exclusiveMinimum();
    boolean _notEquals_3 = (!Objects.equal(_exclusiveMinimum, _exclusiveMinimum_1));
    if (_notEquals_3) {
      return false;
    }
    Boolean _exclusiveMaximum = td1.exclusiveMaximum();
    Boolean _exclusiveMaximum_1 = td2.exclusiveMaximum();
    boolean _notEquals_4 = (!Objects.equal(_exclusiveMaximum, _exclusiveMaximum_1));
    if (_notEquals_4) {
      return false;
    }
    Number _multipleOf = td1.multipleOf();
    Number _multipleOf_1 = td2.multipleOf();
    boolean _notEquals_5 = (!Objects.equal(_multipleOf, _multipleOf_1));
    if (_notEquals_5) {
      return false;
    }
    return OASTypeEquality.<Number>facetListEquals(td1.enums(), td2.enums());
  }
  
  private static boolean _oasTypeEquals(final OASIntegerType td1, final OASIntegerType td2, final Set<OASType> visited) {
    String _format = td1.format();
    String _format_1 = td2.format();
    boolean _notEquals = (!Objects.equal(_format, _format_1));
    if (_notEquals) {
      return false;
    }
    Number _minimum = td1.minimum();
    Number _minimum_1 = td2.minimum();
    boolean _notEquals_1 = (!Objects.equal(_minimum, _minimum_1));
    if (_notEquals_1) {
      return false;
    }
    Number _maximum = td1.maximum();
    Number _maximum_1 = td2.maximum();
    boolean _notEquals_2 = (!Objects.equal(_maximum, _maximum_1));
    if (_notEquals_2) {
      return false;
    }
    Boolean _exclusiveMinimum = td1.exclusiveMinimum();
    Boolean _exclusiveMinimum_1 = td2.exclusiveMinimum();
    boolean _notEquals_3 = (!Objects.equal(_exclusiveMinimum, _exclusiveMinimum_1));
    if (_notEquals_3) {
      return false;
    }
    Boolean _exclusiveMaximum = td1.exclusiveMaximum();
    Boolean _exclusiveMaximum_1 = td2.exclusiveMaximum();
    boolean _notEquals_4 = (!Objects.equal(_exclusiveMaximum, _exclusiveMaximum_1));
    if (_notEquals_4) {
      return false;
    }
    Number _multipleOf = td1.multipleOf();
    Number _multipleOf_1 = td2.multipleOf();
    boolean _notEquals_5 = (!Objects.equal(_multipleOf, _multipleOf_1));
    if (_notEquals_5) {
      return false;
    }
    return OASTypeEquality.<Number>facetListEquals(td1.enums(), td2.enums());
  }
  
  /**
   * facet list equality
   */
  private static <T extends Object> boolean facetListEquals(final List<T> l1, final List<T> l2) {
    int _size = l1.size();
    int _size_1 = l2.size();
    boolean _equals = (_size == _size_1);
    if (_equals) {
      final Set<T> s1 = new HashSet<T>(l1);
      final Set<T> s2 = new HashSet<T>(l2);
      return Objects.equal(s1, s2);
    }
    return false;
  }
  
  private static boolean oasTypeEquals(final OASType td1, final OASType td2, final Set<OASType> visited) {
    if (td1 instanceof OASArrayType
         && td2 instanceof OASArrayType) {
      return _oasTypeEquals((OASArrayType)td1, (OASArrayType)td2, visited);
    } else if (td1 instanceof OASIntegerType
         && td2 instanceof OASIntegerType) {
      return _oasTypeEquals((OASIntegerType)td1, (OASIntegerType)td2, visited);
    } else if (td1 instanceof OASNumberType
         && td2 instanceof OASNumberType) {
      return _oasTypeEquals((OASNumberType)td1, (OASNumberType)td2, visited);
    } else if (td1 instanceof OASObjectType
         && td2 instanceof OASObjectType) {
      return _oasTypeEquals((OASObjectType)td1, (OASObjectType)td2, visited);
    } else if (td1 instanceof OASStringType
         && td2 instanceof OASStringType) {
      return _oasTypeEquals((OASStringType)td1, (OASStringType)td2, visited);
    } else if (td1 != null
         && td2 != null) {
      return _oasTypeEquals(td1, td2, visited);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(td1, td2, visited).toString());
    }
  }
}
