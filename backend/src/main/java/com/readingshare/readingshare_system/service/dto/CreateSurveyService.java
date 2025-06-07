package com.readingshare.readingshare_system.service.dto;

public  class CreateSurveyService {
    private String title;
    private String description;

    public CreateSurveyService(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    
}
