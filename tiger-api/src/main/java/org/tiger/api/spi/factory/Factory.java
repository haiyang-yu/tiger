package org.tiger.api.spi.factory;

import java.util.function.Supplier;

/**
 * {@link Factory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-11 10:15 周六
 */
@FunctionalInterface
public interface Factory<T> extends Supplier<T> {
}
