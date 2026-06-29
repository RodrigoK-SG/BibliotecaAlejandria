package com.alejandria.app.controlador;

import com.alejandria.app.modelo.*;
import com.alejandria.app.modelo.enums.MetodoPago;
import com.alejandria.app.modelo.enums.TipoComprobante;
import com.alejandria.app.repositorio.CategoriaRepositorio;
import com.alejandria.app.repositorio.ClienteRepositorio;
import com.alejandria.app.repositorio.InventarioVentaRepositorio;
import com.alejandria.app.repositorio.LibroRepositorio;
import com.alejandria.app.servicio.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
    private final InventarioVentaRepositorio inventarioVentaRepositorio;

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
            // 🟢 NUEVO: Agregamos el parámetro para capturar la opción del select
            @RequestParam(name = "orden", defaultValue = "default") String orden, 
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

        // 🟢 NUEVO: Lógica de ordenamiento global (Usando compareTo porque es BigDecimal)
        if ("precio-asc".equals(orden)) {
            libros.sort((l1, l2) -> l1.getPrecioVentaActual().compareTo(l2.getPrecioVentaActual()));
        } else if ("precio-desc".equals(orden)) {
            libros.sort((l1, l2) -> l2.getPrecioVentaActual().compareTo(l1.getPrecioVentaActual()));
        }

        // 🟢 NUEVO: Enviamos la variable "ordenActual" al HTML para que el select no se borre
        model.addAttribute("ordenActual", orden);
        
        model.addAttribute("libros", libros);
        
        // Asegúrate de enviar también las categorías a la vista para renderizar el menú lateral
        model.addAttribute("categorias", categoriaRepositorio.findAll());
        
        return "cliente/index";
    }
    

    @GetMapping("/libro/{slug}")
    public String detalleLibro(@PathVariable("slug") String slug, Model model) {
        Libro libro = libroServicio.buscarPorSlug(slug).orElse(null);
        if (libro == null || !libro.getActivo()) return "redirect:/tienda";
        
        // --- 🟢 LÓGICA DE STOCK AÑADIDA ---
        // Buscamos el inventario usando el ID del libro
        InventarioVenta inventario = inventarioVentaRepositorio.findById(libro.getId()).orElse(null);
        
        // Extraemos la cantidad disponible (si no hay inventario registrado, es 0)
        int stock = (inventario != null) ? inventario.getCantidadDisponible() : 0;
        
        // Pasamos la variable 'stock' a tu HTML de Thymeleaf
        model.addAttribute("stock", stock);
        // -----------------------------------
        
        model.addAttribute("libro", libro);
        return "cliente/detalle-libro";
    }

 // ==========================================
    // AGREGAR AL CARRITO (Validando InventarioVenta)
    // ==========================================
    @PostMapping("/carrito/agregar")
    @ResponseBody
    public ResponseEntity<String> agregarAlCarrito(
            Principal principal,
            @RequestParam("libroId") Integer libroId,
            @RequestParam("cantidad") Integer cantidad) {

        Cliente cliente = obtenerClienteSesion(principal);
        if (cliente == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        // --- 🟢 NUEVA VALIDACIÓN DE STOCK BASADA EN TUS MODELOS ---
        InventarioVenta inventario = inventarioVentaRepositorio.findById(libroId).orElse(null);
        
        if (inventario == null) {
            return ResponseEntity.status(404).body("No se encontró información de inventario para este libro.");
        }

        if (inventario.getCantidadDisponible() <= 0) {
            return ResponseEntity.status(400).body("Lo sentimos, no hay stock disponible de este libro.");
        }

        // Verificar cuántos de este libro YA tiene en el carrito
        Carrito carrito = carritoServicio.obtenerPorCliente(cliente.getId()).orElse(null);
        int cantidadEnCarrito = 0;
        if (carrito != null && carrito.getDetalles() != null) {
            cantidadEnCarrito = carrito.getDetalles().stream()
                    .filter(d -> d.getLibro().getId().equals(libroId))
                    .mapToInt(CarritoDetalle::getCantidad)
                    .sum();
        }

        // Validar si lo que quiere agregar + lo que ya tiene supera lo disponible
        if (inventario.getCantidadDisponible() < (cantidadEnCarrito + cantidad)) {
            return ResponseEntity.status(400).body("Stock insuficiente. Solo hay " + inventario.getCantidadDisponible() + " unidades disponibles y ya tienes " + cantidadEnCarrito + " en tu carrito.");
        }
        // -----------------------------------------------------------

        carritoServicio.agregarLibroACarrito(cliente.getId(), libroId, cantidad);

        return ResponseEntity.ok("OK");
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
    
    @GetMapping("/carrito/resumen")
    @ResponseBody
    public ResponseEntity<?> obtenerResumenCarrito(Principal principal) {

        Cliente cliente = obtenerClienteSesion(principal);
        if (cliente == null) return ResponseEntity.status(401).body(null);

        Carrito carrito = carritoServicio.obtenerPorCliente(cliente.getId())
                .orElse(null);

        if (carrito == null) return ResponseEntity.ok(0);

        int totalItems = carrito.getDetalles()
                .stream()
                .mapToInt(CarritoDetalle::getCantidad)
                .sum();

        return ResponseEntity.ok(totalItems);
    }

    @PostMapping("/checkout")

    public String procesarCompra(Principal principal,

                                 @RequestParam("direccionCompleta") String direccionStr,

                                 @RequestParam("metodoPago") String metodoPagoStr,

                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) { // 1. Agregamos RedirectAttributes

        

        Cliente cliente = obtenerClienteSesion(principal);

        if (cliente == null) return "redirect:/tienda/vista-login";



        // 2. VERIFICACIÓN: Obtener carrito y comprobar si tiene items

        Carrito carrito = carritoServicio.obtenerPorCliente(cliente.getId()).orElse(null);

        if (carrito == null || carrito.getDetalles().isEmpty()) {

            // Enviamos un mensaje de error a la vista de pagos

            redirectAttributes.addFlashAttribute("errorCarrito", "No puedes realizar una compra con el carrito vacío.");

            return "redirect:/tienda/carrito";

        }



        // Creamos la dirección

        DireccionEnvio direccion = new DireccionEnvio();

        direccion.setCliente(cliente);

        direccion.setDireccionCompleta(direccionStr);

        direccion.setEtiqueta("Principal");

        direccion.setCiudad("Lima");



        MetodoPago metodoPago = MetodoPago.valueOf(metodoPagoStr);

        TipoComprobante comprobante = TipoComprobante.BOLETA;



        // Procesar

        Pedido pedidoGenerado = pedidoServicio.procesarVentaCheckout(cliente.getId(), direccion, metodoPago, comprobante);



        return "redirect:/tienda/pedido-exitoso?id=" + pedidoGenerado.getId();

    }

    @GetMapping("/pedido-exitoso")
    public String verPedidoExitoso(@RequestParam("id") Integer pedidoId, Model model) {
        model.addAttribute("pedido", pedidoServicio.findById(pedidoId).orElse(null));
        return "cliente/pedido-exitoso";
    }
    
 // ==========================================
    // ACTUALIZAR CANTIDAD (Validando InventarioVenta)
    // ==========================================
    @PostMapping("/carrito/actualizar-cantidad")
    @ResponseBody
    public ResponseEntity<String> actualizarCantidadCarrito(
            Principal principal,
            @RequestParam("libroId") Integer libroId,
            @RequestParam("nuevaCantidad") Integer nuevaCantidad) {
        try {
            Cliente cliente = obtenerClienteSesion(principal);
            if (cliente == null) return ResponseEntity.status(401).body("No autorizado");

            // --- 🟢 NUEVA VALIDACIÓN DE STOCK BASADA EN TUS MODELOS ---
            InventarioVenta inventario = inventarioVentaRepositorio.findById(libroId).orElse(null);
            
            if (inventario == null) {
                return ResponseEntity.status(404).body("No se encontró información de inventario para este libro.");
            }

            if (inventario.getCantidadDisponible() < nuevaCantidad) {
                return ResponseEntity.status(400).body("Stock insuficiente. Solo quedan " + inventario.getCantidadDisponible() + " unidades disponibles.");
            }
            // -----------------------------------------------------------

            carritoServicio.actualizarCantidad(cliente.getId(), libroId, nuevaCantidad);
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(500).body("Error en el servidor: " + e.getMessage());
        }
    }

    @PostMapping("/carrito/eliminar-item")
    @ResponseBody
    public ResponseEntity<String> eliminarItemCarrito(
            Principal principal,
            @RequestParam("libroId") Integer libroId) {        
        try {
            Cliente cliente = obtenerClienteSesion(principal);
            if (cliente == null) return ResponseEntity.status(401).body("No autorizado");

            carritoServicio.removerLibroDeCarrito(cliente.getId(), libroId);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace(); // Revisa la consola al ver este error
            return ResponseEntity.status(500).body("Error en el servidor: " + e.getMessage());
        }
    }
    
    @GetMapping("/carrito/cantidad")
    @ResponseBody
    public ResponseEntity<Integer> obtenerCantidadCarrito(Principal principal) {

        Cliente cliente = obtenerClienteSesion(principal);
        if (cliente == null) {
            return ResponseEntity.status(401).body(0);
        }

        Carrito carrito = carritoServicio.obtenerPorCliente(cliente.getId())
                .orElse(null);

        if (carrito == null || carrito.getDetalles() == null) {
            return ResponseEntity.ok(0);
        }

        int total = carrito.getDetalles()
                .stream()
                .mapToInt(CarritoDetalle::getCantidad)
                .sum();

        return ResponseEntity.ok(total);
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
                                        @RequestParam("email") String nuevoEmail,
                                        @RequestParam(value = "passwordActual", required = false) String passwordActual,
                                        @RequestParam(value = "passwordNueva", required = false) String passwordNueva) {
        
        Cliente cliente = obtenerClienteSesion(principal);
        if (cliente == null) return "redirect:/tienda/vista-login";

        Usuario usuario = cliente.getUsuario();

        // 1. Validar si el correo cambió
        if (!usuario.getEmail().equals(nuevoEmail)) {
            // Verificar si otro usuario ya usa ese correo
            if (usuarioServicio.buscarPorEmail(nuevoEmail).isPresent()) {
                return "redirect:/tienda/perfil?errorEmail=true"; 
            }
            usuario.setEmail(nuevoEmail);
            
            // Actualizamos en BD
            usuarioServicio.actualizarEstado(usuario.getId(), true);
            clienteRepositorio.save(cliente);
            
            // Forzamos salida porque el nombre de usuario (email) cambió
            return "redirect:/logout?emailCambiado=true"; 
        }

        // 2. Si el correo no cambió, solo actualizamos datos y contraseña
        usuario.setNombreCompleto(nombre);
        cliente.setTelefono(telefono);
        
        if (passwordActual != null && !passwordActual.isEmpty() && passwordNueva != null && !passwordNueva.isEmpty()) {
            if (passwordEncoder.matches(passwordActual, usuario.getPasswordHash())) {
                usuario.setPasswordHash(passwordEncoder.encode(passwordNueva));
            } else {
                return "redirect:/tienda/perfil?errorPassword=true";
            }
        }
        
        usuarioServicio.actualizarEstado(usuario.getId(), true);
        clienteRepositorio.save(cliente);
        
        return "redirect:/tienda/perfil?cambioExitoso=true";
    }
    
    @GetMapping("/pedido/{id}")
    public String verDetallePedido(@PathVariable("id") Integer id, Principal principal, Model model) {
        Cliente cliente = obtenerClienteSesion(principal);
        if (cliente == null) return "redirect:/tienda/vista-login";

        Pedido pedido = pedidoServicio.findById(id).orElse(null);
        
        // Si el pedido no existe o no le pertenece a este cliente, lo regresamos a su perfil
        if (pedido == null || !pedido.getCliente().getId().equals(cliente.getId())) {
            return "redirect:/tienda/perfil";
        }

        model.addAttribute("pedido", pedido);
        return "cliente/detalle-pedido";
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