package com.pedromax.commons.enums;

import com.pedromax.commons.exceptions.DatoInvalidoException;
import com.pedromax.commons.utils.StringCustomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoDocumento {

    INE("Ine"),
    LICENCIA("Licencia"),
    PASAPORTE("Pasaporte");

    private final String descripcion;

    public static TipoDocumento obtenerTipoDocumentoPorDescripcion(String descripcion){
        StringCustomUtils.validarNoVacio(descripcion, "La descripción es requerida");
        String descripcionNormalizada = StringCustomUtils.quitarAcentos(descripcion);

        for (TipoDocumento tipoDocumento : values()){
            if (StringCustomUtils.quitarAcentos(tipoDocumento.descripcion).equalsIgnoreCase(descripcionNormalizada))
                return tipoDocumento;
        }

        throw new DatoInvalidoException("No existe un tipo de documento con la descripción: " + descripcion);
    }
}
