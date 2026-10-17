package com.pedromax.habitaciones.services;

import com.pedromax.commons.dto.habitaciones.HabitacionRequest;
import com.pedromax.commons.dto.habitaciones.HabitacionResponse;
import com.pedromax.commons.service.CrudService;

public interface HabitacionService extends CrudService<HabitacionRequest, HabitacionResponse> {

    HabitacionResponse obtenerHabitacionPorIdSinEstado(Long id);
    void actualizarEstadoHabitacion(Long idHabitacion, Long idEstadoHabitacion);
}
