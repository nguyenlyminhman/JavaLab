package com.lab.core.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class ExternalApiClient {

    private final RestClient restClient;

    public ExternalApiClient(RestClient.Builder builder) {
        this.restClient = builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // =========================
    // GET
    // =========================
    public <T> T get(
            String url,
            Map<String, String> headers,
            Map<String, Object> queryParams,
            ParameterizedTypeReference<T> responseType
    ) {
        try {
            RestClient.RequestHeadersSpec<?> request = restClient.get()
                    .uri(uriBuilder -> {
                        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

                        if (queryParams != null) {
                            queryParams.forEach(builder::queryParam);
                        }

                        return builder.build().toUri();
                    });

            if (headers != null) {
                request.headers(httpHeaders -> httpHeaders.setAll(headers));
            }

            return request
                    .retrieve()
                    .body(responseType);

        } catch (RestClientResponseException ex) {
            throw buildException(ex);
        } catch (Exception ex) {
            throw new RuntimeException("GET API call failed: " + ex.getMessage(), ex);
        }
    }

    // =========================
    // POST
    // =========================
    public <T, R> R post(
            String url,
            Map<String, String> headers,
            T requestBody,
            ParameterizedTypeReference<R> responseType
    ) {
        try {
            RestClient.RequestBodySpec request = restClient.post()
                    .uri(url);

            if (headers != null) {
                request.headers(httpHeaders -> httpHeaders.setAll(headers));
            }

            return request
                    .body(requestBody)
                    .retrieve()
                    .body(responseType);

        } catch (RestClientResponseException ex) {
            throw buildException(ex);
        } catch (Exception ex) {
            throw new RuntimeException("POST API call failed: " + ex.getMessage(), ex);
        }
    }

    // =========================
    // ERROR HANDLER
    // =========================
    private RuntimeException buildException(RestClientResponseException ex) {
        return new RuntimeException(
                "External API error: status=" + ex.getStatusCode() +
                        ", body=" + ex.getResponseBodyAsString(),
                ex
        );
    }
}
