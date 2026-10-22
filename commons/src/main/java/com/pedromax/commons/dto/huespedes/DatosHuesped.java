package com.pedromax.commons.dto.huespedes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un huésped asignado a una reservas")
public record DatosHuesped(

        @Schema(description = "Identificador único del huésped", example = "1")
        Long id,

        @Schema(description = "Nombre completo del huésped", example = "Pedro Vásquez")
        String nombreCompleto,

        @Schema(description = "Correo electrónico del huésped", example = "pedro.vásquez@example.com")
        String email,

        @Schema(description = "Teléfono del huésped", example = "2221234567")
        String telefono,

        @Schema(description = "Documentación oficial del huésped", example = "INE - ABCDE1234567890")
        String documentoIdentidad,

        @Schema(description = "Nacionalidad del huésped", example = "Mexicana")
        String nacionalidad
) {}