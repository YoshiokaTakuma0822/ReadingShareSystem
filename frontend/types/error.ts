// API エラー用共通型とエラーコード列挙
export interface ApiErrorResponse {
    code: string
    message: string
}

// アンケート作成時の固有エラーコード
export enum SurveyErrorCode {
    TOO_FEW_OPTIONS = 'TOO_FEW_OPTIONS',   // 選択肢が2つ未満
    DUPLICATE_OPTIONS = 'DUPLICATE_OPTIONS', // 重複選択肢
    ROOM_INACTIVE = 'ROOM_INACTIVE', // 部屋が活動時間外
    SURVEY_EXPIRED = 'SURVEY_EXPIRED', // アンケート終了時刻過ぎ
}
