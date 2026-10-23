package com.tiendamotos.tiendamotosgc.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tiendamotos.tiendamotosgc.repository.ProductoRepositoryGC;
import com.tiendamotos.tiendamotosgc.repository.ProveedorRepositoryGC;
import com.tiendamotos.tiendamotosgc.service.OrdenCompraServiceGC;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrdenCompraControllerGC {

    private final OrdenCompraServiceGC ordenService;
    private final ProveedorRepositoryGC proveedorRepository;
    private final ProductoRepositoryGC productoRepository;

    private boolean sinSesion(HttpSession session) {
        return session.getAttribute("usuarioLogueado") == null;
    }

    @GetMapping("/listar")
    public String listar(Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        model.addAttribute("ordenes", ordenService.listarOrdenes());
        return "ordenes/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        model.addAttribute("proveedores", proveedorRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        return "ordenes/form";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idProveedor,
                          @RequestParam Integer idProducto,
                          @RequestParam Integer cantidad,
                          @RequestParam BigDecimal precioCompra,
                          HttpSession session,
                          RedirectAttributes flash) {
        if (sinSesion(session)) return "redirect:/";
        try {
            ordenService.crearOrden(idProveedor, idProducto, cantidad, precioCompra);
            flash.addFlashAttribute("mensaje", "Orden registrada como pendiente.");
            return "redirect:/ordenes/listar";
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/ordenes/nueva";
        }
    }

    @PostMapping("/completar")
    public String completar(@RequestParam Integer idOrden, HttpSession session,
                            RedirectAttributes flash) {
        if (sinSesion(session)) return "redirect:/";
        try {
            ordenService.completarOrden(idOrden);
            flash.addFlashAttribute("mensaje", "Orden completada; stock actualizado.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ordenes/listar";
    }

    @PostMapping("/anular")
    public String anular(@RequestParam Integer idOrden, HttpSession session,
                         RedirectAttributes flash) {
        if (sinSesion(session)) return "redirect:/";
        try {
            ordenService.anularOrden(idOrden);
            flash.addFlashAttribute("mensaje", "Orden anulada; stock sin cambios.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ordenes/listar";
    }
}
