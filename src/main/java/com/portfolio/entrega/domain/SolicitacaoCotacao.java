package com.portfolio.entrega.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitacaoCotacao(
        Coordenada origem,
        Coordenada destino,
        BigDecimal valorPedido,
        int tempoPreparoMinutos,
        LocalDateTime momento,
        boolean chovendo
) {

    public FaixaDeHorario faixaDeHorario() {
        return FaixaDeHorario.de(momento.toLocalTime());
    }
}
