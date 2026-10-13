package com.eprisons.login.exception;




import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    private final BearerTokenAuthenticationEntryPoint delegate =
            new BearerTokenAuthenticationEntryPoint();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException {

        // Preserve the standard Bearer authentication header
        delegate.commence(request, response, exception);

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE);

        ApiError error = ApiError.of(
                401,
                "Unauthorized",
                "Authentication required or token invalid",
                request.getRequestURI()
        );

        mapper.writeValue(
                response.getOutputStream(),
                error
        );
    }
}

