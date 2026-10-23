package com.tiendamotos.tiendamotosgc.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_orden_compra")
public class OrdenCompraGC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Integer idOrden;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private ProveedorGC proveedor;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private ProductoGC producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "fecha_orden", nullable = false)
    private LocalDate fechaOrden;

    @Column(name = "estado", length = 1)
    private String estado; // 'P': Pendiente, 'C': Completado, 'A': Anulado

    public OrdenCompraGC() {
    }

    public Integer getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
    }

    public ProveedorGC getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorGC proveedor) {
        this.proveedor = proveedor;
    }

    public ProductoGC getProducto() {
        return producto;
    }

    public void setProducto(ProductoGC producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public LocalDate getFechaOrden() {
        return fechaOrden;
    }

    public void setFechaOrden(LocalDate fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}