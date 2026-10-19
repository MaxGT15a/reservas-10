package com.pedromax.reservas.enums;

import com.pedromax.commons.exceptions.DatoInvalidoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum EstadoReserva {

    CONFIRMADA(1, "Reserva creada", true, false){
        @Override
        public Set<EstadoReserva> puedeCambiar() {
            return EnumSet.of(EN_CURSO, CANCELADA);
        }
    },
    EN_CURSO(2, "Check-in realizado", true, false){
        @Override
        public Set<EstadoReserva> puedeCambiar() {
            return EnumSet.of(FINALIZADA);
        }
    },
    FINALIZADA(3, "Check-out realizado", false, true){
        @Override
        public Set<EstadoReserva> puedeCambiar() {
            return Set.of();
        }
    },
    CANCELADA(4, "Reserva cancelada", false, true){
        @Override
        public Set<EstadoReserva> puedeCambiar() {
            return Set.of();
        }
    };

    private final Integer codigo;
    private final String descripcion;
    private final boolean isActualizable;
    private final boolean isEliminable;

    public abstract Set<EstadoReserva> puedeCambiar();
    public boolean puedeCambiarA(EstadoReserva estadoReserva){
        return puedeCambiar().contains(estadoReserva);
    }

    public static EstadoReserva obtenerEstadoReservaPorCodigo(Integer codigo){
        for (EstadoReserva e : values()){
            if (Objects.equals(e.codigo, codigo))
                return e;
        }

        throw new DatoInvalidoException("Código de estado de reserva no válido: " + codigo);
    }
}
