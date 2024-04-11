package com.webapps.tss.batchpagmenet.repository;

import com.webapps.tss.batchpagmenet.entity.Transacao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface TransacaoRepository extends CrudRepository<Transacao, Long> {

    // select * from transacoes order by nome_loj asd, id desc
    List<Transacao> findAllByOrderByNomeLojaAscIdDesc();

}
