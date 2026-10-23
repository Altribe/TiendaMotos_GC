package com.tiendamotos.tiendamotosgc.repository;

import com.tiendamotos.tiendamotosgc.model.ProveedorGC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepositoryGC extends JpaRepository<ProveedorGC, Integer> {
}