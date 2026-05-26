package com.markOne.api.Repository;

import com.markOne.api.Entity.ConversionJob;
import com.markOne.api.Enum.JobStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConversionJobRepository extends MongoRepository<ConversionJob,String> {
    List<ConversionJob> findByJobStatus(JobStatus jobStatus);
}
