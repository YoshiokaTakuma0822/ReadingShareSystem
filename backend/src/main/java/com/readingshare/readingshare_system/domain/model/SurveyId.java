package com.readingshare.readingshare_system.domain.model;

public class SurveyId {
    private String id;

    public SurveyId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SurveyId)) return false;
        SurveyId surveyId = (SurveyId) o;
        return id.equals(surveyId.id);
    }
