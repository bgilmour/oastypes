package com.langtoun.oastypes.util

import com.google.common.base.CaseFormat
import java.util.HashMap
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * Utility methods that operate on strings. These methods are designed to be used
 * as static extensions in Xtend source or via regular static import in Java
 * sources.
 */
class StringExtensions {

  static val ALT_FIELD_PREFIX = "aws__"

  static val legalIdentifierMatcher = Pattern.compile("[^\\w#]").matcher("")

  static val illegalIdentifiers = new HashMap<String, String>

  /**
   * A utility method that uses the first and last characters in the {code quotes}
   * string to prefix and suffix the target string.
   *
   * @param str
   *          The string that is to be quoted.
   * @param quotes
   *          The string that provides the quotation characters.
   * @return The target string surrounded by quotation characters.
   */
  def static String quote(String str, String quotes) {
    if (str !== null && !str.empty) {
      if (quotes === null || quotes.length == 0) {
        return str
      }
      return quotes.substring(0,  1) + str + quotes.substring(quotes.length() - 1)
    }
    return null
  }

  /**
   * Use {@link StringExtensions#quote} to surround a string with double quotes.
   *
   * @param str
   *          The string that is to be double quoted.
   * @return The double quoted string.
   */
  def static String doubleQuote(String str) {
    return quote(str, "\"")
  }

  /**
   * Use {@link StringExtensions#quote} to surround a string with single quotes.
   *
   * @param str
   *          The string that is to be single quoted.
   * @return The single quoted string.
   */
  def static String singleQuote(String str) {
    return quote(str, "'")
  }

  /**
   * Use {@link StringExtensions#quote} to surround a string with parentheses.
   *
   * @param str
   *          The string that is to be parenthesised.
   * @return The parenthesised string.
   */
  def static String parenthesise(String str) {
    return quote(str, "()")
  }

  /**
   * Use {@link StringExtensions#quote} to surround a string with brackets.
   *
   * @param str
   *          The string that is to be bracketed.
   * @return The bracketed string.
   */
  def static String bracket(String str) {
    return quote(str, "[]")
  }

  /**
   * Use {@link StringExtensions#quote} to surround a string with braces.
   *
   * @param str
   *          The string that is to be surrounded with braces.
   * @return The brace quoted string.
   */
  def static String brace(String str) {
    return quote(str, "{}")
  }

  /**
   * If an identifier starts with an illegal character it is prefixed with an
   * identifying string.
   *
   * @param str
   *          The string representing an identifier that is to be checked.
   * @return The checked string, which may have been modified.
   */
  def static String altFieldPrefix(String str) {
    if (str !== null && !str.empty) {
      if (!Character.isLetter(str.charAt(0))) {
        return ALT_FIELD_PREFIX + str
      }
    }
    return str
  }

  /**
   * Transform a given identifier into a legal string. If the string is transformed,
   * the original and the transformed version are cached in a map so that the
   * transformation process isn't repeated unnecessarily.
   *
   * @param identifier
   *          The string to the tested for legality.
   * @return The identifier string, which may have been transformed.
   */
  def static String legal(String identifier) {
    if (identifier === null || identifier.empty) {
      return identifier
    }
    var replacement = illegalIdentifiers.getOrDefault(identifier, null)
    if (replacement !== null) {
      return replacement
    }
    legalIdentifierMatcher.reset(identifier)
    if (legalIdentifierMatcher.find) {
      if (identifier.matches("^[a-z].*")) {
        replacement =
          identifier.split("#")
            .stream
            .map[part | CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, part.replaceAll("\\W+", "-"))]
            .collect(Collectors.joining("#"))
      } else {
        replacement =
          identifier.split("#")
            .stream
            .map[part | CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, part.replaceAll("\\W+", "-"))]
            .collect(Collectors.joining("#"))
      }
      illegalIdentifiers.put(identifier, replacement)
      return replacement
    }
    return identifier
  }

}