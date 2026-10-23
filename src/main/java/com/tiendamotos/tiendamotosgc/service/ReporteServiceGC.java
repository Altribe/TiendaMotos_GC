package com.tiendamotos.tiendamotosgc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiendamotos.tiendamotosgc.model.DetalleVentaGC;
import com.tiendamotos.tiendamotosgc.model.OrdenCompraGC;
import com.tiendamotos.tiendamotosgc.model.ProductoGC;
import com.tiendamotos.tiendamotosgc.model.VentaGC;
import com.tiendamotos.tiendamotosgc.repository.DetalleVentaRepositoryGC;
import com.tiendamotos.tiendamotosgc.repository.OrdenCompraRepositoryGC;
import com.tiendamotos.tiendamotosgc.repository.ProductoRepositoryGC;
import com.tiendamotos.tiendamotosgc.repository.VentaRepositoryGC;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceGC {

    private final VentaRepositoryGC ventaRepository;
    private final DetalleVentaRepositoryGC detalleRepository;
    private final ProductoRepositoryGC productoRepository;
    private final OrdenCompraRepositoryGC ordenRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerResumen() {

        List<VentaGC> ventas = ventaRepository.findAll();
        List<DetalleVentaGC> detalles = detalleRepository.findAll();
        List<ProductoGC> productos = productoRepository.findAll();
        List<OrdenCompraGC> ordenes = ordenRepository.findAll();

        Map<String, Object> resumen = new HashMap<>();

        // Total facturado
        BigDecimal ingresos = ventas.stream()
                .map(VentaGC::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Número de ventas
        int cantidadVentas = ventas.size();

        // Unidades vendidas
        int unidadesVendidas = detalles.stream()
                .mapToInt(d -> d.getCantidad() == null
                        ? 0 : d.getCantidad())
                .sum();

        // Existencias actuales
        int stockTotal = productos.stream()
                .mapToInt(p -> p.getStock() == null
                        ? 0 : p.getStock())
                .sum();

        // Productos con 3 o menos unidades
        List<ProductoGC> productosStockBajo = productos.stream()
                .filter(p -> p.getStock() != null
                        && p.getStock() <= 3)
                .sorted(Comparator.comparing(
                        ProductoGC::getStock))
                .toList();

        // Órdenes por estado
        long ordenesPendientes = ordenes.stream()
                .filter(o -> "P".equals(o.getEstado()))
                .count();

        long ordenesCompletadas = ordenes.stream()
                .filter(o -> "C".equals(o.getEstado()))
                .count();

        long ordenesAnuladas = ordenes.stream()
                .filter(o -> "A".equals(o.getEstado()))
                .count();

        // Agrupar productos vendidos
        Map<Integer, Map<String, Object>> agrupados =
                new HashMap<>();

        for (DetalleVentaGC detalle : detalles) {

            if (detalle.getProducto() == null) {
                continue;
            }

            ProductoGC producto = detalle.getProducto();
            Integer id = producto.getIdProducto();

            Map<String, Object> dato = agrupados.computeIfAbsent(
                    id, clave -> {
                        Map<String, Object> nuevo = new HashMap<>();
                        nuevo.put("nombre", producto.getNombre());
                        nuevo.put("cantidad", 0);
                        nuevo.put("importe", BigDecimal.ZERO);
                        return nuevo;
                    });

            int cantidadActual = (Integer) dato.get("cantidad");

            dato.put("cantidad",
                    cantidadActual + detalle.getCantidad());

            BigDecimal importeActual =
                    (BigDecimal) dato.get("importe");

            dato.put("importe", importeActual.add(
                    detalle.getSubtotal()));
        }

        List<Map<String, Object>> masVendidos =
                new ArrayList<>(agrupados.values());

        masVendidos.sort((a, b) -> Integer.compare(
                (Integer) b.get("cantidad"),
                (Integer) a.get("cantidad")));

        resumen.put("ingresos", ingresos);
        resumen.put("cantidadVentas", cantidadVentas);
        resumen.put("unidadesVendidas", unidadesVendidas);
        resumen.put("stockTotal", stockTotal);
        resumen.put("totalProductos", productos.size());
        resumen.put("stockBajo", productosStockBajo);
        resumen.put("cantidadStockBajo", productosStockBajo.size());
        resumen.put("ordenesPendientes", ordenesPendientes);
        resumen.put("ordenesCompletadas", ordenesCompletadas);
        resumen.put("ordenesAnuladas", ordenesAnuladas);
        resumen.put("masVendidos", masVendidos);

        return resumen;
    }
}
