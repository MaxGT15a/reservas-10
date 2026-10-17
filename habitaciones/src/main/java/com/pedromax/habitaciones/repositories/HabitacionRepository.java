package com.pedromax.habitaciones.repositories;

import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.habitaciones.entities.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    List<Habitacion> findByEstadoRegistro(EstadoRegistro estadoRegistro);
    Optional<Habitacion> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

    boolean existsByNumeroAndEstadoRegistro(Integer numero, EstadoRegistro estadoRegistro);
    boolean existsByNumeroAndEstadoRegistroAndIdNot(Integer numero, EstadoRegistro estadoRegistro, Long id);
}