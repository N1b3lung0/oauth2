package es.n1b3lung0.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
@EnableMethodSecurity
public class ProblemDetailsAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public ProblemDetailsAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);

        if (authException.getCause() instanceof JwtValidationException validation) {
            ProblemDetail detail = ProblemDetail.forStatus(401);
            detail.setType(URI.create("https://tools.ietf.org/html/rfc6750#section-3.1"));
            detail.setTitle("Invalid Token");
            detail.setProperty("errors", validation.getErrors());
            this.mapper.writeValue(response.getWriter(), detail);
        }
    }

    @Bean
    SecurityFilterChain appSecurity(
            HttpSecurity http,
            ProblemDetailsAuthenticationEntryPoint entryPoint
    ) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET,"/exercises/**")
                        .hasAuthority("SCOPE_exercise:read")
                        .requestMatchers("/exercises/**")
                        .hasAuthority("SCOPE_exercise:write")
                        .anyRequest().authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2
                        .authenticationEntryPoint(entryPoint)
                        .jwt(Customizer.withDefaults())
                );
        return http.build();
    }
}
