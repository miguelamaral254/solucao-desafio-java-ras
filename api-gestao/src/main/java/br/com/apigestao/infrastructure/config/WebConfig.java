package br.com.apigestao.infrastructure.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {

    @Bean
    public FilterRegistrationBean<RequestMdcFilter> loggingFilter() {
        FilterRegistrationBean<RequestMdcFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestMdcFilter());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}