package com.tiendamotos.tiendamotosgc.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tiendamotos.tiendamotosgc.model.ClienteGC;

public interface ClienteRepositoryGC
        extends JpaRepository<ClienteGC, Integer> {

    Optional<ClienteGC> findByDni(String dni);

    boolean existsByDni(String dni);
}
