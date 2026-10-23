package com.tiendamotos.tiendamotosgc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoGC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    private String nombre;
    
    private Double precio;
    
    private Integer stock;

    // Relación con Categoría
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaGC categoria;

    // Relación con Proveedor
    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private ProveedorGC proveedor;
}