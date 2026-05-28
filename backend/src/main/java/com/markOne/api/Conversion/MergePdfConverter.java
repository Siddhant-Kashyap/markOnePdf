package com.markOne.api.Conversion;

import com.markOne.api.Entity.FileMetaData;
import com.markOne.api.Enum.ConversionType;
import com.markOne.api.Storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MergePdfConverter implements Converter {

    private final StorageService storageService;

    @Override
    public byte[] convert(List<FileMetaData> inputs) {
        try {
            PDFMergerUtility merger = new PDFMergerUtility();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            merger.setDestinationStream(out);

            for (FileMetaData file : inputs) {
                Resource resource = storageService.load(file.getStoragePath());
                merger.addSource(new RandomAccessReadBuffer(resource.getInputStream()));
            }

            merger.mergeDocuments(null);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF merge failed", e);
        }
    }

    @Override
    public boolean supports(ConversionType type) {
        return type == ConversionType.MERGE_PDF;
    }
}
