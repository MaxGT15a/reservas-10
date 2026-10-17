package com.pedromax.commons.dto.habitaciones.huespedes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Información de un huesped")
public record HuespedResponse(

        @Schema(description = "Identificador único del huesped", example = "1")
        Long id,

        @Schema(description = "Nombre del huesped", example = "Juan")
        String nombre,

        @Schema(description = "Apellido del huesped", example = "Pérez")
        String apellido,

        @Schema(description = "Correo electrónico del huesped", example = "juan.perez@example.com")
        String email,

        @Schema(description = "Teléfono del huesped", example = "1234567890")
        String telefono,

        @Schema(description = "Nacionalidad del huesped", example = "Colombiane")
        String nacionalidad,

        @Schema(description = "Tipo de documento del huesped", example = "INE")
        String tipoDocumento,

        @Schema(description = "Número de documento del huesped", example = "1234567890")
        String numeroDocumento
) {
}
