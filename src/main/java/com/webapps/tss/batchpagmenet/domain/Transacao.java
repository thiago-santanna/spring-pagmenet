package com.webapps.tss.batchpagmenet.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public record Transacao(
        Long id,
        Integer tipo,
        Date data,
        BigDecimal valor,
        Long cpf,
        String cartao,
        Time hora,
        String donoLoja,
        String nomeLoja
) {

    public Transacao withValor(BigDecimal valor) {
        return new Transacao(id, tipo, data, valor, cpf, cartao, hora, donoLoja, nomeLoja);
    }

    public Transacao withData(String data) throws ParseException {
        var dateFormat = new SimpleDateFormat("yyyyMMdd");
        var date = dateFormat.parse(data);

        return new Transacao(id, tipo, new Date(date.getTime()), valor, cpf, cartao, hora, donoLoja, nomeLoja);
    }

    public Transacao withHora(String hora) throws ParseException {
        var timeFormat = new SimpleDateFormat("HHmmss");
        var time = timeFormat.parse(hora);

        return new Transacao(id, tipo, data, valor, cpf, cartao, new Time(time.getTime()), donoLoja, nomeLoja);
    }
}
