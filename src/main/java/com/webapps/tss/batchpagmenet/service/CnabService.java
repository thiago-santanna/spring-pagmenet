package com.webapps.tss.batchpagmenet.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class CnabService {

    private final Path fileStorageLocation;
    private JobLauncher jobLauncher;
    private Job job;

    public CnabService(@Value("${file.upload.dir}") String fileStorageLocation,
                       @Qualifier("jobLauncherAsync") JobLauncher jobLauncher,
                       Job job) {
        this.fileStorageLocation = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    public void uploadCnabFile(MultipartFile file) throws IOException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path targetLocation = fileStorageLocation.resolve(fileName);
        file.transferTo(targetLocation);

        var jobParameters = new JobParametersBuilder()
                .addJobParameter("cnab", file.getOriginalFilename(), String.class, true)
                        .addJobParameter("cnabFile", "file:" + targetLocation.toString(), String.class)
                                .toJobParameters();

        jobLauncher.run(job, jobParameters);
    }
}
