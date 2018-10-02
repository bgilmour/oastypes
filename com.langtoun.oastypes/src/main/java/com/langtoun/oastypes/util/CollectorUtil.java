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
   * Create a two parameter collector method that creates a new collector
   * backed by a {@link LinkedHashMap}.
   *
   * @param <T>
   *          The type of input elements to the reduction operation.
   * @param <A>
   *          The mutable accumulation type of the reduction operation
   *          (often hidden as an implementation detail).
   * @param <R>
   *          The result type of the reduction operation.
   * @param keyMapper
   *          The function that provides the key value.
   * @param valueMapper
   *          The function that provides the mapped value.
   * @return A new collector that uses a {@link LinkedHashMap}.
   */
  public static <T, A, R> Collector<T, ?, Map<A, R>> toLinkedMap(
    Function<? super T, ? extends A> keyMapper,
    Function<? super T, ? extends R> valueMapper
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
