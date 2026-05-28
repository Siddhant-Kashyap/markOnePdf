package com.markOne.api.Conversion;

import com.markOne.api.Entity.FileMetaData;
import com.markOne.api.Enum.ConversionType;
import com.markOne.api.Storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageToPdfConverter implements Converter {
    private final StorageService storageService;

    @Override
    public byte[] convert(List<FileMetaData> inputs) {
        try (PDDocument document = new PDDocument()) {
            for(FileMetaData file:inputs){
                Resource resource =storageService.load(file.getStoragePath());
                byte[] imageBytes = resource.getInputStream().readAllBytes();
                PDImageXObject image = PDImageXObject.createFromByteArray(document,imageBytes, file.getOriginalName());
                PDRectangle pageSize = new PDRectangle(image.getWidth(),image.getHeight());
                PDPage page = new PDPage(pageSize);
                document.addPage(page);
                try(PDPageContentStream content = new PDPageContentStream(document,page)){
                    content.drawImage(image,0,0, image.getWidth() ,image.getHeight());
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        }catch (Exception e){
            throw new RuntimeException("Image to PDF conversion failed",e);
        }
    }

    @Override
    public boolean supports(ConversionType type) {
        return type ==ConversionType.IMAGE_TO_PDF;
    }

}
