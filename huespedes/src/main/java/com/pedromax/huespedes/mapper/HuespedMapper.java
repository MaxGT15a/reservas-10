package com.pedromax.huespedes.mapper;

import com.pedromax.commons.dto.huespedes.HuespedRequest;
import com.pedromax.commons.dto.huespedes.HuespedResponse;
import com.pedromax.commons.mapper.CommonMapper;
import com.pedromax.huespedes.entity.Huesped;
import org.springframework.stereotype.Component;

@Component
public class HuespedMapper implements CommonMapper<HuespedRequest, HuespedResponse, Huesped> {
    @Override
    public Huesped requestAEntidad(HuespedRequest request) {
        return request == null ? null : Huesped.crear(
                request.nombre(),
                request.apellido(),
                request.email(),
                request.telefono(),
                request.numeroDocumento(),
                request.nacionalidad(),
                request.tipoDocumento()
        );
    }

    @Override
    public HuespedResponse entidadAResponse(Huesped entidad) {
        return entidad == null ? null : new HuespedResponse(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getApellido(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getTipoDocumento().getDescripcion(),
                entidad.getNacionalidad(),
                entidad.getNumeroDocumento(),
                entidad.getEstadoRegistro().getDescripcion()
        );
    }
}
