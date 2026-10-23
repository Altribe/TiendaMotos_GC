package com.tiendamotos.tiendamotosgc.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoSeleccionadoGC {

    private Integer idProducto;
    private String nombre;
    private BigDecimal precio;
    private Integer cantidad;

    public BigDecimal getSubtotal() {
        if (precio == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }
}