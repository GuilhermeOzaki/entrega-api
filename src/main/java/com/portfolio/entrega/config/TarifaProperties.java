package com.portfolio.entrega.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "entrega.tarifa")
public record TarifaProperties(
        BigDecimal taxaBase,
        BigDecimal valorPorKm,
        double raioIncluidoKm,
        double distanciaMaximaKm,
        BigDecimal adicionalLongaDistancia,
        double limiarLongaDistanciaKm,
        BigDecimal adicionalPedidoPequeno,
        BigDecimal limiarPedidoPequeno,
        BigDecimal limiarEntregaGratuita,
        BigDecimal taxaMaxima,
        BigDecimal multiplicadorPico,
        BigDecimal multiplicadorChuva
) {
}
