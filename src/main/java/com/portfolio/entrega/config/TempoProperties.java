package com.portfolio.entrega.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "entrega.tempo")
public record TempoProperties(
        double velocidadeMediaKmH,
        double velocidadePicoKmH,
        int minutosColeta,
        int minutosEntrega,
        int minutosAdicionaisChuva,
        double margemJanela
) {
}
