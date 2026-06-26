package com.alejandria.app.servicio;

import com.alejandria.app.modelo.Carrito;
import com.alejandria.app.modelo.CarritoDetalle;
import com.alejandria.app.modelo.CarritoDetalleId;
import com.alejandria.app.modelo.Libro;
import com.alejandria.app.repositorio.CarritoRepositorio;
import com.alejandria.app.repositorio.LibroRepositorio;
import com.alejandria.app.repositorio.CarritoDetalleRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarritoServicio {

    private final CarritoRepositorio carritoRepositorio;
    private final CarritoDetalleRepositorio carritoDetalleRepositorio;
    private final LibroRepositorio libroRepositorio;

    public Optional<Carrito> obtenerPorCliente(Integer clienteId) {
        return carritoRepositorio.findByClienteId(clienteId);
    }

    @Transactional
    public void agregarLibroACarrito(Integer clienteId, Integer libroId, int cantidad) {
        Carrito carrito = carritoRepositorio.findByClienteId(clienteId)
                .orElseThrow(() -> new RuntimeException("No se encontró un carrito asociado al cliente."));
        
        Libro libro = libroRepositorio.findById(libroId)
                .orElseThrow(() -> new RuntimeException("El producto que intenta agregar no existe."));

        CarritoDetalleId detalleId = new CarritoDetalleId(carrito.getId(), libroId);
        Optional<CarritoDetalle> detalleOpt = carritoDetalleRepositorio.findById(detalleId);

        if (detalleOpt.isPresent()) {
            CarritoDetalle detalle = detalleOpt.get();
            detalle.setCantidad(detalle.getCantidad() + cantidad);
            carritoDetalleRepositorio.save(detalle);
        } else {
            CarritoDetalle nuevoDetalle = new CarritoDetalle();
            nuevoDetalle.setId(detalleId);
            nuevoDetalle.setCarrito(carrito);
            nuevoDetalle.setLibro(libro);
            nuevoDetalle.setCantidad(cantidad);
            carritoDetalleRepositorio.save(nuevoDetalle);
        }
    }

    @Transactional
    public void removerLibroDeCarrito(Integer clienteId, Integer libroId) {
        Carrito carrito = carritoRepositorio.findByClienteId(clienteId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado."));
        
        CarritoDetalleId detalleId = new CarritoDetalleId(carrito.getId(), libroId);
        carritoDetalleRepositorio.deleteById(detalleId);
    }

    @Transactional
    public void limpiarCarrito(Carrito carrito) {
        carrito.getDetalles().clear();
        carritoRepositorio.save(carrito);
    }
}