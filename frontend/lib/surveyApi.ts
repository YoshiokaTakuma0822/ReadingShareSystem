import { CreateSurveyRequest, SubmitSurveyAnswerRequest, Survey, SurveyResult } from '../types/survey'
import apiClient from './apiClient'

export const surveyApi = {
    // アンケート作成：作成されたアンケートID(UUID文字列)を返す
    createSurvey: async (request: CreateSurveyRequest): Promise<string> => {
        // baseURLを空文字で上書きして、重複した '/api' を防止
        const response = await apiClient.post<string>('/api/surveys', request, { baseURL: '' })
        return response.data
    },
    // アンケートフォーマット取得
    getSurveyFormat: async (surveyId: string): Promise<Survey> => {
        const response = await apiClient.get<Survey>(`/api/surveys/${surveyId}/format`, { baseURL: '' })
        return response.data
    },
    // 回答提出
    answerSurvey: async (surveyId: string, request: SubmitSurveyAnswerRequest): Promise<void> => {
        await apiClient.post(`/api/surveys/${surveyId}/answers`, request, { baseURL: '' })
    },
    // 結果取得
    getSurveyResult: async (surveyId: string): Promise<SurveyResult> => {
        const response = await apiClient.get<SurveyResult>(`/api/surveys/${surveyId}/results`, { baseURL: '' })
        return response.data
    },
    // 回答済みかどうかを確認（専用エンドポイントを使用）
    hasAnswered: async (surveyId: string, userId: string): Promise<boolean> => {
        try {
            const response = await apiClient.get<boolean>(`/api/surveys/${surveyId}/has-answered`, { baseURL: '' })
            return response.data
        } catch (error) {
            // エラーの場合（例：認証エラー、サーバーエラー等）は未回答として扱う
            return false
        }
    },
}
