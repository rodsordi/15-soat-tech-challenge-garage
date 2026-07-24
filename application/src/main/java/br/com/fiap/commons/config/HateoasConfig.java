package br.com.fiap.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

@Configuration
public class HateoasConfig {

    @Bean
    public LinkRelationProvider linkRelationProvider() {
        return new DefaultLinkRelationProvider();
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public ForwardedHeaderTransformer forwardedHeaderTransformer() {
        return new ForwardedHeaderTransformer();
    }
}
