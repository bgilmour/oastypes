package com.langtoun.oastypes.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.reprezen.jsonoverlay.JsonOverlay;
import com.reprezen.jsonoverlay.Overlay;
import com.reprezen.jsonoverlay.RefOverlay;
import com.reprezen.jsonoverlay.Reference;

/**
 * Utility class for working with overlay objects from the json-overlay library.
 *
 * @see <a href="https://github.com/RepreZen/JsonOverlay">RepreZen JsonOverlay Library</a>
 */
public class OverlayUtil {

  /**
   * Get the {@link Reference} used to define an object of type T using an
   * {@link Overlay} as the starting point.
   *
   * @param <T>
   *          The type of the OAS model3 object that may be defined by reference.
   * @param overlay
   *          An {@link Overlay} for an object of type T that may be defined
   *          by reference.
   * @return The {@link Reference} that defines the object of type T, or {@code null}
   *         if the object is not defined by reference.
   */
  public static <T> Reference getReference(final Overlay<T> overlay) {
    if (overlay != null) {
      final JsonOverlay<T> jsonOverlay = overlay.getOverlay();
      if (jsonOverlay != null) {
        return getReference(jsonOverlay);
      }
    }
    return null;
  }

  /**
   * Get the {@link Reference} used to define an object of type T using a
   * {@link JsonOverlay} as the starting point. This method breaks the encapsulation
   * of the json-overlay library through use of reflection to find and invoke the
   * private method {@code _getRefOverlay). The documented method for retrieving a
   * {@link Reference} from an object field (when the object isn't a {@link PropertiesOverlay},
   * a {@link MapOverlay}, or a {@link ListOverlay}), never returns a reference.
   *
   * @param <T>
   *          The type of the OAS model3 object that may be defined by reference.
   * @param jsonOverlay
   *          A {@link JsonOverlay} for an object of type T that may be
   *          defined by reference.
   * @return The {@link Reference} that defines the object of type T, or {@code null}
   *         if the object is not defined by reference.
   */
  @SuppressWarnings("unchecked")
  public static <T> Reference getReference(final JsonOverlay<T> jsonOverlay) {
    if (jsonOverlay != null) {
      try {
        Class<?> clazz = jsonOverlay.getClass();
        while (clazz != null) {
          try {
            Method method = clazz.getDeclaredMethod("_getRefOverlay");
            method.setAccessible(true);
            return getReference((RefOverlay<T>) method.invoke(jsonOverlay));
          } catch (NoSuchMethodException e) {
            clazz = clazz.getSuperclass();
          }
        }
      } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) { }
    }
    return null;
  }

  /**
   * Get the {@link Reference} used to define an object of type T using a
   * {@link RefOverlay} as the starting point.
   *
   * @param <T>
   *          The type of the OAS model3 object that may be defined by reference.
   * @param refOverlay
   *          A {@link RefOverlay} for an object of type T that may be
   *          defined by reference.
   * @return The {@link Reference} that defines the object of type T, or {@code null}
   *         if the object is not defined by reference.
   */
  public static <T> Reference getReference(final RefOverlay<T> refOverlay) {
    if (refOverlay != null) {
      return refOverlay._getReference();
    }
    return null;
  }

  /**
   * Get the {@link Reference} associated with the mapped items with the specified
   * key. The reference may be {@code null}.
   *
   * @param <T>
   *          The type of the OAS model3 object that may be defined by reference.
   * @param mappedItems
   *          The map containing the item whose reference is to be retrieved.
   * @param key
   *          Identifier for the item whose reference is to be retrieved.
   * @return The {@link Reference} that defines the object of type T, or {@code null}
   *         if the object is not defined by reference.
   */
  public static <T> Reference getReference(Map<String, T> mappedItems, String key) {
    return Overlay.of(mappedItems).getReference(key);
  }

}
