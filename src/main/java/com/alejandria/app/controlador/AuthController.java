package com.alejandria.app.controlador;

import com.alejandria.app.modelo.Cliente;
import com.alejandria.app.modelo.Rol;
import com.alejandria.app.modelo.Usuario;
import com.alejandria.app.modelo.enums.TipoDocumento;
import com.alejandria.app.repositorio.ClienteRepositorio;
import com.alejandria.app.repositorio.RolRepositorio;
import com.alejandria.app.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepositorio usuarioRepository;
    private final RolRepositorio rolRepository;
    private final ClienteRepositorio clienteRepositorio; // Añadimos el repositorio del Cliente
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/registro")
    public String registrarUsuarioWeb(
            @RequestParam("nombreRazonSocial") String nombre,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("nroDocumento") String nroDocumento) {
        
        // 1. Verificamos que el email no exista en Usuarios
        if (usuarioRepository.findByEmail(email).isPresent()) {
            return "redirect:/tienda/vista-login?errorRegistro=EmailYaExiste";
        }

        // 2. Verificamos que el documento no exista en Clientes
        if (clienteRepositorio.findByNumeroDocumento(nroDocumento).isPresent()) {
            return "redirect:/tienda/vista-login?errorRegistro=DocumentoYaExiste";
        }

        // 3. Creamos el Usuario (Credenciales)
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreCompleto(nombre); 
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(password)); 
        nuevoUsuario.setActivo(true);
        
        // Asignamos el rol usando getRoles().add() porque ahora es un Set en lugar de List
        Rol rolCliente = rolRepository.findByNombre("CLIENTE_WEB")
                .orElseThrow(() -> new RuntimeException("El rol CLIENTE_WEB no existe en la BD"));
        nuevoUsuario.getRoles().add(rolCliente);

        // Guardamos el Usuario para que la BD genere su ID
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 4. Creamos el Perfil de Cliente y lo vinculamos al Usuario
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setUsuario(usuarioGuardado); // Enlazamos el usuario recién creado
        nuevoCliente.setTipoDocumento(TipoDocumento.DNI); // Puedes ajustar esto si recibes el tipo desde el HTML
        nuevoCliente.setNumeroDocumento(nroDocumento);
        
        // Guardamos el Cliente
        clienteRepositorio.save(nuevoCliente);

        // 5. Redirigimos al login para que ingrese con su nueva cuenta
        return "redirect:/tienda/vista-login?registroExitoso=true";
    }
}