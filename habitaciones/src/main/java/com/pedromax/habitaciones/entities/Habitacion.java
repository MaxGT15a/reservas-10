package com.pedromax.habitaciones.entities;

import com.pedromax.commons.enums.EstadoHabitacion;
import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.enums.TipoHabitacion;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.DatoInvalidoException;
import com.pedromax.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "HABITACIONES")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HABITACION")
    private Long id;

    @Column(name = "NUMERO", nullable = false)
    private Integer numero;

    @Column(name = "PRECIO", nullable = false)
    private BigDecimal precio;

    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_HABITACION", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_HABITACION", nullable = false)
    private EstadoHabitacion estadoHabitacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    private static void validarDatos(Integer numero, BigDecimal precio, Integer capacidad){
        ValoresNumericosUtils.validarNumeroPositivo(numero, "El número de la habitación es requerido y debe ser positivo");
        ValoresNumericosUtils.validarBigDecimalPositivo(precio, "El precio de la habitación es requerido y debe ser positivo");
        ValoresNumericosUtils.validarNumeroPositivo(capacidad, "La capacidad de huéspedes es requerida y debe ser positiva");
    }

    private void validarNoEliminada() {
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictoException("Esta habitación ya ha sido eliminada");
    }

    public void actualizarTipoHabitacion(TipoHabitacion tipoHabitacion) {
        validarNoEliminada();
        if (tipoHabitacion == null)
            throw new DatoInvalidoException("El tipo de habitación es requerido");
        this.tipoHabitacion = tipoHabitacion;
    }

    public void actualizarEstadoHabitacion(EstadoHabitacion nuevoEstado) {
        if (nuevoEstado == null)
            throw new DatoInvalidoException("El nuevo estado de la habitación es requerido");

        if (!estadoHabitacion.puedeCambiarA(nuevoEstado))
            throw new ConflictoException("La habitación con estado " + estadoHabitacion + " solo puede cambiar a " + estadoHabitacion.puedeCambiar());

        this.estadoHabitacion = nuevoEstado;
    }

    public void eliminar() {
        validarNoEliminada();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizar(Integer numero, BigDecimal precio, Integer capacidad, TipoHabitacion tipoHabitacion){
        validarNoEliminada();
        validarDatos(numero, precio, capacidad);
        actualizarTipoHabitacion(tipoHabitacion);

        this.numero = numero;
        this.precio = precio;
        this.capacidad = capacidad;
    }

    public static Habitacion crear(Integer numero, BigDecimal precio, Integer capacidad){
        validarDatos(numero, precio, capacidad);

        return Habitacion.builder()
                .numero(numero)
                .precio(precio)
                .capacidad(capacidad)
                .estadoHabitacion(EstadoHabitacion.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();

    }

}
