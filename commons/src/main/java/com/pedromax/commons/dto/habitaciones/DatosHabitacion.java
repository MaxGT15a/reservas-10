package com.pedromax.commons.dto.habitaciones;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Datos de una habitación asociada a una reserva")
public record DatosHabitacion(

        @Schema(description = "Número de la habitación", example = "1")
        String numero,

        @Schema(description = "Precio de la habitación", example = "789.00")
        String precio,

        @Schema(description = "Capacidad de huéspedes de la habitación", example = "2")
        String capacidad,

        @Schema(description = "Tipo de habitación", example = "Sencilla")
        String tipoHabitacion
) {}