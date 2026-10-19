package com.pedromax.commons.clients;

import com.pedromax.commons.dto.habitaciones.HabitacionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "habitaciones")
public interface HabitacionClient {

    @GetMapping("/{id}")
    HabitacionResponse obtenerHabitacionPorIdActiva(@PathVariable("id") Long id);

    @GetMapping("/id-habitacion/{id}")
    HabitacionResponse obtenerHabitacionPorIdSinEstado(@PathVariable("id") Long id);

    @PatchMapping("/{id}/estado/{idEstado}")
    void actualizarEstadoHabitacion(@PathVariable("id") Long id, @PathVariable("idEstado") Long idEstado);
}
