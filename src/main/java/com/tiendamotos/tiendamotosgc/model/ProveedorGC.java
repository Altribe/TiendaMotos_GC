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
@Table(name = "tbl_proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorGC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProveedor;

    private String ruc;

    private String razonSocial;
    private String direccion;

    private String telefono;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaGC categoria;
}