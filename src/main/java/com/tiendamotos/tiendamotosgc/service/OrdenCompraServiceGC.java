package com.tiendamotos.tiendamotosgc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiendamotos.tiendamotosgc.model.OrdenCompraGC;
import com.tiendamotos.tiendamotosgc.model.ProductoGC;
import com.tiendamotos.tiendamotosgc.model.ProveedorGC;
import com.tiendamotos.tiendamotosgc.repository.OrdenCompraRepositoryGC;
import com.tiendamotos.tiendamotosgc.repository.ProductoRepositoryGC;
import com.tiendamotos.tiendamotosgc.repository.ProveedorRepositoryGC;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenCompraServiceGC {

    private final OrdenCompraRepositoryGC ordenRepository;
    private final ProveedorRepositoryGC proveedorRepository;
    private final ProductoRepositoryGC productoRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<OrdenCompraGC> listarOrdenes() {
        return ordenRepository.findAll();
    }

    @Transactional
    public OrdenCompraGC crearOrden(Integer idProveedor, Integer idProducto,
                                   Integer cantidad, BigDecimal precioCompra) {
        if (idProveedor == null || idProducto == null) {
            throw new IllegalArgumentException("Debe seleccionar un proveedor y un producto.");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (precioCompra == null || precioCompra.signum() <= 0) {
            throw new IllegalArgumentException("El precio de compra debe ser mayor que cero.");
        }
        if (precioCompra.scale() > 2) {
            throw new IllegalArgumentException("El precio de compra admite como máximo dos decimales.");
        }

        ProveedorGC proveedor = proveedorRepository.findById(idProveedor)
            .orElseThrow(() -> new IllegalArgumentException("El proveedor no existe."));
        ProductoGC producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new IllegalArgumentException("El producto no existe."));

        // Comprobar que el producto corresponda al proveedor seleccionado.
        if (producto.getProveedor() == null ||
            !idProveedor.equals(producto.getProveedor().getIdProveedor())) {
            throw new IllegalArgumentException("El producto no pertenece al proveedor seleccionado.");
        }

        BigDecimal total = precioCompra.multiply(BigDecimal.valueOf(cantidad))
            .setScale(2, RoundingMode.UNNECESSARY);
        if (total.precision() > 10) {
            throw new IllegalArgumentException("El monto total supera el límite permitido.");
        }

        OrdenCompraGC orden = new OrdenCompraGC();
        orden.setProveedor(proveedor);
        orden.setProducto(producto);
        orden.setCantidad(cantidad);
        orden.setMontoTotal(total);
        orden.setFechaOrden(LocalDate.now());
        orden.setEstado("P");
        return ordenRepository.save(orden);
    }

    @Transactional
    public void completarOrden(Integer idOrden) {
        if (idOrden == null) {
            throw new IllegalArgumentException("Orden inválida.");
        }
        // Bloquea la orden para que no se complete dos veces a la vez.
        OrdenCompraGC orden = entityManager.find(OrdenCompraGC.class, idOrden,
                LockModeType.PESSIMISTIC_WRITE);
        if (orden == null) {
            throw new IllegalArgumentException("La orden no existe.");
        }
        if (!"P".equals(orden.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden completar órdenes pendientes.");
        }
        if (orden.getCantidad() == null || orden.getCantidad() <= 0) {
            throw new IllegalArgumentException("La orden tiene una cantidad inválida.");
        }

        Integer idProducto = orden.getProducto().getIdProducto();
        ProductoGC producto = entityManager.find(ProductoGC.class, idProducto,
                LockModeType.PESSIMISTIC_WRITE);
        if (producto == null) {
            throw new IllegalArgumentException("El producto de la orden no existe.");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock actual del producto no es válido.");
        }
        long stockNuevo = (long) producto.getStock() + orden.getCantidad();
        if (stockNuevo > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("La cantidad excede el límite del inventario.");
        }

        producto.setStock((int) stockNuevo);
        orden.setEstado("C");
        // JPA persiste ambas actualizaciones en la misma transacción.
    }

    @Transactional
    public void anularOrden(Integer idOrden) {
        if (idOrden == null) {
            throw new IllegalArgumentException("Orden inválida.");
        }
        OrdenCompraGC orden = entityManager.find(OrdenCompraGC.class, idOrden,
                LockModeType.PESSIMISTIC_WRITE);
        if (orden == null) {
            throw new IllegalArgumentException("La orden no existe.");
        }
        if (!"P".equals(orden.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden anular órdenes pendientes.");
        }
        orden.setEstado("A");
    }
}
