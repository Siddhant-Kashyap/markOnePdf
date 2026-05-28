package com.markOne.api.Conversion;

import com.markOne.api.Entity.FileMetaData;
import com.markOne.api.Enum.ConversionType;
import com.markOne.api.Storage.StorageService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HtmlToPdfConverter implements Converter {

    private final StorageService storageService;

    @Override
    public byte[] convert(List<FileMetaData> inputs) {
        try {
            StringBuilder htmlContent = new StringBuilder();
            for (FileMetaData file : inputs) {
                Resource resource = storageService.load(file.getStoragePath());
                htmlContent.append(new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent.toString(), null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("HTML to PDF conversion failed", e);
        }
    }

    @Override
    public boolean supports(ConversionType type) {
        return type == ConversionType.HTML_TO_PDF;
    }
}
