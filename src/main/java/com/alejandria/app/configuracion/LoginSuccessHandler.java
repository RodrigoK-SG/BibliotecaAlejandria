package com.alejandria.app.configuracion;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // Ruta por defecto en caso de que algo falle o el rol no coincida
        String redirectUrl = "/"; 
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            // Spring Security por defecto suele agregar el prefijo "ROLE_", 
            // pero si tu base de datos o UserDetailsService lo manda limpio, se compara así:
            String roleName = grantedAuthority.getAuthority();
            
            if (roleName.equals("ADMINISTRADOR") || roleName.equals("ROLE_ADMINISTRADOR")) {
                redirectUrl = "/admin";
                break;
            } else if (roleName.equals("ALMACENERO") || roleName.equals("ROLE_ALMACENERO")) {
                redirectUrl = "/almacen";
                break;
            } else if (roleName.equals("CLIENTE_WEB") || roleName.equals("ROLE_CLIENTE_WEB")) {
                redirectUrl = "/tienda"; // O "/catalogo/perfil" si decides mantener esa ruta
                break;
            }
        }
        
        response.sendRedirect(redirectUrl);
    }
}