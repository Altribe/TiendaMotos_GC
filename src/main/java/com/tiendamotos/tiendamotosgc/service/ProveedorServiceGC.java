package com.tiendamotos.tiendamotosgc.service;

import com.tiendamotos.tiendamotosgc.model.ProveedorGC;
import com.tiendamotos.tiendamotosgc.repository.ProveedorRepositoryGC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProveedorServiceGC {

    @Autowired
    private ProveedorRepositoryGC proveedorRepository;

    public List<ProveedorGC> listarTodos() {
        return proveedorRepository.findAll();
    }

    public ProveedorGC obtenerPorId(Integer id) {
        return proveedorRepository.findById(id).orElse(null);
    }

    public ProveedorGC guardar(ProveedorGC proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public void eliminar(Integer id) {
        proveedorRepository.deleteById(id);
    }
}