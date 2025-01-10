package com.certimeter.tickets.filter;

import com.certimeter.tickets.enumeration.HttpResponseEnum;
import com.certimeter.tickets.enumeration.UserRoleEnum;
import com.certimeter.tickets.exception.FailureException;
import com.certimeter.tickets.dto.HttpResponse;
import com.certimeter.tickets.requestcontext.RequestContext;
import com.certimeter.tickets.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    private final RequestContext requestContext;

    private static final Logger LOG = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    public JWTAuthorizationFilter(JWTService jwtService, RequestContext requestContext) {
        this.jwtService = jwtService;

        this.requestContext = requestContext;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        LOG.info("--------- START - GET {}?{} ------", request.getRequestURI(), request.getQueryString());
        LOG.info("Endpoint: {}", request.getRequestURI());
        LOG.info("Method: {}", request.getMethod());
        LOG.info("Query string: {}", request.getQueryString());


        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If no valid Authorization header, block the request
            returnCustomResponse(response, HttpStatus.UNAUTHORIZED, HttpResponseEnum.INVALID_ACCESS_TOKEN);
            return;
        }

        String accessToken = authHeader.substring(7); // Extract the token

        try {
            jwtService.validateAccessToken(accessToken); // Validate token
            String role = jwtService.getClaimFromAccessToken(accessToken, "role", String.class);
            Long userId = jwtService.getClaimFromAccessToken(accessToken, "id_user", Long.class);
            requestContext.setRole(UserRoleEnum.valueOf(role));
            requestContext.setAccessToken(accessToken);
            requestContext.setUserId(userId);
        } catch (FailureException e) {
            if (isTokenRefreshRequest(e, request)) {
                filterChain.doFilter(request, response);
            } else {
                returnCustomResponse(response, e.getHttpResponseEnum().getHttpStatus(), e.getHttpResponseEnum());
            }
            return;
        }

        // If token is valid, proceed to the next filter
        filterChain.doFilter(request, response);
    }

    private boolean isTokenRefreshRequest(FailureException e, HttpServletRequest request) {
        return e.getHttpResponseEnum().equals(HttpResponseEnum.EXPIRED_ACCESS_TOKEN)
                && "/auth/login/refresh".equals(request.getRequestURI());
    }

    private void returnCustomResponse(ServletResponse response, HttpStatus httpStatus, HttpResponseEnum HttpResponseEnum) throws IOException {
        HttpResponse customObjectResponse = new HttpResponse(HttpResponseEnum); //HttpResponse mia
        byte[] responseToSend = restResponseBytes(customObjectResponse, MediaType.APPLICATION_JSON);
        ((HttpServletResponse) response).setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        ((HttpServletResponse) response).setStatus(httpStatus.value());
        response.getOutputStream().write(responseToSend);
    }

    //This method serializes the HttpResponse object into a byte array
    private byte[] restResponseBytes(HttpResponse response, MediaType mediaType) throws IOException {
        ObjectMapper mapper = mediaType == MediaType.APPLICATION_JSON ? new ObjectMapper() : new XmlMapper();
        String serialized = mapper.writeValueAsString(response);
        return serialized.getBytes();
    }
}

