package com.pedromax.commons.dto.reservas;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pedromax.commons.dto.habitaciones.DatosHabitacion;
import com.pedromax.commons.dto.huespedes.DatosHuesped;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Información detallada de una reservación hotelera registrada")
public record ReservaResponse(

        @Schema(description = "Identificador único de la reserva", example = "1")
        Long id,

        @Schema(description = "Información del huésped asociado a la reserva")
        DatosHuesped huesped,

        @Schema(description = "Información de la habitación asociada a la reserva")
        DatosHabitacion habitacion,

        @Schema(description = "Fecha de entrada al hotel (Check-in)", example = "2026-10-25", type = "string", format = "date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaEntrada,

        @Schema(description = "Fecha de salida del hotel (Check-out)", example = "2026-10-30", type = "string", format = "date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaSalida,

        @Schema(description = "Número de huéspedes registrados en la reserva", example = "2")
        Integer numPersonas,

        @Schema(description = "Precio por noche de la habitación", example = "1250.50")
        BigDecimal precioNoche,

        @Schema(description = "Precio total de la reserva", example = "6252.50")
        BigDecimal precioTotal,

        @Schema(description = "Estado de la reserva", example = "CONFIRMADA")
        String estadoReserva,

        @Schema(description = "Estado del registro (Activo o Eliminado)", example = "Activo")
        String estadoRegistro
) {}