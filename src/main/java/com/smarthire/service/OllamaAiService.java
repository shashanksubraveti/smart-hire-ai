package com.smarthire.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Service
public class OllamaAiService {

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    private final RestTemplate restTemplate;

    public OllamaAiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(60));
        this.restTemplate = new RestTemplate(factory);
    }

    public String analyzeResume(String resumeText, String jobDescription) {
        String cleanedResumeText = limitText(resumeText, 6000);
        String cleanedJobDescription = limitText(jobDescription, 4000);

        String prompt = """
                You are SmartHire AI.

                Compare the resume with the job description.

                Be consistent and objective. Do not change the scoring style between runs.
                Use the same evaluation logic every time for the same resume and job description.

                Return the answer only in this format:

                Match Score: __ percent

                Summary:
                One short paragraph.

                Strengths:
                - point 1
                - point 2

                Missing Skills:
                - point 1
                - point 2

                Recommendation:
                One short final recommendation.

                Job Description:
                %s

                Resume:
                %s
                """.formatted(cleanedJobDescription, cleanedResumeText);

        Map<String, Object> requestBody = Map.of(
                "model", ollamaModel,
                "prompt", prompt,
                "stream", false,
                "options", Map.of(
                        "num_predict", 500,
                        "temperature", 0.1,
                        "top_p", 0.9,
                        "seed", 42
                )
        );

        try {
            Map<?, ?> response = restTemplate.postForObject(
                    ollamaApiUrl,
                    requestBody,
                    Map.class
            );

            if (response == null || response.get("response") == null) {
                return "No response received from Ollama.";
            }

            return response.get("response").toString();

        } catch (Exception exception) {
            return """
                    Ollama request failed or timed out.
                    Please make sure Ollama is running and try again.
                    """;
        }
    }

    private String limitText(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength);
    }
}