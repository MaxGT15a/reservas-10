package com.pedromax.huespedes.entity;

import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.enums.TipoDocumento;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
@Table(name = "HUESPEDES")
public class Huesped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HUESPED")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO", nullable = false, length = 50)
    private String apellido;

    @Column(name = "EMAIL", nullable = false, length = 150)
    private String email;

    @Column(name = "TELEFONO", nullable = false, length = 10)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_DOCUMENTO", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @Column(name = "NUMERO_DOCUMENTO", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "NACIONALIDAD", nullable = false, length = 100)
    private String nacionalidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false, length = 20)
    private EstadoRegistro estadoRegistro;

    private static void validarDatos(String nombre, String apellido,
                              String email, String telefono, String numeroDocumento,
                              String nacionalidad){
        StringCustomUtils.validarTamanio(nombre, 2, 50,
                "El nombre es requerido y debe de tener entre 2 a 50 caracteres");

        StringCustomUtils.validarTamanio(apellido, 2, 50,
                "El apellido es requerido y debe de tener entre 2 a 50 caracteres");

        StringCustomUtils.validarNoVacio(email,"El email es requerido");

        StringCustomUtils.validarTextoNumerico(
                telefono, "El teléfono es requerido y debe contener solo números");

        StringCustomUtils.validarNoVacio(numeroDocumento,"El número de documento es requerido");

        StringCustomUtils.validarTamanio(nacionalidad, 2, 100,
                "La nacionalidad es requerida y debe de tener entre 2 a 100 caracteres");
    }

    private void validarNoEliminado() {
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictoException("Este huésped ya ha sido eliminado");
    }

    public void eliminar() {
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public static Huesped crear(String nombre, String apellido,
                                String email, String telefono, String numeroDocumento,
                                String nacionalidad, String tipoDocumento){

        validarDatos(nombre, apellido, email, telefono, numeroDocumento, nacionalidad);

        return Huesped.builder()
                .nombre(nombre.trim())
                .apellido(apellido.trim())
                .email(email.trim().toLowerCase())
                .telefono(telefono.trim())
                .numeroDocumento(numeroDocumento)
                .nacionalidad(nacionalidad.trim())
                .tipoDocumento(TipoDocumento.obtenerTipoDocumentoPorDescripcion(tipoDocumento))
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    public void actualizar(String nombre, String apellido,
                                String email, String telefono, String numeroDocumento,
                                String nacionalidad, String tipoDocumento) {

        validarDatos(nombre, apellido, email, telefono, numeroDocumento, nacionalidad);

        validarNoEliminado();

        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
        this.email = email.trim().toLowerCase();
        this.telefono = telefono.trim();
        this.numeroDocumento = numeroDocumento;
        this.nacionalidad = nacionalidad.trim();
        this.tipoDocumento = TipoDocumento.obtenerTipoDocumentoPorDescripcion(tipoDocumento);
    }

}
