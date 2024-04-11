package com.webapps.tss.batchpagmenet;

import com.webapps.tss.batchpagmenet.entity.Transacao;
import com.webapps.tss.batchpagmenet.repository.TransacaoRepository;
import com.webapps.tss.batchpagmenet.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class TransacaoServiceTest {
    @InjectMocks
    private TransacaoService service;

    @Mock
    private TransacaoRepository transacaoRepository;



    @Test
    public void testListTotaisPorLojas() {
        // AAA

        // Arrange
        final String lojaA = "Loja A";
        final String lojaB = "Loja B";

        var transacao1 = new Transacao(1L, 1, new Date(System.currentTimeMillis()), BigDecimal.valueOf(100),
                12345678900L, "1234-5678-90123-4567", new Time(System.currentTimeMillis()),
                "Dono Loja A", lojaA);

        var transacao2 = new Transacao(2L, 1, new Date(System.currentTimeMillis()), BigDecimal.valueOf(50L),
                34565678900L, "1444-5644-22323-4544", new Time(System.currentTimeMillis()),
                "Dono Loja B", lojaB);

        var transacao3 = new Transacao(3L, 1, new Date(System.currentTimeMillis()), BigDecimal.valueOf(75),
                12345678900L, "1444-5448-90444-4447", new Time(System.currentTimeMillis()),
                "Dono Loja A", lojaA);

        when(transacaoRepository.findAllByOrderByNomeLojaAscIdDesc()).thenReturn(List.of(transacao1, transacao2, transacao3));

        // Act
        var reports = service.listarTotaisPorLoja();

        // Assert
        assertEquals(2, reports.size());
        reports.forEach( report -> {
            if(report.nomeDaLoja().equals(lojaA)) {
                assertEquals(2, report.transacoes().size());
                assertEquals(BigDecimal.valueOf(175), report.total());
                assertTrue(report.transacoes().contains(transacao1));
                assertTrue(report.transacoes().contains(transacao3));
            } else {
                assertEquals(1, report.transacoes().size());
                assertEquals(BigDecimal.valueOf(50), report.total());
                assertTrue(report.transacoes().contains(transacao2));
            }
        });
    }
}
