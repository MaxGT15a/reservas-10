package com.pedromax.commons.clients;

import com.pedromax.commons.dto.reservas.ReservaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "reservas")
public interface ReservaClient {

    @GetMapping("/huesped-reserva/{idHuesped}")
    ResponseEntity<Void> validarHuespedNoTieneReservasActivas(@PathVariable Long idHuesped);

    @GetMapping("/habitacion-reserva/{idHabitacion}")
    ReservaResponse obtenerReservaVigentePorHabitacion(@PathVariable Long idHabitacion);
}
