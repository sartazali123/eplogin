package com.eprisons.login.exception;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    private final BearerTokenAccessDeniedHandler delegate =
            new BearerTokenAccessDeniedHandler();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception)
            throws IOException {

        delegate.handle(request, response, exception);

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE);

        ApiError error = ApiError.of(
                403,
                "Forbidden",
                "You do not have permission to access this resource",
                request.getRequestURI()
        );

        mapper.writeValue(
                response.getOutputStream(),
                error
        );
    }
}

