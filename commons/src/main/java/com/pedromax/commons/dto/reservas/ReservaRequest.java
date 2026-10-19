package com.pedromax.commons.dto.reservas;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Schema(description = "Datos necesarios para registrar o actualizar una reserva")
public record ReservaRequest(

        @Schema(description = "Identificador único del huésped", example = "1")
        @NotNull(message = "El id del huésped es requerido")
        @Positive(message = "El id del huésped debe ser un número positivo")
        Long idHuesped,

        @Schema(description = "Identificador único de la habitación", example = "1")
        @NotNull(message = "El id de la habitación es requerido")
        @Positive(message = "El id de la habitación debe ser un número positivo")
        Long idHabitacion,

        @Schema(description = "Fecha de entrada al hotel (Check-in)", example = "2026-10-25", type = "string", format = "date")
        @NotNull(message = "La fecha de entrada es requerida")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaEntrada,

        @Schema(description = "Fecha de salida del hotel (Check-out)", example = "2026-10-30", type = "string", format = "date")
        @NotNull(message = "La fecha de salida es requerida")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaSalida,

        @Schema(description = "Número de huéspedes que se hospedarán", example = "2")
        @NotNull(message = "El número de personas es requerido")
        @Min(value = 1, message = "El número de personas debe ser mínimo 1")
        Integer numPersonas
) {}
