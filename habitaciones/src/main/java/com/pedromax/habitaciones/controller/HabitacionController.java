package com.pedromax.habitaciones.controller;

import com.pedromax.commons.controller.CrudController;
import com.pedromax.commons.docs.ProblemaDoc;
import com.pedromax.commons.dto.habitaciones.HabitacionRequest;
import com.pedromax.commons.dto.habitaciones.HabitacionResponse;
import com.pedromax.habitaciones.services.HabitacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API Habitaciones", description = "Métodos para la gestion de habitaciones")
public class HabitacionController extends CrudController<HabitacionRequest, HabitacionResponse, HabitacionService> {

    public HabitacionController(HabitacionService service) {
        super(service);
    }

    @GetMapping("/id-habitacion/{id}")
    @Operation(summary = "Obtener habitación por id sin importar el estado del registro")
    @ApiResponse(responseCode = "200", description = "Habitación sin estado encontrada")
    @ApiResponse(responseCode = "404", description = "La habitación sin estado no existe",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<HabitacionResponse> obtenerHabitacionPorIdSinEstado(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ) {
        return ResponseEntity.ok(service.obtenerHabitacionPorIdSinEstado(id));
    }

    @PutMapping("/{id}/estado/{idEstado}")
    @Operation(summary = "Actualizar estado de la habitación")
    @ApiResponse(responseCode = "204", description = "Estado de la habitación actualizada")
    @ApiResponse(responseCode = "409", description = "El estado de la habitación no puede actualizarse por regla de negocio",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<Void> actualizarEstadoHabitacion(
            @PathVariable @Positive(message = "El id de la habitación debe ser positivo") Long id,
            @PathVariable @Positive(message = "El id del estado de la habitación debe ser positivo") Long idEstado
    ) {
        service.actualizarEstadoHabitacion(id, idEstado);
        return ResponseEntity.noContent().build();
    }
}
