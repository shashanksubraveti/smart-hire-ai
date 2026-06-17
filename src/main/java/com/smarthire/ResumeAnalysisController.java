package com.smarthire;

import com.smarthire.service.PdfTextExtractionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
public class ResumeAnalysisController {

    private final PdfTextExtractionService pdfTextExtractionService;

    public ResumeAnalysisController(PdfTextExtractionService pdfTextExtractionService) {
        this.pdfTextExtractionService = pdfTextExtractionService;
    }

    @PostMapping("/extract")
    public String extractResumeText(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("jobDescription") String jobDescription
    ) throws IOException {

        String resumeText = pdfTextExtractionService.extractTextFromPdf(resume);

        return "Job Description Received:\n"
                + jobDescription
                + "\n\nExtracted Resume Text:\n"
                + resumeText;
    }
}