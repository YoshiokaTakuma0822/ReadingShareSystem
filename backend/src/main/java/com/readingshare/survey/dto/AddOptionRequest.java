package com.readingshare.survey.dto;

/**
 * 選択肢追加リクエストDTO
 *
 * @author 02002
 * @componentId C4
 * @moduleName アンケート選択肢追加リクエストDTO
 */
public record AddOptionRequest(String questionText, String newOption) {
}
