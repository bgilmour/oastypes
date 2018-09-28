package com.langtoun.oastypes.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Additional collectors for use with Stream::collect.
 */
public final class CollectorUtil {

  /**
   * Create a two parameter collector that uses a {@link LinkedHashMap}.
   *
   * @param keyMapper  The function that provides the key value.
   * @param valueMapper  The function that provides the mapped value.
   * @return
   */
  public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
    Function<? super T, ? extends K> keyMapper,
    Function<? super T, ? extends U> valueMapper
  ) {
    return Collectors.toMap(
      keyMapper,
      valueMapper,
      (u, v) -> {
        throw new IllegalStateException(String.format("Duplicate key %s", u));
      },
      LinkedHashMap::new
    );
  }

}
