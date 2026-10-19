package com.pedromax.commons.dto.huespedes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para registrar o actualizar un huésped")
public record HuespedRequest(

        @Schema(description = "Nombre del huésped", example = "Pedro")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido del huésped", example = "Vásquez")
        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
        String apellido,

        @Schema(description = "Correo electrónico", example = "pedro.vasquez@example.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @Schema(description = "Teléfono del huésped", example = "2221234567")
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^[0-9]{10}", message = "El teléfono debe tener exactamente 10 dígitos")
        String telefono,

        @Schema(description = "Tipo de documento", example = "INE")
        @NotBlank(message = "El tipo de documento es obligatorio")
        @Pattern(regexp = "^(INE|LICENCIA|PASAPORTE)$", message = "El tipo de documento debe ser: INE, LICENCIA o PASAPORTE")
        String tipoDocumento,

        @Schema(description = "Número único del documento de identidad", example = "ABCDE1234567890", minLength = 8, maxLength = 20)
        @NotBlank(message = "El número de documento es obligatorio")
        @Size(min = 8, max = 20, message = "El número de documento debe tener entre 8 y 20 caracteres")
        String numeroDocumento,

        @Schema(description = "País de origen del huésped", example = "Mexicana")
        @NotBlank(message = "La nacionalidad es obligatoria")
        String nacionalidad
) {}
