package Job;

import com.markOne.api.Entity.ConversionJob;
import com.markOne.api.Enum.ConversionType;
import com.markOne.api.Enum.JobStatus;
import com.markOne.api.Repository.ConversionJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    private ConversionJobRepository conversionJobRepository;

    public ConversionJob createJob(List<String> inputFiles, ConversionType conversionType){
        ConversionJob job = new ConversionJob();

        job.setJobStatus(JobStatus.PENDING);
        job.setConversionType(conversionType);
        job.setInputFilesIds(inputFiles);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        return conversionJobRepository.save(job);
    }

    public ConversionJob getJob(String id){
        return conversionJobRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Job not found: " + id));
    }
    public  ConversionJob updateStatus(String id,JobStatus status,String errorMessage){
        ConversionJob job = getJob(id);
        job.setJobStatus(status);
        job.setUpdatedAt(LocalDateTime.now());
        if(errorMessage != null){
            job.setErrorMessage(errorMessage);
        }
        return  conversionJobRepository.save(job);
    }
     public ConversionJob markComplete(String id,String outputFileId){
        ConversionJob job = getJob(id);
        job.setJobStatus(JobStatus.COMPLETED);
        job.setOutputFileId(outputFileId);
        job.setUpdatedAt(LocalDateTime.now());
        return conversionJobRepository.save(job);
     }

}
