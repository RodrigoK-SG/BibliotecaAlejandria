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
        String redirectUrl = "/catalogo"; 
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String roleName = grantedAuthority.getAuthority();
            
            if (roleName.equals("ADMINISTRADOR")) {
                redirectUrl = "/admin";
                break;
            } else if (roleName.equals("VENDEDOR")) {
                redirectUrl = "vendedor/caja";
                break;
            } else if (roleName.equals("BIBLIOTECARIO")) {
                redirectUrl = "bibliotecario/prestamos";
                break;
            } else if (roleName.equals("ALMACENERO")) {
                redirectUrl = "/almacen";
                break;
            } else if (roleName.equals("CLIENTE_WEB")) { 
                redirectUrl = "/catalogo/perfil"; 
                break;
            }
        }
        response.sendRedirect(redirectUrl);
    }
}