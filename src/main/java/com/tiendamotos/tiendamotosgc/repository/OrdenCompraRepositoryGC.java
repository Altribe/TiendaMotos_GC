package com.tiendamotos.tiendamotosgc.repository;

import com.tiendamotos.tiendamotosgc.model.OrdenCompraGC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenCompraRepositoryGC extends JpaRepository<OrdenCompraGC, Integer> {
}