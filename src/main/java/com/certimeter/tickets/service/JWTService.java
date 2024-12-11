package com.certimeter.tickets.service;

import com.certimeter.tickets.enumeration.HttpResponseEnum;
import com.certimeter.tickets.exception.FailureException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;


@Service
public class JWTService {
    // HINT:    https://www.baeldung.com/spring-classpath-file-access#2-using-value
    @Value("classpath:jwt/access_token.key")
    private Resource accessTokenKeyFile;

    //--------------------
    private SecretKey accessTokenKey;
    //--------------------

    // init(): Initializes the secret keys by reading the content of the resource files. This method is annotated with @PostConstruct, meaning it runs after the bean’s properties have been set.
    @PostConstruct
    public void init() throws IOException {
        this.accessTokenKey = Keys.hmacShaKeyFor(accessTokenKeyFile.getContentAsByteArray());
    }


    // Validates the given access token. If the token is expired, invalid, or malformed, it throws a FailureException with the appropriate response enum.
    public void validateAccessToken(String accessToken) {
        try {

            this.validateToken(accessTokenKey, accessToken);

        } catch (ExpiredJwtException e) {
            throw new FailureException(HttpResponseEnum.EXPIRED_ACCESS_TOKEN);
        } catch (SignatureException e) {
            throw new FailureException(HttpResponseEnum.INVALID_ACCESS_TOKEN);
        } catch (MalformedJwtException e) {
            throw new FailureException(HttpResponseEnum.MALFORMED_ACCESS_TOKEN);
        } catch (Exception e) {
            throw new FailureException(HttpResponseEnum.UNEXPECTED_ERROR);
        }
    }

    // Retrieves a specific claim from the access token after validating it.
    public <T> T getClaimFromAccessToken(String accessToken, String fieldName, Class<T> fieldClass) {
        this.validateAccessToken(accessToken);
        return this.getClaimFromToken(accessTokenKey, accessToken, fieldName, fieldClass);
    }

    public boolean isAccessTokenExpired(String accessToken) {
        try {
            validateAccessToken(accessToken);
            return false;       // token valid
        } catch (FailureException e) {
            if (e.getHttpResponseEnum().equals(HttpResponseEnum.EXPIRED_ACCESS_TOKEN))
                return true;    // token expired
            throw e;            // token malformed --> ERROR
        }
    }

    /**************************************************************************************/

    private void validateToken(SecretKey secretKey, String token) {
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }

    private <T> T getClaimFromToken(SecretKey secretKey, String token, String fieldName, Class<T> fieldClass) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        return claims.getPayload().get(fieldName, fieldClass);
    }

    public <T> T getClaimFromExpiredAccessToken(String accessToken, String fieldName, Class<T> fieldClass) {
        try {
            this.validateToken(accessTokenKey, accessToken);
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            return claims.get(fieldName, fieldClass);
        } catch (SignatureException e) {
            throw new FailureException(HttpResponseEnum.INVALID_ACCESS_TOKEN);
        } catch (MalformedJwtException e) {
            throw new FailureException(HttpResponseEnum.MALFORMED_ACCESS_TOKEN);
        } catch (Exception e) {
            throw new FailureException(HttpResponseEnum.UNEXPECTED_ERROR);
        }
        return null; // or throw an exception if the token is not expired
    }
}


