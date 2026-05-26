package com.markOne.api.Upload;

import com.markOne.api.Entity.FileMetaData;
import com.markOne.api.Repository.FileMetaDataRepository;
import com.markOne.api.Storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final StorageService storageService;
    private final FileMetaDataRepository fileMetaDataRepository;

    private static final long MAX_FILE_SIZE= 10*1024*1024;
    private static final List<String> ALLOWED_TYPES= List.of(
            "image/png", "image/jpeg", "image/webp",
            "text/markdown", "text/html",
            "application/pdf"
    );

    public FileMetaData uploadFile(MultipartFile file){
        if(file.isEmpty()){
            throw new IllegalArgumentException("File is Empty");

        }
        if(file.getSize() >MAX_FILE_SIZE){
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("File type not allowed: " + file.getContentType());
        }
        String storagePath = storageService.store(file);

        FileMetaData metadata = new FileMetaData();
        metadata.setOriginalName(file.getOriginalFilename());
        metadata.setStoragePath(storagePath);
        metadata.setContentType(file.getContentType());
        metadata.setSize(file.getSize());
        metadata.setCreatedAt(LocalDateTime.now());
        metadata.setExpiresAt(LocalDateTime.now().plusHours(24));

        return fileMetaDataRepository.save(metadata);

    }
}
