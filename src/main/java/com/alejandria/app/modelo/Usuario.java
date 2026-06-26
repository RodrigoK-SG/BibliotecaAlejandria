package com.alejandria.app.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USUARIO")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(name = "NOMBRE_COMPLETO", nullable = false, length = 150)
    private String nombreCompleto;

    @Email @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank
    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    @ToString.Exclude
    private String passwordHash;

    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "CREADO_EN", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "ACTUALIZADO_EN")
    private LocalDateTime actualizadoEn;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "USUARIO_ROL",
        joinColumns = @JoinColumn(name = "USUARIO_ID"),
        inverseJoinColumns = @JoinColumn(name = "ROL_ID")
    )
    @ToString.Exclude
    private Set<Rol> roles = new HashSet<>();
}