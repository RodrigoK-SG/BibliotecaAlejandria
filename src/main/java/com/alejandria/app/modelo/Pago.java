package com.alejandria.app.modelo;

import com.alejandria.app.modelo.enums.EstadoPago;
import com.alejandria.app.modelo.enums.MetodoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAGO")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pago {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PEDIDO_ID", nullable = false)
    @ToString.Exclude
    private Pedido pedido;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "METODO_PAGO", nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_PAGO")
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

    @NotNull @Min(0)
    @Column(nullable = false)
    private BigDecimal monto;

    @Column(name = "REFERENCIA_PAGO", length = 100)
    private String referenciaPago;

    @CreationTimestamp
    @Column(name = "FECHA_PAGO", updatable = false)
    private LocalDateTime fechaPago;
}