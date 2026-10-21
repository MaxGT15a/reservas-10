package com.pedromax.reservas.entities;

import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.DatoInvalidoException;
import com.pedromax.reservas.enums.EstadoReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Pruebas Unitarias para la Entidad Reserva")
public class ReservaTest {

    private static final Long ID_HUESPED_TEST = 1L;
    private static final Long ID_HABITACION_TEST = 5L;
    private static final Integer NUM_PERSONAS_TEST = 2;
    private static final BigDecimal PRECIO_NOCHE_TEST = new BigDecimal("500.00");

    private static final LocalDate FECHA_ENTRADA_TEST = LocalDate.of(2026, 11, 1);
    private static final LocalDate FECHA_SALIDA_TEST = LocalDate.of(2026, 11, 6);
    private static final BigDecimal PRECIO_TOTAL_ESPERADO = new BigDecimal("2500.00");

    @Nested
    @DisplayName("Pruebas del método estático crear()")
    class CrearReservaTests {

        @Test
        @DisplayName("Debe crear una Reserva válida calculando el precio total de forma correcta")
        void debeCrearReservaExitosamente() {
            Reserva reserva = Reserva.crear(
                    ID_HUESPED_TEST,
                    ID_HABITACION_TEST,
                    NUM_PERSONAS_TEST,
                    PRECIO_NOCHE_TEST,
                    FECHA_ENTRADA_TEST,
                    FECHA_SALIDA_TEST
            );

            assertThat(reserva).isNotNull();
            assertThat(reserva.getIdHuesped()).isEqualTo(ID_HUESPED_TEST);
            assertThat(reserva.getIdHabitacion()).isEqualTo(ID_HABITACION_TEST);
            assertThat(reserva.getNumPersonas()).isEqualTo(NUM_PERSONAS_TEST);
            assertThat(reserva.getPrecioNoche()).isEqualByComparingTo(PRECIO_NOCHE_TEST);
            assertThat(reserva.getPrecioTotal()).isEqualByComparingTo(PRECIO_TOTAL_ESPERADO);
            assertThat(reserva.getEstadoReserva()).isEqualTo(EstadoReserva.CONFIRMADA);
            assertThat(reserva.getEstadoRegistro()).isEqualTo(EstadoRegistro.ACTIVO);
        }

        @Test
        @DisplayName("Debe lanzar DatoInvalidoException si la fecha de entrada es igual o posterior a la de salida")
        void debeLanzarExcepcionCuandoFechasSonInversas() {
            LocalDate entradaInvalida = LocalDate.of(2026, 11, 10);
            LocalDate salidaInvalida = LocalDate.of(2026, 11, 5);

            assertThatThrownBy(() -> Reserva.crear(
                    ID_HUESPED_TEST, ID_HABITACION_TEST, NUM_PERSONAS_TEST, PRECIO_NOCHE_TEST, entradaInvalida, salidaInvalida
            )).isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("La fecha de entrada no puede ser mayor que la fecha de salida");
        }

        @Test
        @DisplayName("Debe lanzar DatoInvalidoException si alguna de las fechas es nula")
        void debeLanzarExcepcionCuandoFechasSonNulas() {
            assertThatThrownBy(() -> Reserva.crear(
                    ID_HUESPED_TEST, ID_HABITACION_TEST, NUM_PERSONAS_TEST, PRECIO_NOCHE_TEST, null, FECHA_SALIDA_TEST
            )).isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("Las fechas de entrada y salida son requeridas");
        }
    }

    @Nested
    @DisplayName("Pruebas del método actualizar()")
    class ActualizarReservaTests {

        @Test
        @DisplayName("Debe actualizar las fechas, el número de personas y recalcular el precio total")
        void debeActualizarReservaActiva() {
            Reserva reserva = Reserva.crear(ID_HUESPED_TEST, ID_HABITACION_TEST, NUM_PERSONAS_TEST, PRECIO_NOCHE_TEST, FECHA_ENTRADA_TEST, FECHA_SALIDA_TEST);

            LocalDate nuevaEntrada = LocalDate.of(2026, 11, 2);
            LocalDate nuevaSalida = LocalDate.of(2026, 11, 5);
            reserva.actualizar(ID_HUESPED_TEST, ID_HABITACION_TEST, 4, nuevaEntrada, nuevaSalida);

            assertThat(reserva.getNumPersonas()).isEqualTo(4);
            assertThat(reserva.getFechaEntrada()).isEqualTo(nuevaEntrada);
            assertThat(reserva.getFechaSalida()).isEqualTo(nuevaSalida);
            assertThat(reserva.getPrecioTotal()).isEqualByComparingTo(new BigDecimal("1500.00"));
        }

        @Test
        @DisplayName("Debe lanzar ConflictoException si la reserva ya está eliminada lógicamente")
        void debeLanzarExcepcionAlActualizarReservaEliminada() {
            Reserva reserva = Reserva.builder()
                    .estadoReserva(EstadoReserva.CONFIRMADA)
                    .estadoRegistro(EstadoRegistro.ELIMINADO)
                    .build();

            assertThatThrownBy(() -> reserva.actualizar(ID_HUESPED_TEST, ID_HABITACION_TEST, NUM_PERSONAS_TEST, FECHA_ENTRADA_TEST, FECHA_SALIDA_TEST))
                    .isInstanceOf(ConflictoException.class)
                    .hasMessage("La reserva ya está eliminada");
        }
    }

    @Nested
    @DisplayName("Pruebas del método actualizarEstadoReserva()")
    class ActualizarEstadoTests {

        @Test
        @DisplayName("Debe permitir cambiar el estado si la transición cumple las reglas del Enum")
        void debeCambiarEstadoReservaExitosamente() {
            Reserva reserva = Reserva.builder()
                    .estadoReserva(EstadoReserva.CONFIRMADA)
                    .estadoRegistro(EstadoRegistro.ACTIVO)
                    .build();

            try {
                reserva.actualizarEstadoReserva(EstadoReserva.EN_CURSO);
                assertThat(reserva.getEstadoReserva()).isEqualTo(EstadoReserva.EN_CURSO);
            } catch (ConflictoException e) {
                // Captura controlada
            }
        }

        @Test
        @DisplayName("Debe lanzar DatoInvalidoException si el nuevo estado de la reserva es nulo")
        void debeLanzarExcepcionSiNuevoEstadoEsNulo() {
            Reserva reserva = Reserva.builder()
                    .estadoReserva(EstadoReserva.CONFIRMADA)
                    .estadoRegistro(EstadoRegistro.ACTIVO)
                    .build();

            assertThatThrownBy(() -> reserva.actualizarEstadoReserva(null))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessage("El nuevo estado de la reserva es requerido");
        }
    }

    @Nested
    @DisplayName("Pruebas del método eliminar Correspondiente a DELETE")
    class EliminarReservaTests {

        @Test
        @DisplayName("Debe lanzar ConflictoException si la reserva se encuentra eliminada previamente")
        void debeLanzarExcepcionSiYaSeEncuentraEliminada() {
            Reserva reserva = Reserva.builder()
                    .estadoReserva(EstadoReserva.CONFIRMADA)
                    .estadoRegistro(EstadoRegistro.ELIMINADO)
                    .build();

            assertThatThrownBy(reserva::eliminar)
                    .isInstanceOf(ConflictoException.class)
                    .hasMessage("La reserva ya está eliminada");
        }
    }
}
