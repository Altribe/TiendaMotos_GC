package com.tiendamotos.tiendamotosgc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tiendamotos.tiendamotosgc.model.ClienteGC;
import com.tiendamotos.tiendamotosgc.service.ClienteServiceGC;

@Controller
@RequestMapping("/clientes")
public class ClienteControllerGC {

    private final ClienteServiceGC clienteService;

    public ClienteControllerGC(ClienteServiceGC clienteService) {
        this.clienteService = clienteService;
    }

    // Mostrar todos los clientes
    @GetMapping("/listar")
    public String listarClientes(Model model) {

        model.addAttribute("clientes",
                clienteService.listarClientes());

        return "clientes/lista";
    }

    // Mostrar formulario para registrar cliente
    @GetMapping("/nuevo")
    public String nuevoCliente(Model model) {

        model.addAttribute("cliente", new ClienteGC());

        return "clientes/form";
    }

    // Guardar un cliente nuevo o actualizado
    @PostMapping("/guardar")
    public String guardarCliente(
            @ModelAttribute("cliente") ClienteGC cliente,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            clienteService.guardarCliente(cliente);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Cliente guardado correctamente.");

            return "redirect:/clientes/listar";

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());

            return "clientes/form";
        }
    }

    // Mostrar formulario para editar cliente
    @GetMapping("/editar/{id}")
    public String editarCliente(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        var cliente = clienteService.buscarPorId(id);

        if (cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Cliente no encontrado.");

            return "redirect:/clientes/listar";
        }

        model.addAttribute("cliente", cliente.get());

        return "clientes/form";
    }

    // Eliminar un cliente
    @PostMapping("/eliminar/{id}")
    public String eliminarCliente(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            clienteService.eliminarCliente(id);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Cliente eliminado correctamente.");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/clientes/listar";
    }
}
