package com.pedromax.huespedes.controller;

import com.pedromax.commons.controller.CrudController;
import com.pedromax.commons.docs.ProblemaDoc;
import com.pedromax.commons.dto.habitaciones.huespedes.HuespedRequest;
import com.pedromax.commons.dto.habitaciones.huespedes.HuespedResponse;
import com.pedromax.huespedes.service.HuespedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API Huespedes", description = "Métodos para la gestion de huespedes")
public class HuespedController extends CrudController<HuespedRequest, HuespedResponse, HuespedService> {

    public HuespedController(HuespedService service) {
        super(service);
    }

    @GetMapping("/id-huesped/{id}")
    @Operation(summary = "Obtener huésped por id sin importar el estado del registro")
    @ApiResponse(responseCode = "200", description = "Huésped sin estado encontrado")
    @ApiResponse(responseCode = "404", description = "El huésped sin estado no existe",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<HuespedResponse> obtenerHuespedPorIdSinEstado(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ) {
        return ResponseEntity.ok(service.obtenerHuespedPorIdSinEstado(id));
    }
}
