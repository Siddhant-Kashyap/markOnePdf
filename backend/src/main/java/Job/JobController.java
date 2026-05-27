package Job;

import Queue.ConversionMessageProducer;
import com.markOne.api.Entity.ConversionJob;
import com.markOne.api.Enum.JobStatus;
import com.markOne.api.Repository.ConversionJobRepository;
import com.markOne.api.Storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private final ConversionMessageProducer conversionMessageProducer;
    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<ConversionJob> createJob(@RequestBody JobRequest request){
        ConversionJob conversionJob = jobService.createJob(request.getFileIds(),request.getConversionType());
        // send message to producer
        return ResponseEntity.ok(conversionJob);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversionJob> getJobStatus(@PathVariable String id){
        return ResponseEntity.ok(jobService.getJob(id));
    }

    public ResponseEntity<Resource> downloadResult(@PathVariable String id){
        ConversionJob job = jobService.getJob(id);
        if(job.getJobStatus()!= JobStatus.COMPLETED){
            return ResponseEntity.badRequest().build();
        }
        Resource file = storageService.load(job.getOutputFileId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"converted.pdf")
                .body(file);
    }


}
