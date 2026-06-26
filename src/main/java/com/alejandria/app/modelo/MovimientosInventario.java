package com.alejandria.app.modelo;

import com.alejandria.app.modelo.enums.TipoMovimiento;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "MOVIMIENTOS_INVENTARIO")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class MovimientosInventario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID")
    @ToString.Exclude
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIBRO_ID", nullable = false)
    @ToString.Exclude
    private Libro libro;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_MOVIMIENTO", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @NotNull @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    @Column(length = 255)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "FECHA_MOVIMIENTO", updatable = false)
    private LocalDateTime fechaMovimiento;
}