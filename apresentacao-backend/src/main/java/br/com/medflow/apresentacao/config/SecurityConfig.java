package br.com.medflow.apresentacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Permitir todas requisições em termos de HTTP (as anotações @PreAuthorize serão aplicadas em métodos)
        http
            .csrf().disable()
            .authorizeHttpRequests(a -> a.anyRequest().permitAll())
            .httpBasic();
        return http.build();
    }
}
