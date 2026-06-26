package com.alejandria.app.modelo;

import com.alejandria.app.modelo.enums.TipoComprobante;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMPROBANTE_FISCAL")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ComprobanteFiscal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PEDIDO_ID", nullable = false)
    @ToString.Exclude
    private Pedido pedido;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_COMPROBANTE", nullable = false)
    private TipoComprobante tipoComprobante;

    @NotBlank
    @Column(nullable = false, length = 10)
    private String serie;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String numero;

    @NotNull @Min(0)
    @Column(name = "MONTO_IMPUESTO", nullable = false)
    private BigDecimal montoImpuesto;

    @NotNull @Min(0)
    @Column(name = "MONTO_TOTAL", nullable = false)
    private BigDecimal montoTotal;

    @CreationTimestamp
    @Column(name = "FECHA_EMISION", updatable = false)
    private LocalDateTime fechaEmision;
}