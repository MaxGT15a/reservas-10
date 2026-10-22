package com.pedromax.reservas.repositories;

import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.reservas.entities.Reserva;
import com.pedromax.reservas.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByEstadoRegistro(EstadoRegistro estadoRegistro);
    Optional<Reserva> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

    boolean existsByIdHuespedAndEstadoRegistroAndEstadoReservaIn(
            Long idHuesped, EstadoRegistro estadoRegistro, Collection<EstadoReserva> estadosReserva);

    boolean existsByIdHabitacionAndEstadoRegistroAndEstadoReservaInAndIdNot(
            Long idHabitacion, EstadoRegistro estadoRegistro, Collection<EstadoReserva> estadosReserva, Long id);

    Optional<Reserva> findFirstByIdHabitacionAndEstadoRegistroAndEstadoReservaIn(
            Long idHabitacion, EstadoRegistro estadoRegistro, Collection<EstadoReserva> estadosReserva);

    List<Reserva> findByEstadoRegistroAndEstadoReservaIn(
            EstadoRegistro estadoRegistro, Collection<EstadoReserva> estadosReserva);
}
