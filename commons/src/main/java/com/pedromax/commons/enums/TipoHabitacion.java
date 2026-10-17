package com.pedromax.commons.enums;

import com.pedromax.commons.exceptions.DatoInvalidoException;
import com.pedromax.commons.utils.StringCustomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoHabitacion {

    SENCILLA("Sencilla"),
    DOBLE("Doble"),
    SUITE("Suite");

    private final String descripcion;

    public static TipoHabitacion obtenerTipoHabitacionPorDescripcion(String descripcion){
        StringCustomUtils.validarNoVacio(descripcion, "La descripción es requerida");
        String descripcionNormalizada = StringCustomUtils.quitarAcentos(descripcion);

        for (TipoHabitacion tipoHabitacion : values()){
            if (StringCustomUtils.quitarAcentos(tipoHabitacion.descripcion).equalsIgnoreCase(descripcionNormalizada))
                return tipoHabitacion;
        }

        throw new DatoInvalidoException("No existe un tipo de habitación con la descripción: " + descripcion);
    }
}
