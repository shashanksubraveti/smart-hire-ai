package com.smarthire;

import com.smarthire.service.OllamaAiService;
import com.smarthire.service.PdfTextExtractionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
public class ResumeAnalysisController {

    private final PdfTextExtractionService pdfTextExtractionService;
    private final OllamaAiService ollamaAiService;

    public ResumeAnalysisController(
            PdfTextExtractionService pdfTextExtractionService,
            OllamaAiService ollamaAiService
    ) {
        this.pdfTextExtractionService = pdfTextExtractionService;
        this.ollamaAiService = ollamaAiService;
    }

    @PostMapping("/analyze")
    public String analyzeResume(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("jobDescription") String jobDescription
    ) throws IOException {

        String resumeText = pdfTextExtractionService.extractTextFromPdf(resume);

        return ollamaAiService.analyzeResume(resumeText, jobDescription);
    }
}