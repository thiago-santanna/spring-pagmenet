package com.webapps.tss.batchpagmenet.web;

import com.webapps.tss.batchpagmenet.entity.TransacaoReport;
import com.webapps.tss.batchpagmenet.service.TransacaoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    List<TransacaoReport> listAll() {
        return this.transacaoService.listarTotaisPorLoja();
    }
}
