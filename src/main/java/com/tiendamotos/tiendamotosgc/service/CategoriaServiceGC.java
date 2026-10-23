package com.tiendamotos.tiendamotosgc.service;

import com.tiendamotos.tiendamotosgc.model.CategoriaGC;
import com.tiendamotos.tiendamotosgc.repository.CategoriaRepositoryGC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaServiceGC {

    @Autowired
    private CategoriaRepositoryGC categoriaRepository;

    public List<CategoriaGC> listarTodas() {
        return categoriaRepository.findAll();
    }

    public CategoriaGC obtenerPorId(Integer id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    public CategoriaGC guardar(CategoriaGC categoria) {
        return categoriaRepository.save(categoria);
    }

    public void eliminar(Integer id) {
        categoriaRepository.deleteById(id);
    }
}