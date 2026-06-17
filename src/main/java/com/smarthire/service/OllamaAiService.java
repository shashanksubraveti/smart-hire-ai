package com.smarthire.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OllamaAiService {

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    private final RestTemplate restTemplate = new RestTemplate();

    public String analyzeResume(String resumeText, String jobDescription) {

        String cleanedResumeText = limitText(resumeText, 6000);

        String prompt = """
                You are SmartHire AI, an AI resume screening assistant.

                Analyze the resume against the job description.

                Return the answer in this format:

                Match Score: percentage from 0 to 100

                Summary:
                short summary of the candidate fit

                Strengths:
                - list strengths

                Missing Skills:
                - list missing skills

                Recommendation:
                final hiring recommendation

                Job Description:
                %s

                Resume Text:
                %s
                """.formatted(jobDescription, cleanedResumeText);

        Map<String, Object> requestBody = Map.of(
                "model", ollamaModel,
                "prompt", prompt,
                "stream", false
        );

        Map response = restTemplate.postForObject(
                ollamaApiUrl,
                requestBody,
                Map.class
        );

        if (response == null || response.get("response") == null) {
            return "No response received from Ollama.";
        }

        return response.get("response").toString();
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