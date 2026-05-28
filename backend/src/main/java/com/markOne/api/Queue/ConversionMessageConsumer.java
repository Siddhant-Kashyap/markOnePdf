package com.markOne.api.Queue;

import com.markOne.api.Conversion.Converter;
import com.markOne.api.Entity.ConversionJob;
import com.markOne.api.Entity.FileMetaData;
import com.markOne.api.Enum.JobStatus;
import com.markOne.api.Job.JobService;
import com.markOne.api.Repository.FileMetaDataRepository;
import com.markOne.api.Storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConversionMessageConsumer {

    private final JobService jobService;
    private final List<Converter> converters;
    private final FileMetaDataRepository fileMetaDataRepository;

    @Value("${storage.local.path:uploads}")
    private String storagePath;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleMessage(String jobId) {
        log.info("Received conversion job: {}", jobId);
        try {
            ConversionJob job = jobService.getJob(jobId);
            jobService.updateStatus(jobId, JobStatus.PROCESSING, null);

            List<FileMetaData> inputFiles = fileMetaDataRepository.findAllById(job.getInputFilesIds());

            Converter converter = converters.stream()
                    .filter(c -> c.supports(job.getConversionType()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No converter found for: " + job.getConversionType()));

            byte[] output = converter.convert(inputFiles);

            String outputPath = UUID.randomUUID() + "_converted.pdf";
            Files.write(Paths.get(storagePath).toAbsolutePath().resolve(outputPath), output);

            FileMetaData outputMeta = new FileMetaData();
            outputMeta.setOriginalName("converted.pdf");
            outputMeta.setStoragePath(outputPath);
            outputMeta.setContentType("application/pdf");
            outputMeta.setSize(output.length);
            outputMeta.setCreatedAt(LocalDateTime.now());
            outputMeta.setExpiresAt(LocalDateTime.now().plusHours(24));
            fileMetaDataRepository.save(outputMeta);

            jobService.markComplete(jobId, outputPath);

        } catch (Exception e) {
            log.error("Conversion failed for job: {}", jobId, e);
            jobService.updateStatus(jobId, JobStatus.FAILED, e.getMessage());
        }
    }
}
