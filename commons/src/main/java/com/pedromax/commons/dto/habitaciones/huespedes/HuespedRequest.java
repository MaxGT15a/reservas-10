package com.pedromax.commons.dto.habitaciones.huespedes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para crear un huesped a partir de un cigoto")
public record HuespedRequest(

        @Schema(description = "Nombre del huesped 😂😂😂", example = "Tom")
        @NotBlank(message = "😂Nombre😂del😂huesped😂es😂obligatorio😂")
        @Size(min = 2, max = 50)
        String nombre,

        @Schema(description = "Apellido del huesped", example = "Cruise")
        @NotBlank(message = "Apellido del huesped es obligatorio")
        @Size(min = 2, max = 50)
        String apellido,

        @Schema(description = "Correo electrónico del huesped", example = "example@example.com")
        @NotBlank(message = "Correo electrónico del huesped es obligatorio")
        @Email(message = "Correo electrónico inválido")
        String email,

        @Schema(description = "Teléfono del huesped", example = "1234567890")
        @NotBlank(message = "Teléfono del huesped es obligatorio")
        @Size(min = 10, max = 10, message = "Teléfono inválido debe de tener 10 dígitos")
        String telefono,

        @Schema(description = "Tipo de documento del huesped", example = "INE, LICENCIA, PASAPORTE")
        @NotBlank(message = "Tipo de documento del huesped es obligatorio")
        String tipoDocumento,

        @Schema(description = "Número de documento del huesped", example = "1234567890")
        @NotBlank(message = "Número de documento del huesped es obligatorio")
        @Size(min = 8, max = 20, message = "Número de documento inválido debe de tener entre 8 y 20 dígitos")
        String numeroDocumento,

        @Schema(description = "Nacionalidad del huesped", example = "Colombiane")
        @NotBlank(message = "Nacionalidad del huesped es obligatorio")
        @Size(min = 2, max = 50, message = "Nacionalidad debe de tener entre 2 y 50 caracteres")
        String nacionalidad
) {
}
