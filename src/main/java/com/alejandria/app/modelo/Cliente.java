package com.alejandria.app.modelo;

import com.alejandria.app.modelo.enums.TipoDocumento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLIENTE")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID", nullable = false, unique = true)
    @ToString.Exclude
    private Usuario usuario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_DOCUMENTO", nullable = false)
    private TipoDocumento tipoDocumento;

    @NotBlank
    @Column(name = "NUMERO_DOCUMENTO", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Column(length = 20)
    private String telefono;

    @CreationTimestamp
    @Column(name = "CREADO_EN", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "ACTUALIZADO_EN")
    private LocalDateTime actualizadoEn;
}