package com.pedromax.habitaciones.services;

import com.pedromax.commons.clients.ReservaClient;
import com.pedromax.commons.dto.habitaciones.HabitacionRequest;
import com.pedromax.commons.dto.habitaciones.HabitacionResponse;
import com.pedromax.commons.dto.reservas.ReservaResponse;
import com.pedromax.commons.enums.EstadoHabitacion;
import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.enums.TipoHabitacion;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.DatoInvalidoException;
import com.pedromax.habitaciones.entities.Habitacion;
import com.pedromax.habitaciones.mapper.HabitacionMapper;
import com.pedromax.habitaciones.repositories.HabitacionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para HabitacionServiceImpl")
public class HabitacionServiceImplTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private HabitacionMapper habitacionMapper;

    @Mock
    private ReservaClient reservaClient;

    @Mock
    private HttpServletRequest servletRequest;

    @InjectMocks
    private HabitacionServiceImpl habitacionService;

    private static final Long ID_TEST = 1L;
    private static final Integer NUMERO_TEST = 101;
    private static final BigDecimal PRECIO_TEST = new BigDecimal("450.00");
    private static final Integer CAPACIDAD_TEST = 3;

    private Habitacion habitacionDisponible;
    private Habitacion habitacionOcupada;
    private HabitacionRequest habitacionRequest;
    private HabitacionResponse habitacionResponse;

    @BeforeEach
    void setUp() {
        habitacionDisponible = Habitacion.builder()
                .id(ID_TEST)
                .numero(NUMERO_TEST)
                .precio(PRECIO_TEST)
                .capacidad(CAPACIDAD_TEST)
                .tipoHabitacion(TipoHabitacion.SENCILLA)
                .estadoHabitacion(EstadoHabitacion.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();

        habitacionOcupada = Habitacion.builder()
                .id(ID_TEST)
                .numero(NUMERO_TEST)
                .precio(PRECIO_TEST)
                .capacidad(CAPACIDAD_TEST)
                .tipoHabitacion(TipoHabitacion.SENCILLA)
                .estadoHabitacion(EstadoHabitacion.OCUPADA)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();

        habitacionRequest = new HabitacionRequest(NUMERO_TEST, PRECIO_TEST, CAPACIDAD_TEST, "Sencilla");
        habitacionResponse = new HabitacionResponse(ID_TEST, NUMERO_TEST, PRECIO_TEST, CAPACIDAD_TEST, "Sencilla", "DISPONIBLE");
    }

    @Nested
    @DisplayName("Pruebas del método listar()")
    class ListarTests {
        @Test
        @DisplayName("Debe retornar una lista de todas las habitaciones activas")
        void debeListarHabitacionesActivas() {
            when(habitacionRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO)).thenReturn(List.of(habitacionDisponible));
            when(habitacionMapper.entidadAResponse(habitacionDisponible)).thenReturn(habitacionResponse);

            List<HabitacionResponse> resultado = habitacionService.listar();

            assertThat(resultado).isNotEmpty().hasSize(1);
            verify(habitacionRepository).findByEstadoRegistro(EstadoRegistro.ACTIVO);
        }
    }

    @Nested
    @DisplayName("Pruebas del método registrar()")
    class RegistrarTests {
        @Test
        @DisplayName("Debe registrar una habitación exitosamente cuando el número es único")
        void debeRegistrarHabitacionExitosamente() {
            when(habitacionRepository.existsByNumeroAndEstadoRegistro(NUMERO_TEST, EstadoRegistro.ACTIVO)).thenReturn(false);
            when(habitacionMapper.requestAEntidad(eq(habitacionRequest), any())).thenReturn(habitacionDisponible);
            when(habitacionMapper.entidadAResponse(habitacionDisponible)).thenReturn(habitacionResponse);

            HabitacionResponse resultado = habitacionService.registrar(habitacionRequest);

            assertThat(resultado).isNotNull();
            verify(habitacionRepository).saveAndFlush(any(Habitacion.class));
        }

        @Test
        @DisplayName("Debe lanzar DatoInvalidoException si el número de habitación ya existe en registros activos")
        void debeLanzarExcepcionSiNumeroDuplicado() {
            when(habitacionRepository.existsByNumeroAndEstadoRegistro(NUMERO_TEST, EstadoRegistro.ACTIVO)).thenReturn(true);

            assertThatThrownBy(() -> habitacionService.registrar(habitacionRequest))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("Ya existe una habitación activa registrada con el número");

            verify(habitacionRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Pruebas del método actualizar()")
    class ActualizarTests {
        @Test
        @DisplayName("Debe lanzar ConflictoException si está OCUPADA y la nueva capacidad es menor a las personas de la reserva vigente")
        void debeLanzarExcepcionSiCapacidadMenorAReserva() {
            when(habitacionRepository.findByIdAndEstadoRegistro(ID_TEST, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(habitacionOcupada));

            ReservaResponse mockReserva = mock(ReservaResponse.class);
            when(mockReserva.numPersonas()).thenReturn(4);
            when(reservaClient.obtenerReservaVigentePorHabitacion(ID_TEST)).thenReturn(mockReserva);

            assertThatThrownBy(() -> habitacionService.actualizar(habitacionRequest, ID_TEST))
                    .isInstanceOf(ConflictoException.class)
                    .hasMessageContaining("La capacidad no puede ser menor que el número de personas de la reserva vigente");

            verify(habitacionRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("Debe actualizar correctamente si el número es único y cumple las reglas")
        void debeActualizarHabitacionCorrectamente() {
            when(habitacionRepository.findByIdAndEstadoRegistro(ID_TEST, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(habitacionDisponible));
            when(habitacionRepository.existsByNumeroAndEstadoRegistroAndIdNot(NUMERO_TEST, EstadoRegistro.ACTIVO, ID_TEST)).thenReturn(false);
            when(habitacionMapper.entidadAResponse(any())).thenReturn(habitacionResponse);

            HabitacionResponse resultado = habitacionService.actualizar(habitacionRequest, ID_TEST);

            assertThat(resultado).isNotNull();
            verify(habitacionRepository).saveAndFlush(habitacionDisponible);
        }
    }

    @Nested
    @DisplayName("Pruebas del método actualizarEstadoHabitacion()")
    class ActualizarEstadoTests {
        @Test
        @DisplayName("Debe lanzar ConflictoException si un usuario manual intenta cambiar una habitación OCUPADA")
        void debeFallarCambioManualDesdeOcupada() {
            when(habitacionRepository.findByIdAndEstadoRegistro(ID_TEST, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(habitacionOcupada));
            when(servletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (Windows NT 10.0)"); // Petición Web/Manual

            assertThatThrownBy(() -> habitacionService.actualizarEstadoHabitacion(ID_TEST, 3L))
                    .isInstanceOf(ConflictoException.class)
                    .hasMessageContaining("No se puede cambiar manualmente el estado de una habitación ocupada a otro");
        }

        @Test
        @DisplayName("Debe permitir cambiar el estado si la petición proviene de un Microservicio (Sistema)")
        void debePermitirCambioSiEsSistema() {
            Habitacion habitacionSpy = spy(habitacionDisponible);
            doNothing().when(habitacionSpy).actualizarEstadoHabitacion(any());

            when(habitacionRepository.findByIdAndEstadoRegistro(ID_TEST, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(habitacionSpy));
            when(servletRequest.getHeader("User-Agent")).thenReturn("Java/17.0.1 (Feign)"); // Simula llamada de sistema

            habitacionService.actualizarEstadoHabitacion(ID_TEST, 2L);
            verify(habitacionRepository).saveAndFlush(habitacionSpy);
        }
    }

    @Nested
    @DisplayName("Pruebas del método eliminar()")
    class EliminarTests {
        @Test
        @DisplayName("Debe lanzar ConflictoException si la habitación se encuentra OCUPADA")
        void debeFallarAlEliminarHabitacionOcupada() {
            when(habitacionRepository.findByIdAndEstadoRegistro(ID_TEST, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(habitacionOcupada));

            assertThatThrownBy(() -> habitacionService.eliminar(ID_TEST))
                    .isInstanceOf(ConflictoException.class)
                    .hasMessageContaining("No se puede eliminar una habitación que se encuentra actualmente ocupada");
            verify(habitacionRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("Debe marcar el registro como ELIMINADO de forma lógica si está DISPONIBLE")
        void debeEliminarLogicamente() {
            when(habitacionRepository.findByIdAndEstadoRegistro(ID_TEST, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(habitacionDisponible));
            habitacionService.eliminar(ID_TEST);

            assertThat(habitacionDisponible.getEstadoRegistro()).isEqualTo(EstadoRegistro.ELIMINADO);
            verify(habitacionRepository).saveAndFlush(habitacionDisponible);
        }
    }
}
