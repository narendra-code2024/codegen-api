package dev.codegen.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codegen.api.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(
            MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        // Null body (e.g. 204 No Content) — don't wrap
        if (body == null) {
            return null;
        }

        // Already wrapped by GlobalExceptionHandler or SecurityExceptionHandler
        if (body instanceof ApiResponse) {
            return body;
        }

        // Exclude raw response formats (Swagger, Actuator)
        String path = request.getURI().getPath();
        if (path.contains("/v3/api-docs")
                || path.contains("/swagger-ui")
                || path.contains("/actuator")) {
            return body;
        }

        ApiResponse<Object> apiResponse = ApiResponse.success(body);

        // String responses must be serialized directly to prevent ClassCastException
        // in Spring's StringHttpMessageConverter
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(apiResponse);
            } catch (Exception e) {
                return body;
            }
        }

        return apiResponse;
    }
}
