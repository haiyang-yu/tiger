package org.tiger.sdk.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tiger.sdk.filter.CorsFilter;

import javax.servlet.Filter;

/**
 * {@link FilterConfig}
 * 跨域拦截过滤器
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-09 13:56 周四
 */
@Slf4j
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> corsFilter() {
        log.info("start init CORS filter...");
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CorsFilter());
        bean.setOrder(0);
        return bean;
    }
}
