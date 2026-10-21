package com.pedromax.huespedes.entity;

import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.exceptions.ConflictoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Pruebas Unitarias para la Entidad Huésped")
public class HuespedTest {

    private static final String NOMBRE_TEST = "Nabucodonosor";
    private static final String APELLIDO_TEST = "Esternocleidomastoideo";
    private static final String EMAIL_TEST = "pedro.max@trnetwork.com";
    private static final String TELEFONO_TEST = "1234567890";
    private static final String DOC_TEST = "87654321";
    private static final String NACIONALIDAD_TEST = "Mexicana";
    private static final String TIPO_DOC_DESC = "INE";

    @Nested
    @DisplayName("Pruebas del método estático crear()")
    class CrearHuespedTests {

        @Test
        @DisplayName("Debe crear un Huésped válido con datos limpios y formateados")
        void debeCrearHuespedExitosamente() {

            Huesped huesped = Huesped.crear(
                    "  " + NOMBRE_TEST + "  ",
                    "  " + APELLIDO_TEST + "  ",
                    "  PEDRO.MAX@TRNETWORK.COM  ",
                    "  " + TELEFONO_TEST + "  ",
                    DOC_TEST,
                    "  " + NACIONALIDAD_TEST + "  ",
                    TIPO_DOC_DESC
            );

            assertThat(huesped).isNotNull();
            assertThat(huesped.getNombre()).isEqualTo(NOMBRE_TEST);
            assertThat(huesped.getApellido()).isEqualTo(APELLIDO_TEST);
            assertThat(huesped.getEmail()).isEqualTo(EMAIL_TEST);
            assertThat(huesped.getTelefono()).isEqualTo(TELEFONO_TEST);
            assertThat(huesped.getNumeroDocumento()).isEqualTo(DOC_TEST);
            assertThat(huesped.getNacionalidad()).isEqualTo(NACIONALIDAD_TEST);
            assertThat(huesped.getEstadoRegistro()).isEqualTo(EstadoRegistro.ACTIVO);
        }

        @Test
        @DisplayName("Debe fallar al crear si algún campo viola las reglas de StringCustomUtils")
        void debeLanzarExcepcionCuandoDatosSonInvalidos() {
            assertThatThrownBy(() -> Huesped.crear(
                    "N",
                    APELLIDO_TEST,
                    EMAIL_TEST,
                    TELEFONO_TEST,
                    DOC_TEST,
                    NACIONALIDAD_TEST,
                    TIPO_DOC_DESC
            )).hasMessageContaining("El nombre es requerido y debe de tener entre 2 a 50 caracteres");

            assertThatThrownBy(() -> Huesped.crear(
                    NOMBRE_TEST,
                    APELLIDO_TEST,
                    "",
                    TELEFONO_TEST,
                    DOC_TEST,
                    NACIONALIDAD_TEST,
                    TIPO_DOC_DESC
            )).hasMessageContaining("El email es requerido");
        }
    }

    @Nested
    @DisplayName("Pruebas del método actualizar()")
    class ActualizarHuespedTests {

        @Test
        @DisplayName("Debe actualizar todos los campos correctamente en un huésped activo")
        void debeActualizarHuespedActivo() {
            Huesped huesped = Huesped.crear(NOMBRE_TEST, APELLIDO_TEST, EMAIL_TEST, TELEFONO_TEST, DOC_TEST, NACIONALIDAD_TEST, TIPO_DOC_DESC);

            huesped.actualizar(
                    "  " + NOMBRE_TEST + " Segundo  ",
                    "  " + APELLIDO_TEST + " Modificado  ",
                    "  NUEVO." + EMAIL_TEST + "  ",
                    "5544332211",
                    "99999999",
                    "  Extranjera  ",
                    "PASAPORTE"
            );

            assertThat(huesped.getNombre()).isEqualTo(NOMBRE_TEST + " Segundo");
            assertThat(huesped.getApellido()).isEqualTo(APELLIDO_TEST + " Modificado");
            assertThat(huesped.getEmail()).isEqualTo("nuevo." + EMAIL_TEST);
            assertThat(huesped.getTelefono()).isEqualTo("5544332211");
            assertThat(huesped.getNumeroDocumento()).isEqualTo("99999999");
            assertThat(huesped.getNacionalidad()).isEqualTo("Extranjera");
        }

        @Test
        @DisplayName("Debe lanzar excepción al intentar actualizar un huésped ELIMINADO")
        void debeLanzarExcepcionAlActualizarHuespedEliminado() {
            Huesped huesped = Huesped.crear(NOMBRE_TEST, APELLIDO_TEST, EMAIL_TEST, TELEFONO_TEST, DOC_TEST, NACIONALIDAD_TEST, TIPO_DOC_DESC);
            huesped.eliminar();

            assertThatThrownBy(() -> huesped.actualizar(
                    "Otro", APELLIDO_TEST, EMAIL_TEST, TELEFONO_TEST, DOC_TEST, NACIONALIDAD_TEST, TIPO_DOC_DESC
            )).isInstanceOf(ConflictoException.class)
                    .hasMessage("Este huésped ya ha sido eliminado");
        }
    }

    @Nested
    @DisplayName("Pruebas del método eliminar()")
    class EliminarHuespedTests {

        @Test
        @DisplayName("Debe cambiar el estado del registro a ELIMINADO lógicamente")
        void debeCambiarEstadoAEliminadoExitosamente() {
            Huesped huesped = Huesped.crear(NOMBRE_TEST, APELLIDO_TEST, EMAIL_TEST, TELEFONO_TEST, DOC_TEST, NACIONALIDAD_TEST, TIPO_DOC_DESC);
            huesped.eliminar();
            assertThat(huesped.getEstadoRegistro()).isEqualTo(EstadoRegistro.ELIMINADO);
        }

        @Test
        @DisplayName("Debe lanzar una excepción si se intenta volver a eliminar un huésped ya eliminado")
        void debeLanzarExcepcionSiYaEstaEliminado() {
            Huesped huesped = Huesped.crear(NOMBRE_TEST, APELLIDO_TEST, EMAIL_TEST, TELEFONO_TEST, DOC_TEST, NACIONALIDAD_TEST, TIPO_DOC_DESC);
            huesped.eliminar();

            assertThatThrownBy(huesped::eliminar)
                    .isInstanceOf(ConflictoException.class)
                    .hasMessage("Este huésped ya ha sido eliminado");
        }
    }
}
