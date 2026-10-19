package com.pedromax.commons.dto.huespedes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información completa del huésped")
public record HuespedResponse(

        @Schema(description = "Identificador único del huésped", example = "1")
        Long id,

        @Schema(description = "Nombre del huésped", example = "Pedro")
        String nombre,

        @Schema(description = "Apellido del huésped", example = "Vásquez")
        String apellido,

        @Schema(description = "Correo electrónico del huésped", example = "pedro.vasquez@example.com")
        String email,

        @Schema(description = "Teléfono de contacto", example = "2221234567")
        String telefono,

        @Schema(description = "Tipo de documento de identidad", example = "INE")
        String tipoDocumento,

        @Schema(description = "Número del documento de identidad", example = "ABCDE1234567890")
        String numeroDocumento,

        @Schema(description = "Nacionalidad registrada", example = "Mexicana")
        String nacionalidad,

        @Schema(description = "Estado del registro", example = "ACTIVO")
        String estadoRegistro
) {}
