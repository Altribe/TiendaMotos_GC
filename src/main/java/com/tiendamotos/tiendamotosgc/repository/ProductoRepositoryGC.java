package com.tiendamotos.tiendamotosgc.repository;

import com.tiendamotos.tiendamotosgc.model.ProductoGC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepositoryGC extends JpaRepository<ProductoGC, Integer> {

    @Query("""
            select p from ProductoGC p
            where 
                (:nombre is null or :nombre = '' or lower(p.nombre) like lower(concat('%', :nombre, '%')))
                and
                (p.categoria.idCategoria = :idCategoria or :idCategoria is null or :idCategoria = 0)
            order by p.idProducto desc
            """)
    List<ProductoGC> findByFilters(
            @Param("nombre") String nombre,
            @Param("idCategoria") Integer idCategoria
    );
}