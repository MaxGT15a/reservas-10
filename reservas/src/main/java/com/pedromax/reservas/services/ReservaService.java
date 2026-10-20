package com.pedromax.reservas.services;

import com.pedromax.commons.dto.reservas.ReservaRequest;
import com.pedromax.commons.dto.reservas.ReservaResponse;
import com.pedromax.commons.service.CrudService;

import java.util.List;
import java.util.Map;

public interface ReservaService extends CrudService<ReservaRequest, ReservaResponse> {

    void actualizarEstadoReserva(Long idReserva, Integer idEstado);
    void validarHuespedNoTieneReservasActivas(Long idHuesped);
    ReservaResponse obtenerNumeroPersonasReserva(Long idHabitacion);

    List<ReservaResponse> obtenerReporteHistorico();
    Map<String, List<ReservaResponse>> obtenerReportePorEstado();
}
