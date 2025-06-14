package com.readingshare.survey.domain.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * SurveyIdはアンケートの識別子を表すバリューオブジェクト。
 * 外部キーとして利用される場合がある。
 */
@Embeddable
public class SurveyId implements Serializable {

    private Long value;

    // デフォルトコンストラクタ (JPAのために必要)
    protected SurveyId() {}

    public SurveyId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("SurveyId must be a positive non-null value.");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurveyId surveyId = (SurveyId) o;
        return Objects.equals(value, surveyId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "SurveyId{" +
               "value=" + value +
               '}';
    }
}