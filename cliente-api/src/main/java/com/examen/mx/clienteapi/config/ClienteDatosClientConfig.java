package com.examen.mx.clienteapi.config;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class ClienteDatosClientConfig {

    @Bean
    RestClient clienteDatosRestClient(
            RestClient.Builder builder,
            @Value("${cliente-datos.base-url}") String baseUrl,
            @Value("${cliente-datos.timeout:3s}") Duration timeout
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(timeout);

        return builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .requestInterceptor((request, body, execution) -> {
                    String correlationId =
                            ThreadContext.get(CorrelationIdFilter.CONTEXT_KEY);

                    if (correlationId != null && !correlationId.isBlank()) {
                        request.getHeaders().set(
                                CorrelationIdFilter.HEADER,
                                correlationId
                        );
                    }

                    return execution.execute(request, body);
                })
                .build();
    }
}


