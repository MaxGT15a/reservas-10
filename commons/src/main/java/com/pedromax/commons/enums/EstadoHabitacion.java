package com.pedromax.commons.enums;

import com.pedromax.commons.exceptions.RecursoNoEncontradoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum EstadoHabitacion {

    DISPONIBLE(1L, "Lista para asignarse") {
        @Override
        public Set<EstadoHabitacion> puedeCambiar() {
            return EnumSet.of(LIMPIEZA, MANTENIMIENTO);
        }
    },
    OCUPADA(2L, "Asignada a una reserva vigente") {
        @Override
        public Set<EstadoHabitacion> puedeCambiar() {
            return Set.of();
        }
    },
    LIMPIEZA(3L, "Limpieza de habitación en proceso") {
        @Override
        public Set<EstadoHabitacion> puedeCambiar() {
            return EnumSet.of(DISPONIBLE, MANTENIMIENTO);
        }
    },
    MANTENIMIENTO(4L, "En reparación"){
        @Override
        public Set<EstadoHabitacion> puedeCambiar() {
            return EnumSet.of(DISPONIBLE, LIMPIEZA);
        }
    };

    private final Long codigo;
    private final String descripcion;

    public abstract Set<EstadoHabitacion> puedeCambiar();
    public boolean puedeCambiarA(EstadoHabitacion estadoHabitacion){
        return puedeCambiar().contains(estadoHabitacion);
    }

    public static EstadoHabitacion obtenerEstadoHabitacionPorCodigo(Long codigo){
        for (EstadoHabitacion e : values()){
            if (Objects.equals(e.codigo, codigo))
                return e;
        }

        throw new RecursoNoEncontradoException("Código de estado de habitación no válido: " + codigo);
    }
}
