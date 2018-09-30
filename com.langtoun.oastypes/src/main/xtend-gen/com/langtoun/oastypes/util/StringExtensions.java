package com.langtoun.oastypes.util;

import com.google.common.base.CaseFormat;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Conversions;

/**
 * Utility methods that operate on strings. These methods are designed to be used
 * as static extensions in Xtend source or via regular static import in Java
 * sources.
 */
@SuppressWarnings("all")
public class StringExtensions {
  private final static String ALT_FIELD_PREFIX = "aws__";
  
  private final static Matcher legalIdentifierMatcher = Pattern.compile("[^\\w#]").matcher("");
  
  private final static HashMap<String, String> illegalIdentifiers = new HashMap<String, String>();
  
  /**
   * A utility method that uses the first and last characters in the {code quotes}
   * string to prefix and suffix the target string.
   * 
   * @param str  The string that is to be quoted.
   * @param quotes  The string that provides the quotation characters.
   * @return The target string surrounded by quotation characters.
   */
  public static String quote(final String str, final String quotes) {
    if (((str != null) && (!str.isEmpty()))) {
      if (((quotes == null) || (quotes.length() == 0))) {
        return str;
      }
      String _substring = quotes.substring(0, 1);
      String _plus = (_substring + str);
      int _length = quotes.length();
      int _minus = (_length - 1);
      String _substring_1 = quotes.substring(_minus);
      return (_plus + _substring_1);
    }
    return null;
  }
  
  /**
   * Use {@link StringExtensions#quote} to surround a string with double quotes.
   * 
   * @param str  The string that is to be double quoted.
   * @return The double quoted string.
   */
  public static String doubleQuote(final String str) {
    return StringExtensions.quote(str, "\"");
  }
  
  /**
   * Use {@link StringExtensions#quote} to surround a string with single quotes.
   * 
   * @param str  The string that is to be single quoted.
   * @return The single quoted string.
   */
  public static String singleQuote(final String str) {
    return StringExtensions.quote(str, "\'");
  }
  
  /**
   * Use {@link StringExtensions#quote} to surround a string with parentheses.
   * 
   * @param str  The string that is to be parenthesised.
   * @return The parenthesised string.
   */
  public static String parenthesise(final String str) {
    return StringExtensions.quote(str, "()");
  }
  
  /**
   * Use {@link StringExtensions#quote} to surround a string with brackets.
   * 
   * @param str  The string that is to be bracketed.
   * @return The bracketed string.
   */
  public static String bracket(final String str) {
    return StringExtensions.quote(str, "[]");
  }
  
  /**
   * Use {@link StringExtensions#quote} to surround a string with braces.
   * 
   * @param str  The string that is to be surrounded with braces.
   * @return The brace quoted string.
   */
  public static String brace(final String str) {
    return StringExtensions.quote(str, "{}");
  }
  
  /**
   * If an identifier starts with an illegal character it is prefixed with an
   * identifying string.
   * 
   * @param s  The string representing an identifier that is to be checked.
   * @return The checked string, which may have been modified.
   */
  public static String altFieldPrefix(final String s) {
    if (((s != null) && (!s.isEmpty()))) {
      boolean _isLetter = Character.isLetter(s.charAt(0));
      boolean _not = (!_isLetter);
      if (_not) {
        return (StringExtensions.ALT_FIELD_PREFIX + s);
      }
    }
    return s;
  }
  
  /**
   * Transform a given identifier into a legal string. If the string is transformed,
   * the original and the transformed version are cached in a map so that the
   * transformation process isn't repeated unnecessarily.
   * 
   * @param identifier  The string to the tested for legality.
   * @return the identifier string, which may have been transformed.
   */
  public static String legal(final String identifier) {
    if (((identifier == null) || identifier.isEmpty())) {
      return identifier;
    }
    String replacement = StringExtensions.illegalIdentifiers.getOrDefault(identifier, null);
    if ((replacement != null)) {
      return replacement;
    }
    StringExtensions.legalIdentifierMatcher.reset(identifier);
    boolean _find = StringExtensions.legalIdentifierMatcher.find();
    if (_find) {
      boolean _matches = identifier.matches("^[a-z].*");
      if (_matches) {
        final Function<String, String> _function = (String part) -> {
          return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, part.replaceAll("\\W+", "-"));
        };
        replacement = ((List<String>)Conversions.doWrapArray(identifier.split("#"))).stream().<String>map(_function).collect(Collectors.joining("#"));
      } else {
        final Function<String, String> _function_1 = (String part) -> {
          return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, part.replaceAll("\\W+", "-"));
        };
        replacement = ((List<String>)Conversions.doWrapArray(identifier.split("#"))).stream().<String>map(_function_1).collect(Collectors.joining("#"));
      }
      StringExtensions.illegalIdentifiers.put(identifier, replacement);
      return replacement;
    }
    return identifier;
  }
}
