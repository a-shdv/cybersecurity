package com.company.cybersecurity.configs;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static int failureCount = 0;
    private AuthenticationException exception;

    public AuthenticationException getException() {
        return exception;
    }

    public static int getFailureCount() {
        return failureCount;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        failureCount++;
        if (failureCount >= 3) {
            System.exit(0);
        }

        this.exception = exception;

        super.onAuthenticationFailure(request, response, exception);
    }
}
