package com.tiendamotos.tiendamotosgc.repository;

import com.tiendamotos.tiendamotosgc.model.DetalleVentaGC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleVentaRepositoryGC extends JpaRepository<DetalleVentaGC, Integer> {
}