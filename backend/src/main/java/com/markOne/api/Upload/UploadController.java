package com.markOne.api.Upload;

import com.markOne.api.Entity.FileMetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;
    @PostMapping
    public ResponseEntity<List<FileMetaData>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files){
        List<FileMetaData> result = new ArrayList<>();
        for(MultipartFile file:files){
            result.add(uploadService.uploadFile(file));

        }
        return ResponseEntity.ok(result);
    }

}
