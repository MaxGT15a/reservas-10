package com.pedromax.habitaciones.entities;

import com.pedromax.commons.enums.EstadoHabitacion;
import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.enums.TipoHabitacion;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Pruebas Unitarias para la Entidad Habitación")
public class HabitacionTest {

    private static final Integer NUMERO_TEST = 101;
    private static final BigDecimal PRECIO_TEST = new BigDecimal("450.00");
    private static final Integer CAPACIDAD_TEST = 3;

    @Nested
    @DisplayName("Pruebas del método estático crear()")
    class CrearHabitacionTests {

        @Test
        @DisplayName("Debe crear una Habitación válida con estados iniciales correctos")
        void debeCrearHabitacionExitosamente() {
            Habitacion habitacion = Habitacion.crear(NUMERO_TEST, PRECIO_TEST, CAPACIDAD_TEST);

            assertThat(habitacion).isNotNull();
            assertThat(habitacion.getNumero()).isEqualTo(NUMERO_TEST);
            assertThat(habitacion.getPrecio()).isEqualByComparingTo(PRECIO_TEST);
            assertThat(habitacion.getCapacidad()).isEqualTo(CAPACIDAD_TEST);
            assertThat(habitacion.getEstadoHabitacion()).isEqualTo(EstadoHabitacion.DISPONIBLE);
            assertThat(habitacion.getEstadoRegistro()).isEqualTo(EstadoRegistro.ACTIVO);
        }

        @Test
        @DisplayName("Debe fallar al crear si el número de habitación es negativo o cero")
        void debeLanzarExcepcionCuandoNumeroEsInvalido() {
           assertThatThrownBy(() -> Habitacion.crear(-5, PRECIO_TEST, CAPACIDAD_TEST))
                    .hasMessageContaining("El número de la habitación es requerido y debe ser positivo");
        }

        @Test
        @DisplayName("Debe fallar al crear si el precio de la habitación es negativo")
        void debeLanzarExcepcionCuandoPrecioEsInvalido() {
            assertThatThrownBy(() -> Habitacion.crear(NUMERO_TEST, new BigDecimal("-10.00"), CAPACIDAD_TEST))
                    .hasMessageContaining("El precio de la habitación es requerido y debe ser positivo");
        }
    }

    @Nested
    @DisplayName("Pruebas del método actualizar()")
    class ActualizarHabitacionTests {

        @Test
        @DisplayName("Debe actualizar los datos básicos de una habitación activa")
        void debeActualizarHabitacionActiva() {
            Habitacion habitacion = Habitacion.crear(NUMERO_TEST, PRECIO_TEST, CAPACIDAD_TEST);
            habitacion.actualizar(202, new BigDecimal("600.00"), 5, TipoHabitacion.SUITE);

            assertThat(habitacion.getNumero()).isEqualTo(202);
            assertThat(habitacion.getPrecio()).isEqualByComparingTo("600.00");
            assertThat(habitacion.getCapacidad()).isEqualTo(5);
            assertThat(habitacion.getTipoHabitacion()).isEqualTo(TipoHabitacion.SUITE);
        }

        @Test
        @DisplayName("Debe lanzar excepción al intentar actualizar una habitación lógicamente ELIMINADA")
        void debeLanzarExcepcionAlActualizarHabitacionEliminada() {
            Habitacion habitacion = Habitacion.crear(NUMERO_TEST, PRECIO_TEST, CAPACIDAD_TEST);
            habitacion.eliminar();

            assertThatThrownBy(() -> habitacion.actualizar(202, PRECIO_TEST, CAPACIDAD_TEST, TipoHabitacion.SENCILLA))
                    .isInstanceOf(ConflictoException.class)
                    .hasMessage("Esta habitación ya ha sido eliminada");
        }
    }

    @Nested
    @DisplayName("Pruebas del método actualizarEstadoHabitacion()")
    class ActualizarEstadoTests {

        @Test
        @DisplayName("Debe lanzar una excepción si el nuevo estado recibido es nulo")
        void debeLanzarExcepcionSiEstadoEsNulo() {
            Habitacion habitacion = Habitacion.crear(NUMERO_TEST, PRECIO_TEST, CAPACIDAD_TEST);

            assertThatThrownBy(() -> habitacion.actualizarEstadoHabitacion(null))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessage("El nuevo estado de la habitación es requerido");
        }

        //== LA VALIDACIÓN PARA NO ACTUALIZAR A ESTADO OCUPADO MANUALMENTE VIENE EN EL SERVICE ==
    }

    @Nested
    @DisplayName("Pruebas del método eliminar()")
    class EliminarHabitacionTests {

        @Test
        @DisplayName("Debe marcar el estado del registro como ELIMINADO de manera correcta")
        void debeMarcarComoEliminadoLogicamente() {
             Habitacion habitacion = Habitacion.crear(NUMERO_TEST, PRECIO_TEST, CAPACIDAD_TEST);
            habitacion.eliminar();
            assertThat(habitacion.getEstadoRegistro()).isEqualTo(EstadoRegistro.ELIMINADO);
        }

        @Test
        @DisplayName("Debe lanzar excepción si se intenta borrar una habitación previamente eliminada")
        void debeLanzarExcepcionSiYaSeEncuentraEliminada() {
            Habitacion habitacion = Habitacion.crear(NUMERO_TEST, PRECIO_TEST, CAPACIDAD_TEST);
            habitacion.eliminar();

            assertThatThrownBy(habitacion::eliminar)
                    .isInstanceOf(ConflictoException.class)
                    .hasMessage("Esta habitación ya ha sido eliminada");
        }
    }
}
