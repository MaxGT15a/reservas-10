package com.pedromax.huespedes.service;

import com.pedromax.commons.dto.habitaciones.huespedes.HuespedRequest;
import com.pedromax.commons.dto.habitaciones.huespedes.HuespedResponse;

import java.util.List;

public class HuespedServiceImpl implements HuespedService{
    @Override
    public HuespedResponse obtenerHuespedPorIdSinEstado(Long id) {
        return null;
    }

    @Override
    public List<HuespedResponse> listar() {
        return List.of();
    }

    @Override
    public HuespedResponse obtenerPorId(Long id) {
        return null;
    }

    @Override
    public HuespedResponse registrar(HuespedRequest request) {
        return null;
    }

    @Override
    public HuespedResponse actualizar(HuespedRequest request, Long id) {
        return null;
    }

    @Override
    public void eliminar(Long id) {

    }
}
