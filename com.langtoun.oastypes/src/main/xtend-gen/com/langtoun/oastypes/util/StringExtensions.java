package com.langtoun.oastypes.util;

import com.google.common.base.CaseFormat;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Conversions;

@SuppressWarnings("all")
public class StringExtensions {
  private final static String ALT_FIELD_PREFIX = "aws__";
  
  private final static Matcher legalIdentifierMatcher = Pattern.compile("[^\\w#]").matcher("");
  
  private final static HashMap<String, String> illegalIdentifiers = new HashMap<String, String>();
  
  public static String quote(final String str, final String quotes) {
    String _substring = quotes.substring(0, 1);
    String _plus = (_substring + str);
    int _length = quotes.length();
    int _minus = (_length - 1);
    String _substring_1 = quotes.substring(_minus);
    return (_plus + _substring_1);
  }
  
  public static String doubleQuote(final String str) {
    return StringExtensions.quote(str, "\"");
  }
  
  public static String singleQuote(final String str) {
    return StringExtensions.quote(str, "\'");
  }
  
  public static String parenthesise(final String str) {
    return StringExtensions.quote(str, "()");
  }
  
  public static String bracket(final String str) {
    return StringExtensions.quote(str, "[]");
  }
  
  public static String brace(final String str) {
    return StringExtensions.quote(str, "{}");
  }
  
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
        replacement = String.join(
          "#", 
          ((List<String>)Conversions.doWrapArray(identifier.split("#"))).stream().<String>map(_function).collect(Collectors.<String>toList()));
      } else {
        final Function<String, String> _function_1 = (String part) -> {
          return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, part.replaceAll("\\W+", "-"));
        };
        replacement = String.join(
          "#", 
          ((List<String>)Conversions.doWrapArray(identifier.split("#"))).stream().<String>map(_function_1).collect(Collectors.<String>toList()));
      }
      StringExtensions.illegalIdentifiers.put(identifier, replacement);
      return replacement;
    }
    return identifier;
  }
}
