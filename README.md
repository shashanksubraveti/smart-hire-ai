# SmartHire AI

SmartHire AI is a Java Spring Boot backend application that analyzes a resume against a job description using a free local LLM through Ollama.

## Version 1 Features

- Java Spring Boot REST API
- Resume PDF upload
- Job description text input
- Resume text extraction using Apache PDFBox
- Local AI analysis using Ollama
- Returns match score, strengths, missing skills, and recommendation
- Tested using Postman

## Tech Stack

- Java 17
- Spring Boot 3.5.15
- Maven
- Apache PDFBox
- Ollama
- Llama 3.2 1B
- Postman

## API Endpoint

### Analyze Resume

```http
POST http://localhost:8080/api/resume/analyze