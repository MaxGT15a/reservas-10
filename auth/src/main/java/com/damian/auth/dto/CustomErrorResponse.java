package com.damian.auth.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) { }
