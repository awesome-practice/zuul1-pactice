package com.practice.zuul1.gateway.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Luo Bao Ding
 * @since 2018/6/15
 */
public class MyFallbackProvider implements FallbackProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyFallbackProvider.class);
    private static final HttpHeaders headers = new HttpHeaders();

    static {
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    /**
     * <h1>should use service id</h1>
     */
    @Override
    public String getRoute() {
//        return "ribbon-practice-server-say-hello";/* service id */
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
//        LOGGER.error("fallback", cause);
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 200;
            }

            @Override
            public String getStatusText() throws IOException {
                return "ok";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                String msg = "zuul fallback: " + cause.getMessage();
                return new ByteArrayInputStream(msg.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
                return headers;
            }
        };
    }

}
