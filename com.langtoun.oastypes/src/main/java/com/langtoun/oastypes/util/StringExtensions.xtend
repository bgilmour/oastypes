package com.langtoun.oastypes.util

import com.google.common.base.CaseFormat
import java.util.HashMap
import java.util.regex.Pattern
import java.util.stream.Collectors

class StringExtensions {

  static val ALT_FIELD_PREFIX = "aws__"

  static val legalIdentifierMatcher = Pattern.compile("[^\\w#]").matcher("")

  static val illegalIdentifiers = new HashMap<String, String>

  def static String quote(String str, String quotes) {
    return quotes.substring(0,  1) + str + quotes.substring(quotes.length() - 1)
  }

  def static String doubleQuote(String str) {
    return quote(str, "\"")
  }

  def static String singleQuote(String str) {
    return quote(str, "'")
  }

  def static String parenthesise(String str) {
    return quote(str, "()")
  }

  def static String bracket(String str) {
    return quote(str, "[]")
  }

  def static String brace(String str) {
    return quote(str, "{}")
  }

  def static altFieldPrefix(String s) {
    if (s !== null && !s.empty) {
      if (!Character.isLetter(s.charAt(0))) {
        return ALT_FIELD_PREFIX + s
      }
    }
    return s
  }

  def static legal(String identifier) {
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
          String.join(
            "#",
            identifier.split("#")
              .stream
              .map[part | CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, part.replaceAll("\\W+", "-"))]
              .collect(Collectors.toList)
          )
      } else {
        replacement =
          String.join(
            "#",
            identifier.split("#")
              .stream
              .map[part | CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, part.replaceAll("\\W+", "-"))]
              .collect(Collectors.toList)
          )
      }
      illegalIdentifiers.put(identifier, replacement)
      return replacement
    }
    return identifier
  }

}