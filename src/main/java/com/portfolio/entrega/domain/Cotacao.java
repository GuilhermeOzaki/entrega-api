package com.portfolio.entrega.domain;

import java.math.BigDecimal;
import java.util.List;

public record Cotacao(
        double distanciaKm,
        BigDecimal valorTaxa,
        boolean entregaGratuita,
        int tempoMinimoMinutos,
        int tempoMaximoMinutos,
        List<ComponenteTaxa> componentes
) {
}
