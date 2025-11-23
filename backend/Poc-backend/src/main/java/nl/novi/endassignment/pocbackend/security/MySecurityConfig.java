package nl.novi.endassignment.pocbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class MySecurityConfig {
    private final DataSource dataSource;

    public MySecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filter(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "visitors/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "visitors/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "visitors/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "visitors/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "visitors/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "artists/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "artists/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "artists/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "artists/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "artists/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "artworks/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "artworks/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "artworks/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "artworks/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "artworks/**").authenticated()

                        .requestMatchers("/auth").permitAll()
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("SELECT username, password, enabled" +
                        " FROM users" +
                        " WHERE username=?")
                .authoritiesByUsernameQuery("SELECT username, authority" +
                        " FROM authorities " +
                        " WHERE username=?");
        return authenticationManagerBuilder.build();
    }
}
