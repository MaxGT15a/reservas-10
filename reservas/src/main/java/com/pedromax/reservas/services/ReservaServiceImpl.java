package com.pedromax.reservas.services;

import com.pedromax.commons.enums.EstadoHabitacion;
import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.RecursoNoEncontradoException;
import com.pedromax.commons.dto.habitaciones.HabitacionResponse;
import com.pedromax.commons.dto.huespedes.HuespedResponse;
import com.pedromax.commons.dto.reservas.ReservaRequest;
import com.pedromax.commons.dto.reservas.ReservaResponse;
import com.pedromax.commons.clients.HabitacionClient;
import com.pedromax.commons.clients.HuespedClient;
import com.pedromax.reservas.entities.Reserva;
import com.pedromax.reservas.enums.EstadoReserva;
import com.pedromax.reservas.mapper.ReservaMapper;
import com.pedromax.reservas.repositories.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaMapper reservaMapper;
    private final HuespedClient huespedClient;
    private final HabitacionClient habitacionClient;

    private static final List<EstadoReserva> estadosReserva = List.of(EstadoReserva.CONFIRMADA, EstadoReserva.EN_CURSO);

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> listar() {
        log.info("Listando todas las reservas activas");

        return reservaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(reserva -> reservaMapper.entidadAResponse(reserva,
                        obtenerHuespedSinEstado(reserva.getIdHuesped()),
                        obtenerHabitacionSinEstado(reserva.getIdHabitacion()))
                ).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponse obtenerPorId(Long id) {
        Reserva reserva = obtenerReservaActivaPorId(id);
        return reservaMapper.entidadAResponse(reserva,
                obtenerHuespedSinEstado(reserva.getIdHuesped()), obtenerHabitacionSinEstado(reserva.getIdHabitacion())
        );
    }

    @Override
    public ReservaResponse registrar(ReservaRequest request) {
        log.info("Registrando nueva reserva");

        HuespedResponse huesped = obtenerHuespedActivo(request.idHuesped());
        validarHuespedNoTieneReservasActivas(huesped.id());

        HabitacionResponse habitacion = obtenerHabitacionActiva(request.idHabitacion());
        validarHabitacionActivaDisponible(habitacion);

        if (request.numPersonas() > habitacion.capacidad())
            throw new ConflictoException("El numero de personas de la reserva no pude ser mayor a la capacidad de la habitación");

        Reserva reserva = reservaMapper.requestAEntidad(request, habitacion.precio());
        reservaRepository.saveAndFlush(reserva);

        log.info("Reserva registrada exitosamente");
        cambiarEstadoHabitacionSegunEstadoReserva(habitacion.id(), reserva.getEstadoReserva());
        return reservaMapper.entidadAResponse(reserva, huesped, habitacion);
    }

    @Override
    public ReservaResponse actualizar(ReservaRequest request, Long id) {
        Reserva reserva = obtenerReservaActivaPorId(id);

        if (!request.idHuesped().equals(reserva.getIdHuesped()) || !request.idHabitacion().equals(reserva.getIdHabitacion())) {
            throw new ConflictoException("No está permitido cambiar el huésped ni la habitación asignada a la reserva");
        }

        if (reserva.getEstadoReserva() == EstadoReserva.EN_CURSO) {
            if (!request.fechaEntrada().equals(reserva.getFechaEntrada())) {
                throw new ConflictoException("No se permite modificar la fecha de entrada si el check-in ya fue realizado");
            }
        }

        HuespedResponse huesped = obtenerHuespedActivo(reserva.getIdHuesped());
        HabitacionResponse habitacion = obtenerHabitacionActiva(reserva.getIdHabitacion());
        validarCambiosSinTraslape(request.idHabitacion(), id);

        if (request.numPersonas() > habitacion.capacidad())
            throw new ConflictoException("El numero de personas de la reserva no pude ser mayor a la capacidad de la habitación");

        log.info("Actualizando reserva con id: {}", id);
        reserva.actualizar(
                request.idHuesped(),
                request.idHabitacion(),
                request.numPersonas(),
                request.fechaEntrada(),
                request.fechaSalida()
        );
        reservaRepository.saveAndFlush(reserva);

        log.info("Reserva con ID {} actualizada correctamente", id);
        return reservaMapper.entidadAResponse(reserva, huesped, habitacion);
    }

    @Override
    public void actualizarEstadoReserva(Long idReserva, Integer idEstado) {
        Reserva reserva = obtenerReservaActivaPorId(idReserva);
        log.info("Actualizando estado de la reserva con ID {}", idReserva);

        reserva.actualizarEstadoReserva(EstadoReserva.obtenerEstadoReservaPorCodigo(idEstado));
        reservaRepository.saveAndFlush(reserva);

        cambiarEstadoHabitacionSegunEstadoReserva(reserva.getIdHabitacion(), reserva.getEstadoReserva());
        log.info("Estado de la reserva {} actualizando correctamente", idReserva);
    }

    @Override
    public void eliminar(Long id) {
        Reserva reserva = obtenerReservaActivaPorId(id);
        log.info("Eliminando una reserva con ID: {}", id);

        reserva.eliminar();
        reservaRepository.saveAndFlush(reserva);

        log.info("Reserva con id {} ha sido marcada como eliminada", id);
    }

    @Override
    public void validarHuespedNoTieneReservasActivas(Long idHuesped) {
        log.info("Validando que el huésped con id: {} no tenga reservas activas", idHuesped);

        if (reservaRepository.existsByIdHuespedAndEstadoRegistroAndEstadoReservaIn(idHuesped, EstadoRegistro.ACTIVO, estadosReserva))
            throw new ConflictoException("El huésped con id: " + idHuesped + " ya tiene una reserva activa");
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponse obtenerNumeroPersonasReserva(Long idHabitacion) {
        log.info("Buscando reserva vigente para la habitación con ID: {}", idHabitacion);

        Reserva reserva = reservaRepository
                .findFirstByIdHabitacionAndEstadoRegistroAndEstadoReservaIn(idHabitacion, EstadoRegistro.ACTIVO, estadosReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró ninguna reserva vigente para la habitación con ID: " + idHabitacion));

        return reservaMapper.entidadAResponse(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReporteHistorico() {
        log.info("Generando reporte histórico total de reservas...");

        return reservaRepository.findAll().stream().map(reserva ->
                reservaMapper.entidadAResponse(reserva,
                        obtenerHuespedSinEstado(reserva.getIdHuesped()),
                        obtenerHabitacionSinEstado(reserva.getIdHabitacion())
                )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<ReservaResponse>> obtenerReportePorEstado() {
        log.info("Generando reporte de reservas agrupadas por Estado de Reserva...");

        return reservaRepository.findAll().stream().map(reserva ->
                reservaMapper.entidadAResponse(reserva,
                                obtenerHuespedSinEstado(reserva.getIdHuesped()),
                                obtenerHabitacionSinEstado(reserva.getIdHabitacion())
                )).collect(Collectors.groupingBy(ReservaResponse::estadoReserva));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> obtenerIdsHuespedesConReservaActiva() {
        log.info("Consultando huéspedes con reservas activas");

        return reservaRepository
                .findByEstadoRegistroAndEstadoReservaIn(EstadoRegistro.ACTIVO, estadosReserva).stream()
                .map(Reserva::getIdHuesped)
                .distinct()
                .toList();
    }

    //========================================================\\

    private Reserva obtenerReservaActivaPorId(Long id) {
        log.info("Buscando reserva activa con ID {}", id);

        return reservaRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva activa no encontrada con ID: " + id));
    }

    private HuespedResponse obtenerHuespedActivo(Long idHuesped){
        log.info("Buscando huésped activo {} en el servicio remoto...", idHuesped);
        return huespedClient.obtenerHuespedPorIdActivo(idHuesped);
    }

    private HuespedResponse obtenerHuespedSinEstado(Long idHuesped){
        log.info("Buscando huésped sin estado {} en el servicio remoto...", idHuesped);
        return huespedClient.obtenerHuespedPorIdSinEstado(idHuesped);
    }

    private HabitacionResponse obtenerHabitacionActiva(Long idHabitacion){
        log.info("Buscando habitación activa {} en el servicio remoto...", idHabitacion);
        return habitacionClient.obtenerHabitacionPorIdActiva(idHabitacion);
    }

    private HabitacionResponse obtenerHabitacionSinEstado(Long idHabitacion){
        log.info("Buscando habitación sin estado {} en el servicio remoto...", idHabitacion);
        return habitacionClient.obtenerHabitacionPorIdSinEstado(idHabitacion);
    }

    private void validarHabitacionActivaDisponible(HabitacionResponse habitacion){
        log.info("Validando si la habitación activa está disponible...");
        if (!EstadoHabitacion.DISPONIBLE.getDescripcion().equals(habitacion.estadoHabitacion()))
            throw new ConflictoException("La habitación activa no está disponible por el momento");
    }

    private void actualizarEstadoHabitacion(Long idHabitacion, Long idEstadoHabitacion){
        log.info("Actualizando el estado de la habitación en el servicio remoto");
        habitacionClient.actualizarEstadoHabitacion(idHabitacion, idEstadoHabitacion);
        log.info("Estado de la habitación actualizado en el servicio remoto");
    }

    private void cambiarEstadoHabitacionSegunEstadoReserva(Long idHabitacion, EstadoReserva estadoReserva){
        switch (estadoReserva){
            case CONFIRMADA, EN_CURSO ->
                    actualizarEstadoHabitacion(idHabitacion, EstadoHabitacion.OCUPADA.getCodigo());
            case FINALIZADA ->
                    actualizarEstadoHabitacion(idHabitacion, EstadoHabitacion.LIMPIEZA.getCodigo());
            case CANCELADA ->
                    actualizarEstadoHabitacion(idHabitacion, EstadoHabitacion.DISPONIBLE.getCodigo());
        }
    }

    private void validarCambiosSinTraslape(Long idHabitacion, Long idReserva) {
        if (reservaRepository.existsByIdHabitacionAndEstadoRegistroAndEstadoReservaInAndIdNot(idHabitacion, EstadoRegistro.ACTIVO, estadosReserva, idReserva)) {
            throw new ConflictoException("La modificación entra en conflicto con una reserva vigente sobre esta habitación");
        }
    }
}
