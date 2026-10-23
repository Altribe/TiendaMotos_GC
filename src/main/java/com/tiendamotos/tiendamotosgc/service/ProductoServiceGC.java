package com.tiendamotos.tiendamotosgc.service;

import com.tiendamotos.tiendamotosgc.dto.ProductoFilter;
import com.tiendamotos.tiendamotosgc.model.ProductoGC;
import com.tiendamotos.tiendamotosgc.repository.ProductoRepositoryGC;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceGC {

    private final ProductoRepositoryGC productoRepository;

    public List<ProductoGC> listarTodos() {
        return productoRepository.findAll();
    }

    public ProductoGC obtenerPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    public List<ProductoGC> buscarConFiltro(ProductoFilter filter) {
        return productoRepository.findByFilters(filter.nombre(), filter.idCategoria());
    }

    @Transactional
    public String guardar(ProductoGC producto) {
        var registrado = productoRepository.save(producto);
        return String.format("Producto con Id %d registrado correctamente", registrado.getIdProducto());
    }

    @Transactional
    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }
}