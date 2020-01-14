package org.tiger.spi.annotation;

import java.lang.annotation.*;

/**
 * {@link SPI}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 09:52 周二
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SPI {

    /**
     * 扩展点名。
     * @return 扩展点名
     */
    String value() default "";

    /**
     * 加载顺序
     * @return 顺序
     */
    int order() default Integer.MAX_VALUE;
}
