package com.portfolio.entrega.domain.service;

import com.portfolio.entrega.domain.Coordenada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraDistanciaTest {

    private final CalculadoraDistancia calculadora = new CalculadoraDistancia();

    @Test
    @DisplayName("retorna zero quando origem e destino sao o mesmo ponto")
    void retornaZeroParaPontosIguais() {
        Coordenada ponto = new Coordenada(-23.5610, -46.6560);

        assertThat(calculadora.emLinhaReta(ponto, ponto)).isZero();
    }

    @Test
    @DisplayName("calcula um grau de longitude no equador com precisao de referencia")
    void calculaGrauDeLongitudeNoEquador() {
        double distancia = calculadora.emLinhaReta(new Coordenada(0.0, 0.0), new Coordenada(0.0, 1.0));

        assertThat(distancia).isCloseTo(111.19, org.assertj.core.data.Offset.offset(0.05));
    }

    @Test
    @DisplayName("distancia na via e maior que a linha reta por causa da malha urbana")
    void distanciaNaViaEhMaiorQueLinhaReta() {
        Coordenada origem = new Coordenada(-23.5610, -46.6560);
        Coordenada destino = new Coordenada(-23.5730, -46.6390);

        assertThat(calculadora.estimadaNaVia(origem, destino))
                .isGreaterThan(calculadora.emLinhaReta(origem, destino));
    }

    @Test
    @DisplayName("distancia cresce conforme o destino se afasta da origem")
    void distanciaCresceComOAfastamento() {
        Coordenada origem = new Coordenada(-23.5610, -46.6560);
        double perto = calculadora.emLinhaReta(origem, new Coordenada(-23.5650, -46.6560));
        double longe = calculadora.emLinhaReta(origem, new Coordenada(-23.6100, -46.6560));

        assertThat(longe).isGreaterThan(perto);
    }
}
