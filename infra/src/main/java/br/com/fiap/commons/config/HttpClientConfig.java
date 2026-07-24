package br.com.fiap.commons.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static org.springframework.web.client.support.RestClientAdapter.create;
import static org.springframework.web.service.invoker.HttpServiceProxyFactory.builderFor;

@RequiredArgsConstructor
@Configuration
public class HttpClientConfig {

    private static final ClientHttpRequestFactory FACTORY = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());

    private final LogInterceptor logInterceptor;

    public <T> T createClient(
            RestClient.Builder builder,
            Class<T> type,
            String baseUrl,
            ClientHttpRequestInterceptor... interceptors) {
        builder.requestFactory(FACTORY)
                .baseUrl(baseUrl);

        if (interceptors != null)
            for (var interceptor : interceptors)
                builder.requestInterceptor(interceptor);

        builder.requestInterceptor(logInterceptor);

        var restClient = builder.build();
        var adapter = create(restClient);
        var factory = builderFor(adapter).build();
        return factory.createClient(type);
    }
}