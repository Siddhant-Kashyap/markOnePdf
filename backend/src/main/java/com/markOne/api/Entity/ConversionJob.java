package com.markOne.api.Entity;

import com.markOne.api.Enum.ConversionType;
import com.markOne.api.Enum.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "conversion_jobs")
public class ConversionJob {
    private String id;
    private JobStatus jobStatus;
    private ConversionType conversionType;
    private List<String> inputFilesIds;
    private String outputFileId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String errorMessage;


}
