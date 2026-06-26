package com.alejandria.app.filtro;

import com.alejandria.app.modelo.Rol;
import com.alejandria.app.modelo.Usuario;
import com.alejandria.app.repositorio.RolRepositorio;
import com.alejandria.app.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepositorio usuarioRepository;
    private final RolRepositorio rolRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/registro")
    public String registrarUsuarioWeb(
            @RequestParam("nombreRazonSocial") String nombre,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("nroDocumento") String nroDocumento) { // 1. Agregamos el parámetro para capturar el DNI
        
        // 1. Verificamos que el email no exista
        if (usuarioRepository.findByEmail(email).isPresent()) {
            return "redirect:/catalogo/vista-login?errorRegistro=EmailYaExiste";
        }

        // 2. Creamos el usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreCompleto(nombre); 
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(password)); 
        nuevoUsuario.setActivo(true);
        
        // ¡ESTA ES LA LÍNEA CLAVE! 
        // 2. Le asignamos el DNI al objeto antes de mandarlo a guardar
        nuevoUsuario.setNroDocumento(nroDocumento); 

        // 3. Asignamos el rol por defecto: CLIENTE_WEB
        Rol rolCliente = rolRepository.findByNombre("CLIENTE_WEB")
                .orElseThrow(() -> new RuntimeException("El rol CLIENTE_WEB no existe en la BD"));

        List<Rol> roles = new ArrayList<>();
        roles.add(rolCliente);
        nuevoUsuario.setRoles(roles);

        // 4. Guardamos en la BD (ahora se guardará con todo y DNI)
        usuarioRepository.save(nuevoUsuario);

        // 5. Redirigimos al login para que ingrese con su nueva cuenta
        return "redirect:/catalogo/vista-login?registroExitoso=true";
    }
}