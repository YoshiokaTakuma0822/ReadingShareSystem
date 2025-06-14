package com.readingshare.survey.domain.model;

import java.io.Serializable;

/**
 * アンケートIDを表す値オブジェクト。
 */
public class SurveyId implements Serializable {
    private final String value;

    public SurveyId(String value) {
        this.value = value;
    }
    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurveyId surveyId = (SurveyId) o;
        return value.equals(surveyId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
