package com.portfolio.entrega.domain;

import java.time.LocalTime;

public enum FaixaDeHorario {

    PICO_ALMOCO(LocalTime.of(11, 30), LocalTime.of(14, 0)),
    PICO_JANTAR(LocalTime.of(18, 30), LocalTime.of(21, 30)),
    FORA_DE_PICO(null, null);

    private final LocalTime inicio;
    private final LocalTime fim;

    FaixaDeHorario(LocalTime inicio, LocalTime fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    public static FaixaDeHorario de(LocalTime horario) {
        for (FaixaDeHorario faixa : values()) {
            if (faixa.contem(horario)) {
                return faixa;
            }
        }
        return FORA_DE_PICO;
    }

    public boolean ehPico() {
        return this != FORA_DE_PICO;
    }

    private boolean contem(LocalTime horario) {
        return inicio != null && !horario.isBefore(inicio) && horario.isBefore(fim);
    }
}
