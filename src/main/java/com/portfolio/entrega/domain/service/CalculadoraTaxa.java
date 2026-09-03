package com.portfolio.entrega.domain.service;

import com.portfolio.entrega.config.TarifaProperties;
import com.portfolio.entrega.domain.ComponenteTaxa;
import com.portfolio.entrega.domain.ResultadoTaxa;
import com.portfolio.entrega.domain.SolicitacaoCotacao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class CalculadoraTaxa {

    private static final int CASAS_DECIMAIS = 2;

    private final TarifaProperties tarifa;

    public CalculadoraTaxa(TarifaProperties tarifa) {
        this.tarifa = tarifa;
    }

    public ResultadoTaxa calcular(SolicitacaoCotacao solicitacao, double distanciaKm) {
        if (temDireitoAEntregaGratuita(solicitacao)) {
            ComponenteTaxa isencao = new ComponenteTaxa("Entrega gratis pelo valor do pedido", BigDecimal.ZERO.setScale(CASAS_DECIMAIS));
            return new ResultadoTaxa(BigDecimal.ZERO.setScale(CASAS_DECIMAIS), true, List.of(isencao));
        }

        List<ComponenteTaxa> componentes = new ArrayList<>();
        componentes.add(new ComponenteTaxa("Taxa base", normalizar(tarifa.taxaBase())));
        adicionarSeRelevante(componentes, "Distancia percorrida", valorPorDistancia(distanciaKm));
        adicionarSeRelevante(componentes, "Adicional de longa distancia", valorPorLongaDistancia(distanciaKm));
        adicionarSeRelevante(componentes, "Adicional de pedido pequeno", valorPorPedidoPequeno(solicitacao));

        BigDecimal subtotal = somar(componentes);
        adicionarSeRelevante(componentes, "Adicional de alta demanda", valorPorDemanda(solicitacao, subtotal));

        BigDecimal total = somar(componentes);
        if (total.compareTo(tarifa.taxaMaxima()) > 0) {
            componentes.add(new ComponenteTaxa("Teto de taxa aplicado", normalizar(tarifa.taxaMaxima().subtract(total))));
            total = normalizar(tarifa.taxaMaxima());
        }

        return new ResultadoTaxa(total, false, List.copyOf(componentes));
    }

    private boolean temDireitoAEntregaGratuita(SolicitacaoCotacao solicitacao) {
        return solicitacao.valorPedido().compareTo(tarifa.limiarEntregaGratuita()) >= 0;
    }

    private BigDecimal valorPorDistancia(double distanciaKm) {
        double distanciaCobravel = Math.max(0.0, distanciaKm - tarifa.raioIncluidoKm());
        return normalizar(tarifa.valorPorKm().multiply(BigDecimal.valueOf(distanciaCobravel)));
    }

    private BigDecimal valorPorLongaDistancia(double distanciaKm) {
        return distanciaKm > tarifa.limiarLongaDistanciaKm()
                ? normalizar(tarifa.adicionalLongaDistancia())
                : BigDecimal.ZERO;
    }

    private BigDecimal valorPorPedidoPequeno(SolicitacaoCotacao solicitacao) {
        return solicitacao.valorPedido().compareTo(tarifa.limiarPedidoPequeno()) < 0
                ? normalizar(tarifa.adicionalPedidoPequeno())
                : BigDecimal.ZERO;
    }

    private BigDecimal valorPorDemanda(SolicitacaoCotacao solicitacao, BigDecimal subtotal) {
        BigDecimal multiplicador = BigDecimal.ONE;
        if (solicitacao.faixaDeHorario().ehPico()) {
            multiplicador = multiplicador.multiply(tarifa.multiplicadorPico());
        }
        if (solicitacao.chovendo()) {
            multiplicador = multiplicador.multiply(tarifa.multiplicadorChuva());
        }
        return normalizar(subtotal.multiply(multiplicador.subtract(BigDecimal.ONE)));
    }

    private void adicionarSeRelevante(List<ComponenteTaxa> componentes, String descricao, BigDecimal valor) {
        if (valor.signum() > 0) {
            componentes.add(new ComponenteTaxa(descricao, normalizar(valor)));
        }
    }

    private BigDecimal somar(List<ComponenteTaxa> componentes) {
        return componentes.stream()
                .map(ComponenteTaxa::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CASAS_DECIMAIS, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizar(BigDecimal valor) {
        return valor.setScale(CASAS_DECIMAIS, RoundingMode.HALF_UP);
    }
}
