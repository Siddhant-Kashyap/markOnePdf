package Queue;

import Job.JobService;
import com.markOne.api.Enum.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConversionMessageConsumer {
    private  final JobService jobService;

    public void handleMesage(String jobId){
        log.info("Received Conversion job : {}",jobId);
        try {
            jobService.updateStatus(jobId, JobStatus.PROCESSING,null);
            //TODO : dispatch to correct convertor
        }catch (Exception e){
            log.error("Conversion failed  for jobId : {} ",jobId);
        }
    }
}
