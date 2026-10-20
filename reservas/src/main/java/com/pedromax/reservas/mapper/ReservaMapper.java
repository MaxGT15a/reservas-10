package com.pedromax.reservas.mapper;

import com.pedromax.commons.mapper.CommonMapper;
import com.pedromax.commons.dto.habitaciones.DatosHabitacion;
import com.pedromax.commons.dto.huespedes.DatosHuesped;
import com.pedromax.commons.dto.reservas.ReservaRequest;
import com.pedromax.commons.dto.reservas.ReservaResponse;
import com.pedromax.reservas.entities.Reserva;
import com.pedromax.commons.dto.habitaciones.HabitacionResponse;
import com.pedromax.commons.dto.huespedes.HuespedResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ReservaMapper implements CommonMapper<ReservaRequest, ReservaResponse, Reserva> {

    @Override
    public Reserva requestAEntidad(ReservaRequest request) {
        throw new UnsupportedOperationException("Usar el método sobrecargado requestAEntidad");
    }

    public Reserva requestAEntidad(ReservaRequest request, BigDecimal precioNoche){
        return request == null ? null : Reserva.crear(
                request.idHuesped(),
                request.idHabitacion(),
                request.numPersonas(),
                precioNoche,
                request.fechaEntrada(),
                request.fechaSalida()
        );
    }

    @Override
    public ReservaResponse entidadAResponse(Reserva entidad) {
        return entidad == null ? null : new ReservaResponse(
                entidad.getId(),
                null,
                null,
                entidad.getFechaEntrada(),
                entidad.getFechaSalida(),
                entidad.getNumPersonas(),
                entidad.getPrecioNoche(),
                entidad.getPrecioTotal(),
                entidad.getEstadoReserva().getDescripcion()
        );
    }

    public ReservaResponse entidadAResponse(Reserva entidad, HuespedResponse huesped, HabitacionResponse habitacion) {
        return entidad == null ? null : new ReservaResponse(
                entidad.getId(),
                huespedResponseADatosHuesped(huesped),
                habitacionResponseADatosHabitacion(habitacion),
                entidad.getFechaEntrada(),
                entidad.getFechaSalida(),
                entidad.getNumPersonas(),
                entidad.getPrecioNoche(),
                entidad.getPrecioTotal(),
                entidad.getEstadoReserva().getDescripcion()
        );
    }

    private DatosHuesped huespedResponseADatosHuesped(HuespedResponse huesped) {
        return huesped == null ? null : new DatosHuesped(
                String.join(" ", huesped.nombre(), huesped.apellido()).trim(),
                huesped.email().toLowerCase(),
                huesped.telefono(),
                String.join(" - ", huesped.tipoDocumento(), huesped.numeroDocumento()),
                huesped.nacionalidad()
        );
    }

    private DatosHabitacion habitacionResponseADatosHabitacion(HabitacionResponse habitacion) {
        return habitacion == null ? null : new DatosHabitacion(
                "Habitación N° " + habitacion.numero(),
                "$" + habitacion.precio() + " MXN",
                habitacion.capacidad() + " personas máximo",
                habitacion.tipoHabitacion()
        );
    }
}
