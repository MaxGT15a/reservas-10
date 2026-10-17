package com.pedromax.habitaciones.mapper;

import com.pedromax.commons.dto.habitaciones.HabitacionRequest;
import com.pedromax.commons.dto.habitaciones.HabitacionResponse;
import com.pedromax.commons.enums.TipoHabitacion;
import com.pedromax.commons.mapper.CommonMapper;
import com.pedromax.habitaciones.entities.Habitacion;
import org.springframework.stereotype.Component;

@Component
public class HabitacionMapper implements CommonMapper<HabitacionRequest, HabitacionResponse, Habitacion> {

    @Override
    public Habitacion requestAEntidad(HabitacionRequest request) {
        return request == null ? null : Habitacion.crear(
                request.numero(),
                request.precio(),
                request.capacidad()
        );
    }

    public Habitacion requestAEntidad(HabitacionRequest request, TipoHabitacion tipoHabitacion){
        Habitacion habitacion = requestAEntidad(request);
        habitacion.actualizarTipoHabitacion(tipoHabitacion);
        return habitacion;
    }

    @Override
    public HabitacionResponse entidadAResponse(Habitacion entidad) {
        return entidad == null ? null : new HabitacionResponse(
                entidad.getId(),
                entidad.getNumero(),
                entidad.getPrecio(),
                entidad.getCapacidad(),
                entidad.getTipoHabitacion().getDescripcion(),
                entidad.getEstadoHabitacion().getDescripcion()
        );
    }
}
