package com.smarthire.dto;

import java.util.List;

public class ResumeAnalysisResponse {

    private int matchScore;
    private String summary;
    private List<String> strengths;
    private List<String> missingSkills;
    private List<String> recommendations;

    public ResumeAnalysisResponse() {
    }

    public ResumeAnalysisResponse(
            int matchScore,
            String summary,
            List<String> strengths,
            List<String> missingSkills,
            List<String> recommendations
    ) {
        this.matchScore = matchScore;
        this.summary = summary;
        this.strengths = strengths;
        this.missingSkills = missingSkills;
        this.recommendations = recommendations;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}