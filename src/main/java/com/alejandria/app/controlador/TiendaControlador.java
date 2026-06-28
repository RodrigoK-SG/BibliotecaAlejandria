package com.alejandria.app.controlador;

import com.alejandria.app.modelo.*;
import com.alejandria.app.modelo.enums.MetodoPago;
import com.alejandria.app.modelo.enums.TipoComprobante;
import com.alejandria.app.repositorio.CategoriaRepositorio;
import com.alejandria.app.repositorio.ClienteRepositorio;
import com.alejandria.app.repositorio.LibroRepositorio;
import com.alejandria.app.servicio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tienda")
@RequiredArgsConstructor
public class TiendaControlador {

    private final LibroServicio libroServicio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final CarritoServicio carritoServicio;
    private final PedidoServicio pedidoServicio;
    private final UsuarioServicio usuarioServicio;
    private final ClienteRepositorio clienteRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final LibroRepositorio libroRepositorio;

    // --- MÉTODO AUXILIAR PARA OBTENER EL CLIENTE LOGUEADO ---
    private Cliente obtenerClienteSesion(Principal principal) {
        if (principal == null) return null;
        Usuario usuario = usuarioServicio.buscarPorEmail(principal.getName()).orElse(null);
        if (usuario == null) return null;
        return clienteRepositorio.findByUsuarioId(usuario.getId()).orElse(null);
    }

    // ==========================================
    // 1. CATÁLOGO PÚBLICO
    // ==========================================
    @GetMapping
    public String verCatalogoTienda(
            @RequestParam(name = "buscar", required = false) String buscar,
            @RequestParam(name = "categorias", required = false) List<Integer> categoriasIds,
            @RequestParam(name = "formatos", required = false) List<String> formatosSeleccionados,
            Model model) {
        List<Libro> libros;
        // 1. Filtro principal por Búsqueda de Texto (Título o ISBN)
        if (buscar != null && !buscar.trim().isEmpty()) {
            libros = libroRepositorio.findByTituloContainingIgnoreCaseOrIsbnContainingIgnoreCase(buscar, buscar);
        } else {
            libros = libroRepositorio.findAll(); 
            
        }
        
        // 2. Filtro por Categorías (Si el usuario marcó alguna)
        if (categoriasIds != null && !categoriasIds.isEmpty()) {
            libros = libros.stream()
                .filter(libro -> libro.getCategorias().stream()
                        .anyMatch(cat -> categoriasIds.contains(cat.getId())))
                .collect(Collectors.toList());
        }   

        // 3. Filtro por Formatos (Si el usuario marcó alguno)
        if (formatosSeleccionados != null && !formatosSeleccionados.isEmpty()) {
            libros = libros.stream()
                .filter(libro -> libro.getFormato() != null && formatosSeleccionados.contains(libro.getFormato().name()))
                .collect(Collectors.toList());
        }

        model.addAttribute("libros", libros);
        
        // Asegúrate de enviar también las categorías a la vista para renderizar el menú lateral
        model.addAttribute("categorias", categoriaRepositorio.findAll());
        
        return "cliente/index";
    }
    

    @GetMapping("/libro/{slug}")
    public String detalleLibro(@PathVariable("slug") String slug, Model model) {
        Libro libro = libroServicio.buscarPorSlug(slug).orElse(null);
        if (libro == null || !libro.getActivo()) return "redirect:/tienda";
        
        model.addAttribute("libro", libro);
        return "cliente/detalle-libro";
    }

    // ==========================================
    // 2. CARRITO Y CHECKOUT (Privado)
    // ==========================================
    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(Principal principal,
                                   @RequestParam("libroId") Integer libroId, 
                                   @RequestParam("cantidad") Integer cantidad) {
        Cliente cliente = obtenerClienteSesion(principal);
        if (cliente == null) return "redirect:/tienda/vista-login";

        carritoServicio.agregarLibroACarrito(cliente.getId(), libroId, cantidad);
        return "redirect:/tienda/carrito";
    }

    @GetMapping("/carrito")
    public String verCarritoYPagos(Principal principal, Model model) {
        Cliente cliente = obtenerClienteSesion(principal);
        if (cliente == null) return "redirect:/tienda/vista-login";

        Carrito carrito = carritoServicio.obtenerPorCliente(cliente.getId()).orElse(new Carrito());
        List<CarritoDetalle> items = carrito.getDetalles();
        
        BigDecimal subtotal = items.stream()
                .map(item -> item.getLibro().getPrecioVentaActual().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal costoEnvio = new BigDecimal("5.99");

        model.addAttribute("itemsCarrito", items);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("costoEnvio", costoEnvio);
        model.addAttribute("totalReal", subtotal.add(costoEnvio));
        
        // 👇 ESTA ES LA LÍNEA QUE FALTABA
        model.addAttribute("cliente", cliente);
        
        return "cliente/pagos";
    }

    @PostMapping("/checkout")
    public String procesarCompra(Principal principal,
                                 @RequestParam("direccionCompleta") String direccionStr,
                                 @RequestParam("metodoPago") String metodoPagoStr) {
        Cliente cliente = obtenerClienteSesion(principal);
        if (cliente == null) return "redirect:/tienda/vista-login";

        // Creamos una dirección al vuelo (en un proyecto más grande la elegirías de una lista)
        DireccionEnvio direccion = new DireccionEnvio();
        direccion.setCliente(cliente);
        direccion.setDireccionCompleta(direccionStr);
        direccion.setEtiqueta("Principal");
        direccion.setCiudad("Lima"); // Por defecto

        MetodoPago metodoPago = MetodoPago.valueOf(metodoPagoStr);
        TipoComprobante comprobante = TipoComprobante.BOLETA; // Por defecto para B2C

        Pedido pedidoGenerado = pedidoServicio.procesarVentaCheckout(cliente.getId(), direccion, metodoPago, comprobante);

        return "redirect:/tienda/pedido-exitoso?id=" + pedidoGenerado.getId();
    }

    @GetMapping("/pedido-exitoso")
    public String verPedidoExitoso(@RequestParam("id") Integer pedidoId, Model model) {
        model.addAttribute("pedido", pedidoServicio.findById(pedidoId).orElse(null));
        return "cliente/pedido-exitoso";
    }

    // ==========================================
    // 3. PANEL DE PERFIL (Privado)
    // ==========================================
    @GetMapping("/perfil")
    public String perfil(Principal principal, Model model) {
        Cliente cliente = obtenerClienteSesion(principal);
        if (cliente == null) return "redirect:/tienda/vista-login";

        model.addAttribute("cliente", cliente);
        model.addAttribute("usuario", cliente.getUsuario());
        model.addAttribute("listaPedidos", pedidoServicio.listarPedidosPorCliente(cliente.getId()));
        
        return "cliente/perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarDatosPerfil(Principal principal,
                                        @RequestParam("nombreRazonSocial") String nombre,
                                        @RequestParam("telefono") String telefono,
                                        @RequestParam(value = "passwordActual", required = false) String passwordActual,
                                        @RequestParam(value = "passwordNueva", required = false) String passwordNueva) {
        Cliente cliente = obtenerClienteSesion(principal);
        if (cliente == null) return "redirect:/tienda/vista-login";

        Usuario usuario = cliente.getUsuario();
        usuario.setNombreCompleto(nombre);
        cliente.setTelefono(telefono);
        
        if (passwordActual != null && !passwordActual.isEmpty() && passwordNueva != null && !passwordNueva.isEmpty()) {
            if (passwordEncoder.matches(passwordActual, usuario.getPasswordHash())) {
                usuario.setPasswordHash(passwordEncoder.encode(passwordNueva));
            } else {
                return "redirect:/tienda/perfil?errorPassword=true";
            }
        }
        
        usuarioServicio.actualizarEstado(usuario.getId(), true); // Guarda el usuario
        clienteRepositorio.save(cliente);
        
        return "redirect:/tienda/perfil?cambioExitoso=true";
    }

    // ==========================================
    // 4. VISTAS ESTÁTICAS Y LOGIN
    // ==========================================
    @GetMapping("/vista-login") 
    public String login() { 
    	return "cliente/login"; 
    }
    
    @GetMapping("/contacto") 
    public String contacto() { 
    	return "cliente/contacto"; 
    }
    
    @GetMapping("/reglamento") 
    public String reglamento() { 
    	return "cliente/reglamento"; 
    }
    
    @GetMapping("/terminos") 
    public String terminos() { 
    	return "cliente/terminos"; 
    }
    
    @GetMapping("/reclamaciones") 
    public String reclamaciones() { 
    	return "cliente/reclamaciones"; 
    }
    
    @GetMapping("/envio") 
    public String envio() { 
    	return "cliente/envio"; 
    }
    
    @GetMapping("/devolucion") 
    public String devolucion() { 
    	return "cliente/devolucion"; 
    }
    
    @GetMapping("/nuestra-historia") 
    public String historia() { 
    	return "cliente/historia"; 
    }
}