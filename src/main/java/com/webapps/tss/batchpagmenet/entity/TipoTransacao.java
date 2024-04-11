package com.webapps.tss.batchpagmenet.entity;

import java.math.BigDecimal;

public enum TipoTransacao {
    DEBITO(1),
    BOLETO(2),
    FINANCIAMENTO(3),
    CREDITO(4),
    RECEBIMENTO_EMPRESTIMO(5),
    VENDAS(6),
    RECEBIMENTO_TED(7),
    RECEBIMENTO_DOC(8),
    ALUGUEL(9);

    private final int tipo;

    TipoTransacao(int tipo) {
        this.tipo = tipo;
    }

    public int getTipo() {
        return tipo;
    }

    public BigDecimal getSinal() {
        return switch (tipo) {
            case 1, 4, 5, 6, 7, 8 -> new BigDecimal(-1);
            case 2, 3, 9 -> new BigDecimal(1);
            default -> new BigDecimal(0);
        };
    }

    public static TipoTransacao findByTipo(int tipo) {
        var tipoTransacao = switch (tipo) {
            case 1 -> DEBITO;
            case 2 -> BOLETO;
            case 3 -> FINANCIAMENTO;
            case 4 -> CREDITO;
            case 5 -> RECEBIMENTO_EMPRESTIMO;
            case 6 -> VENDAS;
            case 7 -> RECEBIMENTO_TED;
            case 8 -> RECEBIMENTO_DOC;
            case 9 -> ALUGUEL;
            default -> null;
        };

        if (tipoTransacao == null) {
            throw new IllegalArgumentException("Tipo de transação inválido: " + tipo);
        }

        return tipoTransacao;
    }
}
