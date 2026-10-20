package com.pedromax.reservas.controller;

import com.pedromax.commons.controller.CrudController;
import com.pedromax.commons.docs.ProblemaDoc;
import com.pedromax.commons.dto.reservas.ReservaRequest;
import com.pedromax.commons.dto.reservas.ReservaResponse;
import com.pedromax.reservas.services.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "API Reservas", description = "Métodos para la gestion de reservas")
public class ReservaController extends CrudController<ReservaRequest, ReservaResponse, ReservaService> {

    public ReservaController(ReservaService service) {
        super(service);
    }

    @PatchMapping("/{idReserva}/estado/{idEstado}")
    @Operation(summary = "Actualizar estado de la reserva",
            description = "Actualiza el estado de una reserva utilizando el id de la reserva y el id del nuevo estado")
    @ApiResponse(responseCode = "204", description = "Estado de la reserva actualizado")
    @ApiResponse(responseCode = "409", description = "El estado de la reserva no puede actualizarse por regla de negocio",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<Void> actualizarEstadoReserva(
            @PathVariable @Positive(message = "El idReserva debe ser positivo") Long idReserva,
            @PathVariable @Positive(message = "El idEstado debe ser positivo") Integer idEstado){
        service.actualizarEstadoReserva(idReserva, idEstado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/huesped-reserva/{idHuesped}")
    @Operation(summary = "Valida si un huésped tiene reservas activas",
            description = "Validar si un huésped tiene reservas activas")
    @ApiResponse(responseCode = "204", description = "El huésped no tiene citas activas")
    @ApiResponse(responseCode = "409", description = "El huésped tiene una reserva activa y no puede agendar otra",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<Void> huespedTieneReservaActiva(
            @PathVariable @Positive(message = "El idHuesped debe ser positivo") Long idHuesped
    ) {
        service.validarHuespedNoTieneReservasActivas(idHuesped);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/habitacion-reserva/{idHabitacion}")
    @Operation(summary = "Obtener reserva vigente por ID de habitación",
            description = "Consumo interno para validar la capacidad de la habitación frente a una reserva activa")
    @ApiResponse(responseCode = "200", description = "Reserva vigente encontrada exitosamente")
    @ApiResponse(responseCode = "404", description = "No existe una reserva activa para la habitación especificada",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<ReservaResponse> obtenerNumeroPersonasReserva(
            @PathVariable @Positive(message = "El idHabitacion debe ser positivo") Long idHabitacion) {
        ReservaResponse response = service.obtenerNumeroPersonasReserva(idHabitacion);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reportes/historico")
    @Operation(summary = "Obtener reporte histórico total de reservas",
            description = "Devuelve todas las reservas registradas sin importar su estado")
    @ApiResponse(responseCode = "200", description = "Reporte histórico generado exitosamente")
    @ApiResponse(responseCode = "403", description = "Prohibido. El usuario no cuenta con los permisos correspondientes")
    public ResponseEntity<List<ReservaResponse>> obtenerReporteHistorico() {
        List<ReservaResponse> reporte = service.obtenerReporteHistorico();
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/reportes/por-estado")
    @Operation(summary = "Obtener reservas agrupadas por estado",
            description = "Devuelve todas las reservas agrupadas por su estado reserva")
    @ApiResponse(responseCode = "200", description = "Reporte agrupado generado exitosamente")
    @ApiResponse(responseCode = "403", description = "Prohibido. El usuario no cuenta con los permisos correspondientes")
    public ResponseEntity<Map<String, List<ReservaResponse>>> obtenerReportePorEstado() {
        Map<String, List<ReservaResponse>> reporteAgrupado = service.obtenerReportePorEstado();
        return ResponseEntity.ok(reporteAgrupado);
    }
}
