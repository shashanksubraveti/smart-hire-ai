package com.smarthire.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.dto.ResumeAnalysisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Service
public class OllamaAiService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaAiService.class);

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OllamaAiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(180));

        this.restTemplate = new RestTemplate(factory);
        this.objectMapper = new ObjectMapper();
    }

    public ResumeAnalysisResponse analyzeResume(String resumeText, String jobDescription) {

        String prompt = """
                You are SmartHire AI, an intelligent resume screening assistant.

                Analyze the resume text against the job description.

                Return ONLY valid JSON.
                Do not include markdown.
                Do not include explanations outside the JSON.
                Do not wrap the JSON in ```json.

                The JSON must follow this exact structure:

                {
                  "matchScore": 85,
                  "summary": "Short summary of the candidate fit.",
                  "strengths": [
                    "Strength 1",
                    "Strength 2",
                    "Strength 3"
                  ],
                  "missingSkills": [
                    "Missing skill 1",
                    "Missing skill 2"
                  ],
                  "recommendations": [
                    "Recommendation 1",
                    "Recommendation 2"
                  ]
                }

                Rules:
                - matchScore must be a number between 0 and 100.
                - strengths must contain exactly 3 items.
                - missingSkills must contain exactly 3 items.
                - recommendations must contain exactly 3 items.
                - Keep each item short and professional.
                - Return complete valid JSON only.
                - The response must start with { and end with }.

                Resume Text:
                %s

                Job Description:
                %s
                """.formatted(resumeText, jobDescription);

        Map<String, Object> requestBody = Map.of(
                "model", ollamaModel,
                "prompt", prompt,
                "stream", false,
                "options", Map.of(
                        "temperature", 0.1,
                        "top_p", 0.9,
                        "seed", 42,
                        "num_predict", 1000
                )
        );

        try {
            logger.info("Sending resume analysis request to Ollama model: {}", ollamaModel);

            Map<String, Object> response = restTemplate.postForObject(
                    ollamaApiUrl,
                    requestBody,
                    Map.class
            );

            if (response == null || response.get("response") == null) {
                logger.error("Ollama returned an empty response.");
                throw new RuntimeException("Ollama returned an empty response.");
            }

            String aiResponse = response.get("response").toString();

            logger.info("Raw Ollama response: {}", aiResponse);

            String jsonResponse = extractJson(aiResponse);

            logger.info("Extracted JSON response: {}", jsonResponse);

            return objectMapper.readValue(jsonResponse, ResumeAnalysisResponse.class);

        } catch (Exception exception) {
            logger.error("Error while calling or parsing Ollama API response", exception);
            throw new RuntimeException("Failed to analyze resume using Ollama.", exception);
        }
    }

    private String extractJson(String aiResponse) {
        int startIndex = aiResponse.indexOf("{");
        int endIndex = aiResponse.lastIndexOf("}");

        if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
            throw new RuntimeException("No valid JSON object found in Ollama response.");
        }

        return aiResponse.substring(startIndex, endIndex + 1);
    }
}