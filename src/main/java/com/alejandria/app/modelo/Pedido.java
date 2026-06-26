package com.alejandria.app.modelo;

import com.alejandria.app.modelo.enums.CanalVenta;
import com.alejandria.app.modelo.enums.EstadoPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PEDIDO")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENTE_ID", nullable = false)
    @ToString.Exclude
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIRECCION_ENVIO_ID")
    @ToString.Exclude
    private DireccionEnvio direccionEnvio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "CANAL_VENTA", nullable = false)
    private CanalVenta canalVenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_ACTUAL")
    private EstadoPedido estadoActual = EstadoPedido.PENDIENTE_PAGO;

    @NotNull @Min(0)
    @Column(nullable = false)
    private BigDecimal total;

    @CreationTimestamp
    @Column(name = "FECHA_PEDIDO", updatable = false)
    private LocalDateTime fechaPedido;

    @CreationTimestamp
    @Column(name = "CREADO_EN", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "ACTUALIZADO_EN")
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<DetallePedido> detalles = new ArrayList<>();
}