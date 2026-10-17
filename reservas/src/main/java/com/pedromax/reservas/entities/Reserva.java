package com.pedromax.reservas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RESERVAS")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESERVA")
    private Long id;
}
