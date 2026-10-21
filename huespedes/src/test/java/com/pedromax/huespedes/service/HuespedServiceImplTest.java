package com.pedromax.huespedes.service;

import com.pedromax.commons.clients.ReservaClient;
import com.pedromax.commons.dto.huespedes.HuespedRequest;
import com.pedromax.commons.dto.huespedes.HuespedResponse;
import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.enums.TipoDocumento;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.RecursoNoEncontradoException;
import com.pedromax.huespedes.entity.Huesped;
import com.pedromax.huespedes.mapper.HuespedMapper;
import com.pedromax.huespedes.repository.HuespedRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para HuespedServiceImpl")
public class HuespedServiceImplTest {

    @Mock
    private HuespedRepository huespedRepository;

    @Mock
    private HuespedMapper huespedMapper;

    @Mock
    private ReservaClient reservaClient;

    @InjectMocks
    private HuespedServiceImpl huespedService;

    private static final String NOMBRE = "Nabucodonosor";
    private static final String APELLIDO = "Esternocleidomastoideo";
    private static final String EMAIL = "pedro.max@trnetwork.com";
    private static final String TELEFONO = "1234567890";
    private static final String DOCUMENTO = "87654321";
    private static final String NACIONALIDAD = "Mexicana";
    private static final String TIPO_DOC = "INE";

    private Huesped huespedActivo;
    private HuespedRequest huespedRequest;
    private HuespedResponse huespedResponse;

    @BeforeEach
    void setUp() {
        huespedActivo = Huesped.builder()
                .id(1L)
                .nombre(NOMBRE)
                .apellido(APELLIDO)
                .email(EMAIL)
                .telefono(TELEFONO)
                .tipoDocumento(TipoDocumento.INE)
                .numeroDocumento(DOCUMENTO)
                .nacionalidad(NACIONALIDAD)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();

        huespedRequest = new HuespedRequest(NOMBRE, APELLIDO, EMAIL, TELEFONO, TIPO_DOC, DOCUMENTO, NACIONALIDAD);
        huespedResponse = new HuespedResponse(1L, NOMBRE, APELLIDO, EMAIL, TELEFONO, TIPO_DOC, DOCUMENTO, NACIONALIDAD, "ACTIVO");
    }

    @Nested
    @DisplayName("Pruebas del método listar()")
    class ListarTests {
        @Test
        @DisplayName("Debe retornar una lista con los huéspedes activos")
        void debeListarHuespedesActivos() {
            when(huespedRepository.findAllByEstadoRegistro(EstadoRegistro.ACTIVO)).thenReturn(List.of(huespedActivo));
            when(huespedMapper.entidadAResponse(huespedActivo)).thenReturn(huespedResponse);

            List<HuespedResponse> resultado = huespedService.listar();

            assertThat(resultado).isNotEmpty().hasSize(1);
            assertThat(resultado.get(0).email()).isEqualTo(EMAIL);
            verify(huespedRepository).findAllByEstadoRegistro(EstadoRegistro.ACTIVO);
        }
    }

    @Nested
    @DisplayName("Pruebas del método registrar()")
    class RegistrarTests {
        @Test
        @DisplayName("Debe registrar un huésped exitosamente cuando los datos son únicos")
        void debeRegistrarHuespedExitosamente() {
            when(huespedRepository.existsByEmailIgnoreCaseAndEstadoRegistro(EMAIL, EstadoRegistro.ACTIVO)).thenReturn(false);
            when(huespedRepository.existsByTelefonoAndEstadoRegistro(TELEFONO, EstadoRegistro.ACTIVO)).thenReturn(false);
            when(huespedMapper.requestAEntidad(huespedRequest)).thenReturn(huespedActivo);
            when(huespedMapper.entidadAResponse(huespedActivo)).thenReturn(huespedResponse);

           HuespedResponse resultado = huespedService.registrar(huespedRequest);

            assertThat(resultado).isNotNull();
            assertThat(resultado.nombre()).isEqualTo(NOMBRE);
            verify(huespedRepository).saveAndFlush(huespedActivo);
        }

        @Test
        @DisplayName("Debe lanzar ConflictoException si el correo ya existe en un registro activo")
        void debeLanzarExcepcionSiEmailDuplicado() {
            when(huespedRepository.existsByEmailIgnoreCaseAndEstadoRegistro(EMAIL, EstadoRegistro.ACTIVO)).thenReturn(true);

            assertThatThrownBy(() -> huespedService.registrar(huespedRequest))
                    .isInstanceOf(ConflictoException.class)
                    .hasMessageContaining("Ya existe un huésped activo con el mismo correo electrónico");

            verify(huespedRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Pruebas del método actualizar()")
    class ActualizarTests {
        @Test
        @DisplayName("Debe actualizar un huésped activo si no tiene reservas ni datos duplicados")
        void debeActualizarHuespedExitosamente() {
            when(huespedRepository.findByIdAndEstadoRegistro(1L, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(huespedActivo));
            when(reservaClient.validarHuespedNoTieneReservasActivas(1L)).thenReturn(ResponseEntity.noContent().build());
            when(huespedRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(EMAIL, EstadoRegistro.ACTIVO, 1L)).thenReturn(false);
            when(huespedRepository.existsByTelefonoAndEstadoRegistroAndIdNot(TELEFONO, EstadoRegistro.ACTIVO, 1L)).thenReturn(false);
            when(huespedMapper.entidadAResponse(any(Huesped.class))).thenReturn(huespedResponse);

            HuespedResponse resultado = huespedService.actualizar(huespedRequest, 1L);

            assertThat(resultado).isNotNull();
            verify(huespedRepository).saveAndFlush(any(Huesped.class));
        }

        @Test
        @DisplayName("Debe lanzar ConflictoException si el huésped a actualizar cuenta con reservas activas (Feign)")
        void debeLanzarExcepcionAlActualizarConReservasActivas() {
            when(huespedRepository.findByIdAndEstadoRegistro(1L, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(huespedActivo));
            doThrow(FeignException.Conflict.class).when(reservaClient).validarHuespedNoTieneReservasActivas(1L);

            assertThatThrownBy(() -> huespedService.actualizar(huespedRequest, 1L))
                    .isInstanceOf(ConflictoException.class)
                    .hasMessageContaining("tiene reservas activas y no puede ser eliminado o actualizado");

            verify(huespedRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Pruebas del método eliminar()")
    class EliminarTests {
        @Test
        @DisplayName("Debe marcar el estado del registro como ELIMINADO de forma lógica")
        void debeEliminarHuespedLogicamente() {
            when(huespedRepository.findByIdAndEstadoRegistro(1L, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(huespedActivo));
            when(reservaClient.validarHuespedNoTieneReservasActivas(1L)).thenReturn(ResponseEntity.noContent().build());

            huespedService.eliminar(1L);

            assertThat(huespedActivo.getEstadoRegistro()).isEqualTo(EstadoRegistro.ELIMINADO);
            verify(huespedRepository).saveAndFlush(huespedActivo);
        }

        @Test
        @DisplayName("Debe lanzar RecursoNoEncontradoException si el huésped no existe o ya está borrado")
        void debeLanzarExcepcionSiHuespedNoExiste() {
           when(huespedRepository.findByIdAndEstadoRegistro(1L, EstadoRegistro.ACTIVO)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> huespedService.eliminar(1L))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("Huésped no encontrado con id: 1");

            verify(huespedRepository, never()).saveAndFlush(any());
        }
    }
}
