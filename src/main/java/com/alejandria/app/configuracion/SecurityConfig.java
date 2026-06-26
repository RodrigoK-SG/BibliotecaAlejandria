package com.alejandria.app.configuracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    // Fundamental para encriptar/desencriptar las contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactivado por ahora para facilitar el desarrollo
            
            .authorizeHttpRequests(auth -> auth
                // 1. Rutas PÚBLICAS (Accesibles por cualquiera sin iniciar sesión)
                .requestMatchers("/error", "/", "/tienda", "/tienda/libro/**", 
                                 "/tienda/vista-login", "/registro",
                                 "/tienda/contacto", "/tienda/reglamento", "/tienda/terminos", 
                                 "/tienda/reclamaciones", "/tienda/envio", "/tienda/devolucion").permitAll()
                
                // Recursos estáticos (CSS, JS, Imágenes)
                .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**").permitAll()
                
                // 2. Rutas PROTEGIDAS POR ROL ESTRICTO
                .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")
                .requestMatchers("/almacen/**").hasAuthority("ALMACENERO")
                
                // 3. Rutas de Cliente Web (Carrito, Checkout, Perfil)
                // Se permite al ADMIN entrar para poder hacer pruebas de compra en desarrollo
                .requestMatchers("/tienda/perfil/**", "/tienda/carrito/**", "/tienda/pagos", "/tienda/checkout").hasAnyAuthority("CLIENTE_WEB", "ADMINISTRADOR")
                
                // Cualquier otra ruta requiere estar logueado
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/tienda/vista-login") // La URL donde está tu HTML de login
                .loginProcessingUrl("/login") // La ruta que procesa el <form th:action="@{/login}"> (Spring lo hace automático)
                .successHandler(loginSuccessHandler) // ¡Usamos nuestro enrutador simplificado!
                .failureUrl("/tienda/vista-login?error=true") // Si falla, recarga con error
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/tienda") // Al salir, enviamos al usuario a la página de inicio pública
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}