package com.pedromax.commons.dto.habitaciones;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Información de una habitación")
public record HabitacionResponse(

        @Schema(description = "Identificador único de la habitación", example = "1")
        Long id,

        @Schema(description = "Número de la habitación", example = "1")
        Integer numero,

        @Schema(description = "Precio de la habitación", example = "789.00")
        BigDecimal precio,

        @Schema(description = "Capacidad de huéspedes de la habitación", example = "2")
        Integer capacidad,

        @Schema(description = "Tipo de habitación", example = "Sencilla")
        String tipoHabitacion,

        @Schema(description = "Estado de la habitación", example = "Disponible")
        String estadoHabitacion
) {}