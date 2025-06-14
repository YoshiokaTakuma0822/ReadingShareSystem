package com.readingshare.survey.domain.model;

import lombok.Value;
import java.io.Serializable;

/**
 * アンケートIDを表す値オブジェクト。
 */
@Value
public class SurveyId implements Serializable {
    private final String value;
}
