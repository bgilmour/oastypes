package com.langtoun.oastypes.util;

public final class StringExtensions {

  public static String quote(final String str, final String quotes) {
    return quotes.substring(0,  1) + str + quotes.substring(quotes.length() - 1);
  }

  public static String doubleQuote(final String str) {
    return quote(str, "\"");
  }

  public static String singleQuote(final String str) {
    return quote(str, "'");
  }

  public static String parenthesise(final String str) {
    return quote(str, "()");
  }

  public static String bracket(final String str) {
    return quote(str, "[]");
  }

  public static String brace(final String str) {
    return quote(str, "{}");
  }

}
