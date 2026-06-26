package com.alejandria.app.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CARRITO")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Carrito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENTE_ID", nullable = false, unique = true)
    @ToString.Exclude
    private Cliente cliente;

    @CreationTimestamp
    @Column(name = "CREADO_EN", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "ACTUALIZADO_EN")
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<CarritoDetalle> detalles = new ArrayList<>();
}