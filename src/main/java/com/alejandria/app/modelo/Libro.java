package com.alejandria.app.modelo;

import com.alejandria.app.modelo.enums.FormatoLibro;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "LIBRO")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Libro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(unique = true, length = 255)
    private String slug;

    @Column(name = "IMAGEN_PORTADA", length = 255)
    private String imagenPortada;

    @Column(columnDefinition = "TEXT")
    private String sinopsis;

    @NotNull @Min(1)
    @Column(nullable = false)
    private Integer paginas;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormatoLibro formato = FormatoLibro.TAPA_BLANDA;

    @NotNull @Min(0)
    @Column(name = "PRECIO_VENTA_ACTUAL", nullable = false)
    private BigDecimal precioVentaActual;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EDITORIAL_ID", nullable = false)
    @ToString.Exclude
    private Editorial editorial;

    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "CREADO_EN", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "ACTUALIZADO_EN")
    private LocalDateTime actualizadoEn;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "LIBRO_AUTOR",
        joinColumns = @JoinColumn(name = "LIBRO_ID"),
        inverseJoinColumns = @JoinColumn(name = "AUTOR_ID")
    )
    @ToString.Exclude
    private Set<Autor> autores = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "LIBRO_CATEGORIA",
        joinColumns = @JoinColumn(name = "LIBRO_ID"),
        inverseJoinColumns = @JoinColumn(name = "CATEGORIA_ID")
    )
    @ToString.Exclude
    private Set<Categoria> categorias = new HashSet<>();
}