package com.markOne.api.Conversion;

import com.markOne.api.Entity.FileMetaData;
import com.markOne.api.Enum.ConversionType;
import com.markOne.api.Storage.StorageService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MarkdownToPdfConverter implements Converter{
    private final StorageService storageService;
    @Override
    public byte[] convert(List<FileMetaData> inputs) {
        try {
            StringBuilder markdown = new StringBuilder();
            for (FileMetaData file : inputs) {
                Resource resource = storageService.load(file.getStoragePath());
                markdown.append(new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
                markdown.append("\n");
            }

            Parser parser = Parser.builder().build();
            Node document = parser.parse(markdown.toString());
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String html = "<html><body>" + renderer.render(document) + "</body></html>";

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Markdown to PDF conversion failed", e);
        }
    }

    @Override
    public boolean supports(ConversionType type) {
        return type == ConversionType.MARKDOWN_TO_PDF;
    }
}
