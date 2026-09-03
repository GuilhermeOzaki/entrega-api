package com.portfolio.entrega.domain.service;

import com.portfolio.entrega.domain.Coordenada;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculadoraDistancia {

    private static final double RAIO_DA_TERRA_KM = 6371.0;
    private static final double FATOR_MALHA_URBANA = 1.3;

    public double emLinhaReta(Coordenada origem, Coordenada destino) {
        return arredondar(haversineKm(origem, destino));
    }

    public double estimadaNaVia(Coordenada origem, Coordenada destino) {
        return arredondar(haversineKm(origem, destino) * FATOR_MALHA_URBANA);
    }

    private double haversineKm(Coordenada origem, Coordenada destino) {
        double latitudeOrigemRad = Math.toRadians(origem.latitude());
        double latitudeDestinoRad = Math.toRadians(destino.latitude());
        double deltaLatitude = Math.toRadians(destino.latitude() - origem.latitude());
        double deltaLongitude = Math.toRadians(destino.longitude() - origem.longitude());

        double senoLatitude = Math.sin(deltaLatitude / 2);
        double senoLongitude = Math.sin(deltaLongitude / 2);

        double a = senoLatitude * senoLatitude
                + Math.cos(latitudeOrigemRad) * Math.cos(latitudeDestinoRad) * senoLongitude * senoLongitude;

        return 2 * RAIO_DA_TERRA_KM * Math.asin(Math.min(1.0, Math.sqrt(a)));
    }

    private double arredondar(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
