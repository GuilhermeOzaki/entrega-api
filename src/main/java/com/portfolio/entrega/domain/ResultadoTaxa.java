package com.portfolio.entrega.domain;

import java.math.BigDecimal;
import java.util.List;

public record ResultadoTaxa(BigDecimal valor, boolean gratuita, List<ComponenteTaxa> componentes) {
}
