package com.portfolio.entrega.domain.service;

import com.portfolio.entrega.config.TarifaProperties;
import com.portfolio.entrega.domain.ComponenteTaxa;
import com.portfolio.entrega.domain.Coordenada;
import com.portfolio.entrega.domain.ResultadoTaxa;
import com.portfolio.entrega.domain.SolicitacaoCotacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraTaxaTest {

    private static final Coordenada ORIGEM = new Coordenada(-23.5610, -46.6560);
    private static final Coordenada DESTINO = new Coordenada(-23.5730, -46.6390);
    private static final LocalDateTime FORA_DE_PICO = LocalDateTime.of(2026, 3, 10, 15, 0);
    private static final LocalDateTime PICO_JANTAR = LocalDateTime.of(2026, 3, 10, 19, 0);

    private CalculadoraTaxa calculadora;

    @BeforeEach
    void preparar() {
        TarifaProperties tarifa = new TarifaProperties(
                new BigDecimal("3.99"),
                new BigDecimal("1.20"),
                1.0,
                15.0,
                new BigDecimal("2.50"),
                8.0,
                new BigDecimal("3.00"),
                new BigDecimal("20.00"),
                new BigDecimal("60.00"),
                new BigDecimal("24.90"),
                new BigDecimal("1.30"),
                new BigDecimal("1.20")
        );
        calculadora = new CalculadoraTaxa(tarifa);
    }

    @Test
    @DisplayName("cobra taxa base mais distancia em cenario comum")
    void cobraTaxaBaseMaisDistancia() {
        ResultadoTaxa resultado = calculadora.calcular(solicitacao(new BigDecimal("40.00"), FORA_DE_PICO, false), 5.0);

        assertThat(resultado.valor()).isEqualByComparingTo("8.79");
        assertThat(resultado.gratuita()).isFalse();
        assertThat(resultado.componentes()).hasSize(2);
    }

    @Test
    @DisplayName("nao cobra o primeiro quilometro incluso na taxa base")
    void naoCobraOPrimeiroQuilometro() {
        ResultadoTaxa resultado = calculadora.calcular(solicitacao(new BigDecimal("40.00"), FORA_DE_PICO, false), 1.0);

        assertThat(resultado.valor()).isEqualByComparingTo("3.99");
        assertThat(resultado.componentes()).hasSize(1);
    }

    @Test
    @DisplayName("isenta a taxa quando o pedido atinge o valor minimo de frete gratis")
    void isentaTaxaAcimaDoLimiar() {
        ResultadoTaxa resultado = calculadora.calcular(solicitacao(new BigDecimal("60.00"), FORA_DE_PICO, false), 7.0);

        assertThat(resultado.valor()).isEqualByComparingTo("0.00");
        assertThat(resultado.gratuita()).isTrue();
    }

    @Test
    @DisplayName("aplica adicional de pedido pequeno abaixo do limiar")
    void aplicaAdicionalDePedidoPequeno() {
        ResultadoTaxa resultado = calculadora.calcular(solicitacao(new BigDecimal("19.99"), FORA_DE_PICO, false), 2.0);

        assertThat(descricoes(resultado)).contains("Adicional de pedido pequeno");
        assertThat(resultado.valor()).isEqualByComparingTo("8.19");
    }

    @Test
    @DisplayName("respeita o teto de taxa em cenario de pico com chuva e longa distancia")
    void respeitaOTetoDeTaxa() {
        ResultadoTaxa resultado = calculadora.calcular(solicitacao(new BigDecimal("15.00"), PICO_JANTAR, true), 10.0);

        assertThat(resultado.valor()).isEqualByComparingTo("24.90");
        assertThat(descricoes(resultado)).contains("Adicional de alta demanda", "Teto de taxa aplicado");
    }

    @Test
    @DisplayName("horario de pico deixa a taxa mais cara que o mesmo pedido fora de pico")
    void picoEncareceATaxa() {
        BigDecimal forapico = calculadora.calcular(solicitacao(new BigDecimal("30.00"), FORA_DE_PICO, false), 4.0).valor();
        BigDecimal pico = calculadora.calcular(solicitacao(new BigDecimal("30.00"), PICO_JANTAR, false), 4.0).valor();

        assertThat(pico).isGreaterThan(forapico);
    }

    @Test
    @DisplayName("a soma dos componentes sempre bate com o valor final cobrado")
    void somaDosComponentesBateComOTotal() {
        ResultadoTaxa resultado = calculadora.calcular(solicitacao(new BigDecimal("15.00"), PICO_JANTAR, true), 12.0);

        BigDecimal soma = resultado.componentes().stream()
                .map(ComponenteTaxa::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(soma).isEqualByComparingTo(resultado.valor());
    }

    private SolicitacaoCotacao solicitacao(BigDecimal valorPedido, LocalDateTime momento, boolean chovendo) {
        return new SolicitacaoCotacao(ORIGEM, DESTINO, valorPedido, 20, momento, chovendo);
    }

    private java.util.List<String> descricoes(ResultadoTaxa resultado) {
        return resultado.componentes().stream().map(ComponenteTaxa::descricao).toList();
    }
}
