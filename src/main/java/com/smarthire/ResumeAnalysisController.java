package com.smarthire;

import com.smarthire.service.OllamaAiService;
import com.smarthire.service.PdfTextExtractionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "http://localhost:4200")
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

        System.out.println("STEP 1: Request received from Angular");

        String resumeText = pdfTextExtractionService.extractTextFromPdf(resume);

        System.out.println("STEP 2: PDF text extracted");
        System.out.println("Resume text length: " + resumeText.length());

        String result = ollamaAiService.analyzeResume(resumeText, jobDescription);

        System.out.println("STEP 3: Ollama response received");

        return result;
    }
}