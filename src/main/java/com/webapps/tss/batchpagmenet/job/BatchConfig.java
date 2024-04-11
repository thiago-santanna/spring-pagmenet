package com.webapps.tss.batchpagmenet.job;

import com.webapps.tss.batchpagmenet.domain.Transacao;
import com.webapps.tss.batchpagmenet.domain.TransacaoCNAB;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Configuration
public class BatchConfig {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final DataSource dataSource;

    public BatchConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository, DataSource dataSource) {
        this.transactionManager = transactionManager;
        this.jobRepository = jobRepository;
        this.dataSource = dataSource;
    }

    @Bean
    Job job(Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    Step step(ItemReader<TransacaoCNAB> reader,
              ItemProcessor<TransacaoCNAB, Transacao> processor,
              ItemWriter<Transacao> writer) {
        return new StepBuilder("step", jobRepository)
                .<TransacaoCNAB, Transacao>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();

    }

    @StepScope
    @Bean
    FlatFileItemReader<TransacaoCNAB> reader(@Value("#{jobParameters['cnabFile']}") Resource resource) {
        //FileSystemResource resource = new FileSystemResource("files/CNAB.txt");
        return new FlatFileItemReaderBuilder<TransacaoCNAB>()
                .name("reader")
                .resource(resource)
                .fixedLength()
                .columns(
                        new Range(1, 1),
                        new Range(2, 9),
                        new Range(10, 19),
                        new Range(20, 30),
                        new Range(31, 42),
                        new Range(43, 48),
                        new Range(49, 62),
                        new Range(63, 80))
                .names("tipo", "data", "valor", "cpf", "cartao", "hora", "donoDaLoja", "nomeDaLoja")
                .targetType(TransacaoCNAB.class)
                .build();
    }

    @Bean
    ItemProcessor<TransacaoCNAB, Transacao> processor() {
        return item ->
            new Transacao(
                    null,
                    item.tipo(),
                    null,
                    item.valor().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP),
                    item.cpf(),
                    item.cartao(),
                    null,
                    item.donoDaLoja(),
                    item.nomeDaLoja()
            )
                    .withData(item.data())
                    .withHora(item.hora());

    }

    @Bean
    JdbcBatchItemWriter<Transacao> writer() {
        return new JdbcBatchItemWriterBuilder<Transacao>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO transacao (tipo, data, valor, cpf, cartao, hora, dono_loja, nome_loja)
                        VALUES (:tipo, :data, :valor, :cpf, :cartao, :hora, :donoLoja, :nomeLoja)
                     """)
                .beanMapped()
                .build();
    }

    @Bean
    JobLauncher jobLauncherAsync(JobRepository repository) throws Exception {
        var jobLaucher = new TaskExecutorJobLauncher();
        jobLaucher.setJobRepository(repository);
        jobLaucher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLaucher.afterPropertiesSet();
        return jobLaucher;
    }

}
