package org.tiger.spi.factory;

import java.util.function.Supplier;

/**
 * {@link Factory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 10:22 周二
 */
@FunctionalInterface
public interface Factory<T> extends Supplier<T> {
}
