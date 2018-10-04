package com.langtoun.oastypes.util;

import com.reprezen.jsonoverlay.Reference;

/**
 * Utility class that provides methods for dealing with {@link Reference} objects
 * from the JsonOverlay library.
 */
@SuppressWarnings("all")
public class ReferenceUtil {
  public static String fileName(final Reference reference) {
    if ((reference != null)) {
      final String normalizedRef = reference.getNormalizedRef();
      final String beforeFragment = normalizedRef.substring(0, normalizedRef.lastIndexOf("#"));
      int _lastIndexOf = beforeFragment.lastIndexOf("/");
      int _plus = (_lastIndexOf + 1);
      return beforeFragment.substring(_plus);
    }
    return "";
  }
  
  public static String typeName(final Reference reference) {
    if ((reference != null)) {
      final String fragment = reference.getFragment();
      int _lastIndexOf = fragment.lastIndexOf("/");
      int _plus = (_lastIndexOf + 1);
      return fragment.substring(_plus);
    }
    return "";
  }
  
  public static String namespaceURI(final Reference reference) {
    if ((reference != null)) {
      String _fileName = ReferenceUtil.fileName(reference);
      String _plus = ("http://" + _fileName);
      String _plus_1 = (_plus + "/");
      String _substring = reference.getFragment().substring(1, reference.getFragment().lastIndexOf("/"));
      return (_plus_1 + _substring);
    }
    return "";
  }
  
  public static String namespaceAlias(final Reference reference) {
    if ((reference != null)) {
      return ReferenceUtil.fileName(reference).replaceAll("[^a-zA-Z0-9]+", "_");
    }
    return null;
  }
}
