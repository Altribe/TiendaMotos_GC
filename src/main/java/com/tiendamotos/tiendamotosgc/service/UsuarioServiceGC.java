package com.tiendamotos.tiendamotosgc.service;

import com.tiendamotos.tiendamotosgc.model.UsuarioGC;
import com.tiendamotos.tiendamotosgc.repository.UsuarioRepositoryGC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceGC {

    @Autowired
    private UsuarioRepositoryGC usuarioRepository;

    public UsuarioGC login(String username, String password) {
        UsuarioGC usuario = usuarioRepository.findByUsername(username);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        return null;
    }
}