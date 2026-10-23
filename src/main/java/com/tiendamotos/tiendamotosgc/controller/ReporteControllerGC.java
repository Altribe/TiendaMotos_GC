package com.tiendamotos.tiendamotosgc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tiendamotos.tiendamotosgc.service.ReporteServiceGC;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteControllerGC {

    private final ReporteServiceGC reporteService;

    @GetMapping
    public String mostrarReportes(
            Model model,
            HttpSession session) {

        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/";
        }

        model.addAllAttributes(
                reporteService.obtenerResumen());

        return "reportes/index";
    }
}
