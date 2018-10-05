package com.langtoun.oastypes.util

import com.reprezen.jsonoverlay.Reference

/**
 * Utility class that provides methods for dealing with {@link Reference} objects
 * from the JsonOverlay library.
 */
class ReferenceUtil {

  def static String fileName(Reference reference) {
    if (reference !== null) {
      val normalizedRef = reference.normalizedRef
      val beforeFragment = normalizedRef.substring(0, normalizedRef.lastIndexOf('#'))
      return beforeFragment.substring(beforeFragment.lastIndexOf('/') + 1);
    }
    return ""
  }

  def static String typeName(Reference reference) {
    if (reference !== null) {
      val fragment = reference.fragment
      return fragment.substring(fragment.lastIndexOf('/') + 1);
    }
    return ""
  }

  def static String namespaceURI(Reference reference) {
    if (reference !== null) {
      return "http://" + reference.fileName.replaceAll("[^a-zA-Z0-9]+", "_") + "/" + reference.fragment.substring(1, reference.fragment.lastIndexOf('/'))
    }
    return ""
  }

  def static String namespaceAlias(Reference reference) {
    if (reference !== null) {
      return reference.fileName.replaceAll("[^a-zA-Z0-9]+", "_")
    }
    return null
  }

}