package com.alejandria.app.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "DIRECCION_ENVIO")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class DireccionEnvio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENTE_ID", nullable = false)
    @ToString.Exclude
    private Cliente cliente;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String etiqueta;

    @NotBlank
    @Column(name = "DIRECCION_COMPLETA", nullable = false, length = 255)
    private String direccionCompleta;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(name = "CODIGO_POSTAL", length = 20)
    private String codigoPostal;
}