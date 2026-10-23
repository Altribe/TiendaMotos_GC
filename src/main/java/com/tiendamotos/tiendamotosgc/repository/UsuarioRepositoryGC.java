package com.tiendamotos.tiendamotosgc.repository;

import com.tiendamotos.tiendamotosgc.model.UsuarioGC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositoryGC extends JpaRepository<UsuarioGC, Integer> {
    UsuarioGC findByUsername(String username);
}