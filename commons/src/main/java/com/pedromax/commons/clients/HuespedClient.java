package com.pedromax.commons.clients;

import com.pedromax.commons.dto.huespedes.HuespedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "huespedes")
public interface HuespedClient {

    @GetMapping("/{id}")
    HuespedResponse obtenerHuespedPorIdActivo(@PathVariable("id") Long id);

    @GetMapping("/id-huesped/{id}")
    HuespedResponse obtenerHuespedPorIdSinEstado(@PathVariable("id") Long id);
}
