package com.portfolio.entrega.domain.service;

import com.portfolio.entrega.config.TempoProperties;
import com.portfolio.entrega.domain.JanelaDeEntrega;
import com.portfolio.entrega.domain.SolicitacaoCotacao;
import org.springframework.stereotype.Component;

@Component
public class CalculadoraTempo {

    private final TempoProperties tempo;

    public CalculadoraTempo(TempoProperties tempo) {
        this.tempo = tempo;
    }

    public JanelaDeEntrega calcular(SolicitacaoCotacao solicitacao, double distanciaKm) {
        double minutosEstimados = solicitacao.tempoPreparoMinutos()
                + minutosDeDeslocamento(solicitacao, distanciaKm)
                + tempo.minutosColeta()
                + tempo.minutosEntrega()
                + (solicitacao.chovendo() ? tempo.minutosAdicionaisChuva() : 0);

        int estimativaCentral = (int) Math.round(minutosEstimados);
        int margem = (int) Math.round(estimativaCentral * tempo.margemJanela());

        return new JanelaDeEntrega(Math.max(1, estimativaCentral - margem), estimativaCentral + margem);
    }

    private double minutosDeDeslocamento(SolicitacaoCotacao solicitacao, double distanciaKm) {
        double velocidade = solicitacao.faixaDeHorario().ehPico()
                ? tempo.velocidadePicoKmH()
                : tempo.velocidadeMediaKmH();
        return (distanciaKm / velocidade) * 60;
    }
}
