package com.pedromax.commons.utils;

import com.pedromax.commons.exceptions.DatoInvalidoException;

import java.math.BigDecimal;

public class ValoresNumericosUtils {

    public static <N extends Number> void validarNumeroRequerido(N numero) {
        if (numero == null)
            throw new DatoInvalidoException("El valor numérico es requerido");
    }

    public static void validarNumeroPositivo(Number numero, String mensaje) {
        validarNumeroRequerido(numero);

        if (numero.doubleValue() <= 0.0) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    public static void validarBigDecimalPositivo(BigDecimal numero, String mensaje) {
        validarNumeroRequerido(numero);

        if (numero.compareTo(BigDecimal.ZERO) < 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarRangoShort(Short numero, short min, short max, String mensaje) {
        validarNumeroRequerido(numero);

        if (numero < min || numero > max)
            throw new IllegalArgumentException(mensaje);
    }
}
