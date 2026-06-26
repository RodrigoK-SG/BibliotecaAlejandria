package com.alejandria.app.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "DETALLE_PEDIDO")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class DetallePedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PEDIDO_ID", nullable = false)
    @ToString.Exclude
    private Pedido pedido;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIBRO_ID", nullable = false)
    @ToString.Exclude
    private Libro libro;

    @NotNull @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull @Min(0)
    @Column(name = "PRECIO_UNITARIO", nullable = false)
    private BigDecimal precioUnitario;

    @NotNull @Min(0)
    @Column(nullable = false)
    private BigDecimal subtotal;
}