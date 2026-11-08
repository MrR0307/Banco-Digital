package mz.banco.client_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {

            String token = authorization.substring(7);

            if (jwtService.validarToken(token)) {

                String identificador = jwtService.extrairEmail(token);
                String role = jwtService.extrairRole(token);

                if ("CLIENTE".equals(role)) {

                    Long contaId = jwtService.extrairContaId(token);

                    boolean alterarPin = jwtService.extrairAlterarPin(token);

                    String caminho = request.getRequestURI();

                    Pattern padrao = Pattern.compile("/api/contas/(\\d+)/pin");
                    Matcher matcher = padrao.matcher(caminho);

                    boolean endpointAlterarPin =
                            "PATCH".equalsIgnoreCase(request.getMethod())
                            && matcher.matches();

                    if (endpointAlterarPin) {

                        Long idRecebido = Long.valueOf(matcher.group(1));

                        if (!contaId.equals(idRecebido)) {
                            response.sendError(
                                    HttpServletResponse.SC_FORBIDDEN,
                                    "Nao pode alterar o PIN de outra conta."
                            );
                            return;
                        }

                    } else if (alterarPin) {

                        response.sendError(
                                HttpServletResponse.SC_FORBIDDEN,
                                "Altere o PIN inicial antes de continuar."
                        );
                        return;
                    }
                }

                UsernamePasswordAuthenticationToken autenticacao =
                        new UsernamePasswordAuthenticationToken(
                                identificador,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(autenticacao);
            }
        }

        filterChain.doFilter(request, response);
    }
}