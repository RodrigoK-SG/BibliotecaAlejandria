package com.alejandria.app.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class CarritoDetalleId implements Serializable {
    @Column(name = "CARRITO_ID")
    private Integer carritoId;

    @Column(name = "LIBRO_ID")
    private Integer libroId;
}