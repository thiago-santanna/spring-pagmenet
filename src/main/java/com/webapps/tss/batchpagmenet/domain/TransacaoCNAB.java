package com.webapps.tss.batchpagmenet.domain;

import java.math.BigDecimal;

public record TransacaoCNAB(
        Integer tipo,
        String data,
        BigDecimal valor,
        Long cpf,
        String cartao,
        String hora,
        String donoDaLoja,
        String nomeDaLoja
) {
}
