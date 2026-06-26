package com.alejandria.app.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "CARRITO_DETALLE")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class CarritoDetalle {
    @EmbeddedId
    private CarritoDetalleId id = new CarritoDetalleId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("carritoId")
    @JoinColumn(name = "CARRITO_ID")
    @ToString.Exclude
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("libroId")
    @JoinColumn(name = "LIBRO_ID")
    @ToString.Exclude
    private Libro libro;

    @NotNull @Min(1)
    @Column(nullable = false)
    private Integer cantidad = 1;
}