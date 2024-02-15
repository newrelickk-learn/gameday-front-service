package technology.nrkk.demo.front.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http)  {

        http
            .csrf(csrf ->
                csrf.disable()
            )
            .cors(cors ->
                cors.disable()
            )
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/actuator/**", "/static/**", "/login", "/login/**")
                .permitAll()
                .anyExchange().authenticated()
            )
            .httpBasic(withDefaults())
            .formLogin(withDefaults());
        ;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://*");
        config.addAllowedOrigin("https://*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedOriginPattern("http://*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CorsWebFilter corsFilter() {
        return new CorsWebFilter(corsConfiguration());
    }


    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName("JSESSIONID");
        resolver.addCookieInitializer((builder) -> builder.path("/"));
        resolver.addCookieInitializer((builder) -> builder.sameSite("None"));
        resolver.addCookieInitializer((builder) -> builder.secure(false));
        return resolver;
    }


    @Bean
    public UserDetailsManager userDetailsManager() {
        JdbcUserDetailsManager user = new JdbcUserDetailsManager(this.dataSource);
        // ユーザーを追加したい時
        // user.createUser(makeUser("user", "pass", "USER"));

        return user;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public CookieSameSiteSupplier cookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofNone();
    }
}