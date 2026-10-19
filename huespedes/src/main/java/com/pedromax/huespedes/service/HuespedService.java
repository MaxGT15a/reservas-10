package com.pedromax.huespedes.service;

import com.pedromax.commons.dto.habitaciones.huespedes.HuespedRequest;
import com.pedromax.commons.dto.habitaciones.huespedes.HuespedResponse;
import com.pedromax.commons.service.CrudService;

public interface HuespedService extends CrudService<HuespedRequest, HuespedResponse> {
    HuespedResponse obtenerHuespedPorIdSinEstado(Long id);
}
