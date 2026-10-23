package com.tiendamotos.tiendamotosgc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tiendamotos.tiendamotosgc.dto.ProductoFilter;
import com.tiendamotos.tiendamotosgc.model.ProductoGC;
import com.tiendamotos.tiendamotosgc.service.CategoriaServiceGC;
import com.tiendamotos.tiendamotosgc.service.ProductoServiceGC;
import com.tiendamotos.tiendamotosgc.service.ProveedorServiceGC;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoControllerGC {

    private final ProductoServiceGC productoService;
    private final CategoriaServiceGC categoriaService;
    private final ProveedorServiceGC proveedorService;

    @GetMapping({"", "/", "/listar"})
    public String listar(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/";
        }
        model.addAttribute("productos", productoService.listarTodos());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/";
        }
        model.addAttribute("producto", new ProductoGC());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        return "productos/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/";
        }
        ProductoGC producto = productoService.obtenerPorId(id);
        if (producto == null) {
            return "redirect:/productos/listar";
        }
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        return "productos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("producto") ProductoGC producto) {
        productoService.guardar(producto);
        return "redirect:/productos/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id) {
        productoService.eliminar(id);
        return "redirect:/productos/listar";
    }

    @GetMapping("/consulta")
    public String consultaFiltrada(@ModelAttribute ProductoFilter filter, Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/";
        }
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("productos", productoService.buscarConFiltro(filter));
        model.addAttribute("productoFilter", filter);
        return "productos/consulta";
    }
}