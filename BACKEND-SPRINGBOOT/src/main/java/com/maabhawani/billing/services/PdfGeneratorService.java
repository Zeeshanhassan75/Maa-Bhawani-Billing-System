package com.maabhawani.billing.services;

import com.maabhawani.billing.models.Invoice;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

        @Autowired
        private TemplateEngine templateEngine;

        public byte[] generateInvoicePdf(Invoice invoice) throws Exception {
                // 1. Setup Thymeleaf Context
                Context context = new Context();
                context.setVariable("invoice", invoice);

                // 2. Render HTML explicitly using our new XHTML template
                String renderedHtml = templateEngine.process("invoice-template", context);

                // 3. Convert HTML to PDF using OpenHTMLtoPDF fast-renderer
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        PdfRendererBuilder builder = new PdfRendererBuilder();
                        builder.useFastMode(); // Fast mode is strictly recommended for table/text generation

                        // Render the HTML string. The second param is baseURI for resolving external
                        // assets like images.
                        builder.withHtmlContent(renderedHtml, "classpath:/templates/");
                        builder.toStream(outputStream);
                        builder.run();

                        return outputStream.toByteArray();
                }
        }
}
