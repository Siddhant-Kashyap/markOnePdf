package com.markOne.api.Storage;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {
    private final Path rootLocation;

    public LocalStorageService(@Value("${storage.local.path:uploads}") String path){
        this.rootLocation = Paths.get(path).toAbsolutePath().normalize();
        try{
            Files.createDirectories(this.rootLocation);

        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory",e);
        }
    }
    @Override
    public String store(MultipartFile file) {
        String fileName = UUID.randomUUID()+"_"+file.getOriginalFilename();
        try{
            Files.copy(file.getInputStream(),this.rootLocation.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file",e);
        }
    }

    @Override
    public Resource load(String storagePath) {
        try{
            Path file = this.rootLocation.resolve(storagePath).normalize();
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists()){
                return resource;
            }
            throw  new RuntimeException("File Not found : "+ storagePath);
        }catch (Exception e){
            throw new RuntimeException("Failed to load file: "+storagePath,e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try{
            Files.deleteIfExists(this.rootLocation.resolve(storagePath));
        }catch (IOException e){
            throw new RuntimeException("Failed to delete file:"+ storagePath,e);
        }
    }
}
