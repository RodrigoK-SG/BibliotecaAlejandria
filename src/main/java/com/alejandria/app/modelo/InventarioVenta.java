package com.alejandria.app.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "INVENTARIO_VENTA")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "libroId")
public class InventarioVenta {
    @Id
    @Column(name = "LIBRO_ID")
    private Integer libroId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "LIBRO_ID")
    @ToString.Exclude
    private Libro libro;

    @NotNull @Min(0)
    @Column(name = "CANTIDAD_DISPONIBLE", nullable = false)
    private Integer cantidadDisponible = 0;

    @NotNull @Min(0)
    @Column(name = "CANTIDAD_RESERVADA", nullable = false)
    private Integer cantidadReservada = 0;

    @Column(name = "STOCK_MINIMO")
    private Integer stockMinimo = 5;

    @Column(name = "STOCK_MAXIMO")
    private Integer stockMaximo = 1000;
}