package com.readingshare.survey.exception;

public enum SurveyErrorCode {
    TOO_FEW_OPTIONS, // 選択肢が2つ未満
    DUPLICATE_OPTIONS, // 重複選択肢
    ROOM_INACTIVE // 部屋が活動時間外
}
