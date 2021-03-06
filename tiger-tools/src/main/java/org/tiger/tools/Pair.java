package org.tiger.tools;

import lombok.Getter;

/**
 * {@link Pair}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 14:17 周二
 */
@Getter
public class Pair<K, V> {

    private final K key;
    private final V value;

    private Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Pair<K, V> of(K k, V v) {
        return new Pair<>(k, v);
    }
}
