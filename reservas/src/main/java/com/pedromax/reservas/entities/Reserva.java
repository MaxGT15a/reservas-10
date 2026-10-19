package com.pedromax.reservas.entities;

import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.DatoInvalidoException;
import com.pedromax.commons.utils.ValoresNumericosUtils;
import com.pedromax.reservas.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "RESERVAS")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESERVA")
    private Long id;

    @Column(name = "ID_HUESPED", nullable = false)
    private Long idHuesped;

    @Column(name = "ID_HABITACION", nullable = false)
    private Long idHabitacion;

    @Column(name = "FECHA_ENTRADA", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "FECHA_SALIDA", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "NUMERO_PERSONAS", nullable = false)
    private Integer numPersonas;

    @Column(name = "PRECIO_NOCHE", nullable = false)
    private BigDecimal precioNoche;

    @Column(name = "PRECIO_TOTAL", nullable = false)
    private BigDecimal precioTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_RESERVA", nullable = false)
    private EstadoReserva estadoReserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    private static void validarId(Long id, String campo){
        ValoresNumericosUtils.validarNumeroPositivo(id, "El id del/la " + campo + " es requerido(a) y debe ser positivo(a)");
    }

    private static void esFechaInicioMenor(LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new DatoInvalidoException("Las fechas de entrada y salida son requeridas");
        }

        if (!fechaEntrada.isBefore(fechaSalida))
            throw new DatoInvalidoException("La fecha de entrada no puede ser mayor que la fecha de salida");
    }

    private static BigDecimal calcularPrecioFinal(BigDecimal precioNoche, LocalDate fechaEntrada, LocalDate fechaSalida){
        long numDias = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        return precioNoche.multiply(BigDecimal.valueOf(numDias));
    }

    private static void validarDatos(Long idHuesped, Long idHabitacion, Integer numPersonas, LocalDate fechaEntrada, LocalDate fechaSalida){
        validarId(idHuesped, "huésped");
        validarId(idHabitacion, "habitación");
        esFechaInicioMenor(fechaEntrada, fechaSalida);

        ValoresNumericosUtils.validarNumeroPositivo(numPersonas,
                "El número de personas es requerido y debe ser positivo");
    }

    private void validarNoEliminada(){
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictoException("La reserva ya está eliminada");
    }

    private void validarEliminacionPermitida(){
        validarNoEliminada();

        if (!estadoReserva.isEliminable())
            throw new ConflictoException("La reserva con estado " + estadoReserva + " no puede eliminarse");
    }

    private void validarActualizacionPermitida(){
        validarNoEliminada();

        if (!estadoReserva.isActualizable())
            throw new ConflictoException("La reserva con estado " + estadoReserva + " no puede actualizarse");
    }

    public void eliminar(){
        validarEliminacionPermitida();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizar(Long idHuesped, Long idHabitacion, Integer numPersonas, LocalDate fechaEntrada, LocalDate fechaSalida){
        validarDatos(idHuesped, idHabitacion, numPersonas, fechaEntrada, fechaSalida);
        validarActualizacionPermitida();

        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.numPersonas = numPersonas;
        this.precioTotal = calcularPrecioFinal(this.precioNoche, this.fechaEntrada, this.fechaSalida);
    }

    public void actualizarEstadoReserva(EstadoReserva nuevoEstado){
        validarActualizacionPermitida();

        if (nuevoEstado == null)
            throw new DatoInvalidoException("El nuevo estado de la reserva es requerido");

        if (!estadoReserva.puedeCambiarA(nuevoEstado)) {
            throw new ConflictoException("La reserva con estado " + estadoReserva + " solo puede cambiar a " + estadoReserva.puedeCambiar());
        }

        this.estadoReserva = nuevoEstado;
    }

    public static Reserva crear(Long idHuesped, Long idHabitacion, Integer numPersonas, BigDecimal precioNoche,
                                LocalDate fechaEntrada, LocalDate fechaSalida){
        validarDatos(idHuesped, idHabitacion, numPersonas, fechaEntrada, fechaSalida);
        ValoresNumericosUtils.validarBigDecimalPositivo(precioNoche, "El precio noche es requerido y debe ser positivo");

        return Reserva.builder()
                .idHuesped(idHuesped)
                .idHabitacion(idHabitacion)
                .fechaEntrada(fechaEntrada)
                .fechaSalida(fechaSalida)
                .numPersonas(numPersonas)
                .precioNoche(precioNoche)
                .precioTotal(calcularPrecioFinal(precioNoche, fechaEntrada, fechaSalida))
                .estadoReserva(EstadoReserva.CONFIRMADA)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
}
