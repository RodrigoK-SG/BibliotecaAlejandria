package com.alejandria.app.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "AUTOR")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Autor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombre;
}