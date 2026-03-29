package com.example.moneymanager.advices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    // ✅ Create ObjectMapper once and register JavaTimeModule for LocalDateTime support
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // 1. Skip wrapping for Swagger/Actuator endpoints
        List<String> allowedRoutes = List.of("/v3/api-docs", "/actuator");
        boolean isAllowed = allowedRoutes.stream()
                .anyMatch(route -> request.getURI().getPath().contains(route));

        if (isAllowed) return body;

        // 2. Get HTTP status
        HttpStatus status = HttpStatus.resolve(
                ((ServletServerHttpResponse) response).getServletResponse().getStatus()
        );

        // 3. Skip wrapping for specific statuses or null bodies
        if (status == HttpStatus.NO_CONTENT ||
                status == HttpStatus.NOT_FOUND ||
                status == HttpStatus.INTERNAL_SERVER_ERROR ||
                status == HttpStatus.CONFLICT ||
                body == null) {
            return body;
        }

        // 4. Skip if already wrapped (prevents double wrapping)
        if (body instanceof ApiResponse<?>) {
            return body;
        }

        // 5. Build the wrapper
        ApiResponse<Object> apiResponse = new ApiResponse<>(body);

        // 6. 🔥 THE CRITICAL FIX:
        // If the body is a String, we MUST return a String.
        // StringHttpMessageConverter expects a String, not an ApiResponse object.
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(apiResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting ApiResponse to String", e);
            }
        }

        return apiResponse;
    }
}