package com.pedromax.commons.dto.habitaciones;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Datos necesarios para registrar o actualizar una habitación")
public record HabitacionRequest(

        @Schema(description = "Número de la habitación", example = "1")
        @NotNull(message = "El número de la habitación es requerido")
        @Positive(message = "El número de la habitación debe ser positivo")
        Integer numero,

        @Schema(description = "Precio de la habitación", example = "789.00")
        @NotNull(message = "El precio de la habitación es requerido")
        @Positive(message = "El precio de la habitación debe ser positivo")
        BigDecimal precio,

        @Schema(description = "Capacidad de huéspedes de la habitación", example = "2")
        @NotNull(message = "La capacidad de huéspedes de la habitación es requerida")
        @Positive(message = "La capacidad de huéspedes de la habitación debe ser positiva")
        Integer capacidad,

        @Schema(description = "Tipo de habitación (Puede estar en mayúsculas o minúsculas)", example = "SENCILLA")
        @NotBlank(message = "La categoría es requerida")
        String tipoHabitacion
) {}
