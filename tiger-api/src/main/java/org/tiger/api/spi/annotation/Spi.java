package org.tiger.api.spi.annotation;

import java.lang.annotation.*;

/**
 * {@link Spi}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-11 09:42 周六
 */
@Documented
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Spi {

    /**
     * SPI 名称
     * @return SPI 名称
     */
    String value() default "";

    /**
     * 排序顺序
     * @return 排序顺序
     */
    int order() default Integer.MAX_VALUE;
}
