package com.tiendamotos.tiendamotosgc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiendamotos.tiendamotosgc.model.ClienteGC;
import com.tiendamotos.tiendamotosgc.repository.ClienteRepositoryGC;

@Service
public class ClienteServiceGC {

    private final ClienteRepositoryGC clienteRepository;

    public ClienteServiceGC(ClienteRepositoryGC clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Listar todos los clientes
    public List<ClienteGC> listarClientes() {
        return clienteRepository.findAll();
    }

    // Buscar un cliente por su ID
    public Optional<ClienteGC> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    // Buscar cliente por DNI
    public Optional<ClienteGC> buscarPorDni(String dni) {
        return clienteRepository.findByDni(dni);
    }

    // Guardar o actualizar un cliente
    @Transactional
    public ClienteGC guardarCliente(ClienteGC cliente) {

        if (cliente.getDni() == null ||
                !cliente.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException(
                "El DNI debe contener exactamente 8 numeros.");
        }

        Optional<ClienteGC> existente =
                clienteRepository.findByDni(cliente.getDni());

        if (existente.isPresent() &&
            !existente.get().getIdCliente()
                .equals(cliente.getIdCliente())) {
            throw new IllegalArgumentException(
                "Ya existe un cliente registrado con ese DNI.");
        }

        return clienteRepository.save(cliente);
    }

    // Eliminar un cliente
    @Transactional
    public void eliminarCliente(Integer id) {

        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException(
                "El cliente no existe.");
        }

        clienteRepository.deleteById(id);
    }
}
