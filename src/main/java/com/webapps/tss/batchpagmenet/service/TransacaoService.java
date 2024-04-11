package com.webapps.tss.batchpagmenet.service;

import com.webapps.tss.batchpagmenet.entity.TransacaoReport;
import com.webapps.tss.batchpagmenet.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public List<TransacaoReport> listarTotaisPorLoja() {
        var transacoes = transacaoRepository.findAllByOrderByNomeLojaAscIdDesc();
        var reportMap = new LinkedHashMap<String, TransacaoReport>(); //linked preserva a ordem

        transacoes.forEach(transacao -> {
            String nomeLoja = transacao.nomeLoja();
            BigDecimal valor = transacao.valor();

            reportMap.compute(nomeLoja, (key, existingReport) -> {
                var report = (existingReport != null) ? existingReport :
                        new TransacaoReport(nomeLoja, BigDecimal.ZERO, new ArrayList<>());

                return report.addTotal(valor).addTransacao(transacao.withValor(valor));
            });
        });

        return new ArrayList<>(reportMap.values());
    }
}
