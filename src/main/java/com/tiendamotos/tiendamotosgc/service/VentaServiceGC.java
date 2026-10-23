package com.tiendamotos.tiendamotosgc.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiendamotos.tiendamotosgc.dto.ProductoSeleccionadoGC;
import com.tiendamotos.tiendamotosgc.model.DetalleVentaGC;
import com.tiendamotos.tiendamotosgc.model.ProductoGC;
import com.tiendamotos.tiendamotosgc.model.UsuarioGC;
import com.tiendamotos.tiendamotosgc.model.VentaGC;
import com.tiendamotos.tiendamotosgc.repository.DetalleVentaRepositoryGC;
import com.tiendamotos.tiendamotosgc.repository.ProductoRepositoryGC;
import com.tiendamotos.tiendamotosgc.repository.VentaRepositoryGC;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaServiceGC {
    private final VentaRepositoryGC ventaRepository;
    private final DetalleVentaRepositoryGC detalleVentaRepository;
    private final ProductoRepositoryGC productoRepository;
    private final EntityManager entityManager;

    @Transactional
    public VentaGC registrarVenta(VentaGC venta, List<ProductoSeleccionadoGC> seleccionados, UsuarioGC usuario) {
        if (usuario == null) throw new IllegalArgumentException("Debe iniciar sesión.");
        if (seleccionados == null || seleccionados.isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto.");
        }
        if (!"Boleta".equals(venta.getTipoDocumento()) && !"Factura".equals(venta.getTipoDocumento())) {
            throw new IllegalArgumentException("Seleccione un tipo de documento válido.");
        }
        String documento = venta.getNumDocumento() == null ? "" : venta.getNumDocumento().trim();
        if (!(documento.matches("\\d{8}") || documento.matches("\\d{11}"))) {
            throw new IllegalArgumentException("El documento debe tener 8 dígitos (DNI) o 11 (RUC).");
        }
        if (venta.getNombreCliente() == null || venta.getNombreCliente().isBlank()
                || venta.getDireccionCliente() == null || venta.getDireccionCliente().isBlank()) {
            throw new IllegalArgumentException("Complete los datos del cliente.");
        }
        venta.setNumDocumento(documento);

        // Ordenar los IDs para tomar bloqueos en orden estable.
        List<ProductoSeleccionadoGC> ordenados = new ArrayList<>(seleccionados);
        ordenados.sort((a, b) -> Integer.compare(
            a.getIdProducto() == null ? -1 : a.getIdProducto(),
            b.getIdProducto() == null ? -1 : b.getIdProducto()));
        Set<Integer> ids = new HashSet<>();
        List<ProductoGC> productos = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // Bloqueo de filas: impide que dos ventas simultáneas descuenten el mismo stock.
        for (ProductoSeleccionadoGC item : ordenados) {
            if (item.getIdProducto() == null || item.getCantidad() == null || item.getCantidad() < 1) {
                throw new IllegalArgumentException("Hay productos o cantidades inválidas.");
            }
            if (!ids.add(item.getIdProducto())) {
                throw new IllegalArgumentException("Hay un producto repetido en el detalle.");
            }
            ProductoGC producto = entityManager.find(
                ProductoGC.class, item.getIdProducto(), LockModeType.PESSIMISTIC_WRITE);
            if (producto == null) {
                throw new IllegalArgumentException("El producto " + item.getIdProducto() + " ya no existe.");
            }
            if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre()
                    + ". Disponible: " + producto.getStock() + ", solicitado: " + item.getCantidad() + ".");
            }
            if (producto.getPrecio() == null || !Double.isFinite(producto.getPrecio()) || producto.getPrecio() < 0) {
                throw new IllegalArgumentException("Precio inválido para " + producto.getNombre());
            }
            total = total.add(BigDecimal.valueOf(producto.getPrecio())
                .multiply(BigDecimal.valueOf(item.getCantidad())));
            productos.add(producto);
        }

        venta.setFechaVenta(LocalDateTime.now());
        venta.setUsuario(usuario);
        venta.setTotal(total);
        VentaGC guardada = ventaRepository.save(venta);

        for (int i = 0; i < ordenados.size(); i++) {
            ProductoSeleccionadoGC item = ordenados.get(i);
            ProductoGC producto = productos.get(i);
            BigDecimal precio = BigDecimal.valueOf(producto.getPrecio());
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            DetalleVentaGC detalle = new DetalleVentaGC();
            detalle.setVenta(guardada);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(precio.multiply(BigDecimal.valueOf(item.getCantidad())));
            detalleVentaRepository.save(detalle);
        }
        return guardada;
    }

    public List<VentaGC> listarVentas() {
        return ventaRepository.findAll();
    }
}
