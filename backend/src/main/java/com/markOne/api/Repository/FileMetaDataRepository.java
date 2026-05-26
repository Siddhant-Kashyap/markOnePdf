package com.markOne.api.Repository;

import com.markOne.api.Entity.FileMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FileMetaDataRepository extends MongoRepository<FileMetaData,String> {
    List<FileMetaData> findByExpiresAtBefore(LocalDateTime dateTime);
}
