package com.pedromax.reservas.services;

import com.pedromax.commons.clients.HabitacionClient;
import com.pedromax.commons.clients.HuespedClient;
import com.pedromax.commons.dto.habitaciones.HabitacionResponse;
import com.pedromax.commons.dto.huespedes.HuespedResponse;
import com.pedromax.commons.dto.reservas.ReservaRequest;
import com.pedromax.commons.dto.reservas.ReservaResponse;
import com.pedromax.commons.enums.EstadoHabitacion;
import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.RecursoNoEncontradoException;
import com.pedromax.reservas.entities.Reserva;
import com.pedromax.reservas.enums.EstadoReserva;
import com.pedromax.reservas.mapper.ReservaMapper;
import com.pedromax.reservas.repositories.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para ReservaServiceImpl")
public class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ReservaMapper reservaMapper;

    @Mock
    private HuespedClient huespedClient;

    @Mock
    private HabitacionClient habitacionClient;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private static final Long ID_RESERVA = 100L;
    private static final Long ID_HUESPED = 1L;
    private static final Long ID_HABITACION = 5L;
    private static final Integer NUM_PERSONAS = 2;
    private static final BigDecimal PRECIO_HABITACION = new BigDecimal("500.00");

    private Reserva reservaConfirmada;
    private ReservaRequest reservaRequest;
    private ReservaResponse reservaResponse;

    @BeforeEach
    void setUp() {
        reservaConfirmada = Reserva.builder()
                .id(ID_RESERVA)
                .idHuesped(ID_HUESPED)
                .idHabitacion(ID_HABITACION)
                .fechaEntrada(LocalDate.of(2026, 11, 1))
                .fechaSalida(LocalDate.of(2026, 11, 6))
                .numPersonas(NUM_PERSONAS)
                .precioNoche(PRECIO_HABITACION)
                .precioTotal(new BigDecimal("2500.00"))
                .estadoReserva(EstadoReserva.CONFIRMADA)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();

        reservaRequest = new ReservaRequest(ID_HUESPED, ID_HABITACION,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 6), NUM_PERSONAS);
        reservaResponse = mock(ReservaResponse.class);
    }

    @Nested
    @DisplayName("Pruebas del método registrar()")
    class RegistrarTests {

        @Test
        @DisplayName("Debe registrar una reserva y cambiar de forma automática el estado de la habitación a OCUPADA")
        void debeRegistrarReservaExitosamente() {
            HuespedResponse mockHuesped = mock(HuespedResponse.class);
            when(mockHuesped.id()).thenReturn(ID_HUESPED);
            when(huespedClient.obtenerHuespedPorIdActivo(ID_HUESPED)).thenReturn(mockHuesped);

            when(reservaRepository.existsByIdHuespedAndEstadoRegistroAndEstadoReservaIn(
                    eq(ID_HUESPED), eq(EstadoRegistro.ACTIVO), any())).thenReturn(false);

            HabitacionResponse mockHabitacion = mock(HabitacionResponse.class);
            when(mockHabitacion.id()).thenReturn(ID_HABITACION);
            when(mockHabitacion.estadoHabitacion()).thenReturn(EstadoHabitacion.DISPONIBLE.getDescripcion());
            when(mockHabitacion.capacidad()).thenReturn(4);
            when(mockHabitacion.precio()).thenReturn(PRECIO_HABITACION);
            when(habitacionClient.obtenerHabitacionPorIdActiva(ID_HABITACION)).thenReturn(mockHabitacion);
            when(reservaMapper.requestAEntidad(reservaRequest, PRECIO_HABITACION)).thenReturn(reservaConfirmada);
            when(reservaMapper.entidadAResponse(reservaConfirmada, mockHuesped, mockHabitacion)).thenReturn(reservaResponse);

            ReservaResponse resultado = reservaService.registrar(reservaRequest);

            assertThat(resultado).isNotNull();
            verify(reservaRepository).saveAndFlush(reservaConfirmada);
            verify(habitacionClient).actualizarEstadoHabitacion(ID_HABITACION, 2L);
        }

        @Test
        @DisplayName("Debe lanzar ConflictoException si el número de personas supera la capacidad máxima de la habitación")
        void debeFallarSiPersonasExcedenCapacidad() {
            HuespedResponse mockHuesped = mock(HuespedResponse.class);
            when(mockHuesped.id()).thenReturn(ID_HUESPED);
            when(huespedClient.obtenerHuespedPorIdActivo(ID_HUESPED)).thenReturn(mockHuesped);

            HabitacionResponse mockHabitacion = mock(HabitacionResponse.class);
            when(mockHabitacion.estadoHabitacion()).thenReturn(EstadoHabitacion.DISPONIBLE.getDescripcion());
            when(mockHabitacion.capacidad()).thenReturn(1);
            when(habitacionClient.obtenerHabitacionPorIdActiva(ID_HABITACION)).thenReturn(mockHabitacion);

            assertThatThrownBy(() -> reservaService.registrar(reservaRequest))
                    .isInstanceOf(ConflictoException.class)
                    .hasMessageContaining("El numero de personas de la reserva no pude ser mayor a la capacidad de la habitación");

            verify(reservaRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Pruebas del método actualizarEstadoReserva()")
    class ActualizarEstadoTests {

        @Test
        @DisplayName("Debe cambiar el estado de la habitación a LIMPIEZA cuando el nuevo estado de la reserva es FINALIZADA")
        void debeCambiarALimpiezaAlFinalizarReserva() {
            Reserva reservaSpy = spy(reservaConfirmada);
            doNothing().when(reservaSpy).actualizarEstadoReserva(any());
            when(reservaSpy.getEstadoReserva()).thenReturn(EstadoReserva.FINALIZADA);
            when(reservaRepository.findByIdAndEstadoRegistro(ID_RESERVA, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(reservaSpy));

            reservaService.actualizarEstadoReserva(ID_RESERVA, 3);

            verify(reservaRepository).saveAndFlush(reservaSpy);
            verify(habitacionClient).actualizarEstadoHabitacion(ID_HABITACION, 3L);
        }
    }

    @Nested
    @DisplayName("Pruebas del método obtenerNumeroPersonasReserva()")
    class ConsumoInternoTests {

        @Test
        @DisplayName("Debe retornar los datos mapeados si encuentra una reserva vigente para la habitación")
        void debeRetornarReservaVigente() {
            when(reservaRepository.findFirstByIdHabitacionAndEstadoRegistroAndEstadoReservaIn(
                    eq(ID_HABITACION), eq(EstadoRegistro.ACTIVO), any())).thenReturn(Optional.of(reservaConfirmada));
            when(reservaMapper.entidadAResponse(reservaConfirmada)).thenReturn(reservaResponse);

            ReservaResponse resultado = reservaService.obtenerNumeroPersonasReserva(ID_HABITACION);

            assertThat(resultado).isNotNull();
            verify(reservaRepository).findFirstByIdHabitacionAndEstadoRegistroAndEstadoReservaIn(eq(ID_HABITACION), eq(EstadoRegistro.ACTIVO), any());
        }

        @Test
        @DisplayName("Debe lanzar RecursoNoEncontradoException si la habitación no tiene reservas vigentes")
        void debeLanzar404SiNoHayReservaVigente() {
            when(reservaRepository.findFirstByIdHabitacionAndEstadoRegistroAndEstadoReservaIn(
                    eq(ID_HABITACION), eq(EstadoRegistro.ACTIVO), any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservaService.obtenerNumeroPersonasReserva(ID_HABITACION))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("No se encontró ninguna reserva vigente para la habitación");
        }
    }
}
