package com.tiendamotos.tiendamotosgc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tiendamotos.tiendamotosgc.model.ProveedorGC;
import com.tiendamotos.tiendamotosgc.service.ProveedorServiceGC;
import com.tiendamotos.tiendamotosgc.util.Alert;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorControllerGC {

    private final ProveedorServiceGC proveedorService;

    @PostMapping("/guardar-modal")
    public String guardarModal(@ModelAttribute ProveedorGC proveedor, RedirectAttributes flash) {
        try {
            proveedorService.guardar(proveedor);
            flash.addFlashAttribute("alerta", Alert.sweetToast("Proveedor registrado con éxito", "success", 4000));
        } catch (Exception e) {
            flash.addFlashAttribute("alerta", Alert.sweetAlertError("No se pudo registrar el proveedor (RUC o Razón Social duplicados)"));
        }
        return "redirect:/productos/nuevo";
    }
}