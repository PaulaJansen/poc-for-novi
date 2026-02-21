package nl.novi.endassignment.pocbackend.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableMethodSecurity
@Configuration
public class MySecurityConfig {
    private final MyUserDetailsService myUserDetailsService;

    public MySecurityConfig(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    protected SecurityFilterChain filter(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**").permitAll()
                                .requestMatchers("/paulajansen-ALOA.yaml").permitAll()

                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/uploads/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                                .requestMatchers("/register", "/login").permitAll()

                                .requestMatchers(HttpMethod.GET, "/artworks/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/artists/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/artists/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/visitors/**").permitAll()

//                        // Normaal mag niet iedereen alle visitors opvragen, maar voor testen alles open gezet
                                .requestMatchers(HttpMethod.GET, "/visitors/**").permitAll()

                                .requestMatchers(HttpMethod.POST, "/artworks/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/**").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/**").authenticated()

//                        // Normaal alleen voor adminrol, maar gezien die er nog niet is voor nu open om te kunnen testen
                                .requestMatchers("/roles/**").permitAll()
                                .requestMatchers("/genres/**").permitAll()
                                .requestMatchers("/users/**").permitAll()
                                .requestMatchers("/visitors/**").permitAll()

                                .anyRequest().denyAll()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }
}
