package com.tiendamotos.tiendamotosgc.repository;

import com.tiendamotos.tiendamotosgc.model.CategoriaGC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepositoryGC extends JpaRepository<CategoriaGC, Integer> {
}