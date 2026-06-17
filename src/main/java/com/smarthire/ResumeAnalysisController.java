package com.smarthire;

import com.smarthire.service.OllamaAiService;
import com.smarthire.service.PdfTextExtractionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "http://localhost:4200")
public class ResumeAnalysisController {

    private static final Logger log = LoggerFactory.getLogger(ResumeAnalysisController.class);

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
    public ResponseEntity<String> analyzeResume(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("jobDescription") String jobDescription
    ) {
        try {
            if (resume == null || resume.isEmpty()) {
                return ResponseEntity.badRequest().body("Resume PDF is required.");
            }

            if (!"application/pdf".equalsIgnoreCase(resume.getContentType())) {
                return ResponseEntity.badRequest().body("Only PDF files are supported.");
            }

            if (jobDescription == null || jobDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Job description is required.");
            }

            log.info("Resume analysis request received. File name: {}", resume.getOriginalFilename());

            String resumeText = pdfTextExtractionService.extractTextFromPdf(resume);

            if (resumeText == null || resumeText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Could not extract text from the uploaded PDF.");
            }

            log.info("PDF text extracted. Character count: {}", resumeText.length());

            String result = ollamaAiService.analyzeResume(resumeText, jobDescription);

            log.info("AI analysis completed successfully.");

            return ResponseEntity.ok(result);

        } catch (IOException exception) {
            log.error("Failed to extract text from PDF", exception);
            return ResponseEntity.internalServerError().body("Failed to read the uploaded PDF.");
        } catch (Exception exception) {
            log.error("Unexpected error during resume analysis", exception);
            return ResponseEntity.internalServerError().body("Unexpected error while analyzing resume.");
        }
    }
}