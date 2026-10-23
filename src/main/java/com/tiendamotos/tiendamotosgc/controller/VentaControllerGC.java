package com.tiendamotos.tiendamotosgc.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tiendamotos.tiendamotosgc.dto.ProductoSeleccionadoGC;
import com.tiendamotos.tiendamotosgc.model.ProductoGC;
import com.tiendamotos.tiendamotosgc.model.UsuarioGC;
import com.tiendamotos.tiendamotosgc.model.VentaGC;
import com.tiendamotos.tiendamotosgc.repository.ProductoRepositoryGC;
import com.tiendamotos.tiendamotosgc.service.ProductoServiceGC;
import com.tiendamotos.tiendamotosgc.service.VentaServiceGC;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/ventas")
@RequiredArgsConstructor
@SessionAttributes("seleccionados")
public class VentaControllerGC {

    private final ProductoServiceGC productoService;
    private final VentaServiceGC ventaService;
    private final ProductoRepositoryGC productoRepository;

    @ModelAttribute("seleccionados")
    public List<ProductoSeleccionadoGC> iniciarSeleccionados() {
        return new ArrayList<>();
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/";
        model.addAttribute("venta", new VentaGC());
        model.addAttribute("productoSeleccionado", new ProductoSeleccionadoGC());
        model.addAttribute("productosDisponibles", productoService.listarTodos());
        return "ventas/nuevo";
    }

    @PostMapping("/agregar")
    public String agregar(@ModelAttribute ProductoSeleccionadoGC seleccionado,
                          @ModelAttribute("seleccionados") List<ProductoSeleccionadoGC> seleccionados,
                          HttpSession session, RedirectAttributes flash) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/";
        Integer id = seleccionado.getIdProducto();
        Integer cantidad = seleccionado.getCantidad();
        if (id == null || cantidad == null || cantidad < 1) {
            flash.addFlashAttribute("error", "Seleccione un producto y una cantidad mayor que cero.");
            return "redirect:/ventas/nuevo";
        }
        ProductoGC producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            flash.addFlashAttribute("error", "El producto seleccionado no existe.");
            return "redirect:/ventas/nuevo";
        }
        if (seleccionados.stream().anyMatch(p -> id.equals(p.getIdProducto()))) {
            flash.addFlashAttribute("error", "Este producto ya está agregado. Utilice Editar para cambiar su cantidad.");
            return "redirect:/ventas/nuevo";
        }
        if (producto.getStock() == null || producto.getStock() < cantidad) {
            flash.addFlashAttribute("error", "Stock insuficiente para " + producto.getNombre()
                + ". Disponible: " + producto.getStock() + ", solicitado: " + cantidad + ".");
            return "redirect:/ventas/nuevo";
        }
        if (producto.getPrecio() == null || !Double.isFinite(producto.getPrecio()) || producto.getPrecio() < 0) {
            flash.addFlashAttribute("error", "El producto no tiene un precio válido.");
            return "redirect:/ventas/nuevo";
        }
        seleccionado.setNombre(producto.getNombre());
        seleccionado.setPrecio(BigDecimal.valueOf(producto.getPrecio()));
        seleccionados.add(seleccionado);
        return "redirect:/ventas/nuevo";
    }

    @PostMapping("/editar-cantidad")
    public String editarCantidad(@RequestParam Integer idProducto,
                                 @RequestParam Integer nuevaCantidad,
                                 @ModelAttribute("seleccionados") List<ProductoSeleccionadoGC> seleccionados,
                                 HttpSession session, RedirectAttributes flash) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/";
        if (nuevaCantidad == null || nuevaCantidad < 1) {
            flash.addFlashAttribute("error", "La cantidad debe ser mayor que cero.");
            return "redirect:/ventas/nuevo";
        }
        ProductoGC producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            flash.addFlashAttribute("error", "El producto no existe.");
            return "redirect:/ventas/nuevo";
        }
        if (producto.getStock() == null || producto.getStock() < nuevaCantidad) {
            flash.addFlashAttribute("error", "Stock insuficiente para " + producto.getNombre()
                + ". Disponible: " + producto.getStock() + ", solicitado: " + nuevaCantidad + ".");
            return "redirect:/ventas/nuevo";
        }
        for (ProductoSeleccionadoGC item : seleccionados) {
            if (idProducto.equals(item.getIdProducto())) {
                item.setCantidad(nuevaCantidad);
                return "redirect:/ventas/nuevo";
            }
        }
        flash.addFlashAttribute("error", "El producto no está agregado a la venta.");
        return "redirect:/ventas/nuevo";
    }

    @PostMapping("/remover")
    public String remover(@RequestParam Integer id,
                          @ModelAttribute("seleccionados") List<ProductoSeleccionadoGC> seleccionados,
                          HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/";
        seleccionados.removeIf(p -> id.equals(p.getIdProducto()));
        return "redirect:/ventas/nuevo";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute VentaGC venta,
                            @ModelAttribute("seleccionados") List<ProductoSeleccionadoGC> seleccionados,
                            HttpSession session, SessionStatus sessionStatus,
                            RedirectAttributes flash) {
        UsuarioGC usuario = (UsuarioGC) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/";
        if (seleccionados.isEmpty()) {
            flash.addFlashAttribute("error", "Debe agregar al menos una motocicleta.");
            return "redirect:/ventas/nuevo";
        }
        try {
            VentaGC guardada = ventaService.registrarVenta(venta, seleccionados, usuario);
            sessionStatus.setComplete();
            flash.addFlashAttribute("ventaExito", guardada);
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventas/nuevo";
    }

    @GetMapping("/listar")
    public String listar(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/";
        model.addAttribute("ventas", ventaService.listarVentas());
        return "ventas/lista";
    }
}
