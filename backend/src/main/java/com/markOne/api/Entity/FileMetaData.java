package com.markOne.api.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "file_metadata")
public class FileMetaData {
    @Id
    private String id;
    private String originalName;
    private String storagePath;
    private String contentType;
    private long size;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

}
