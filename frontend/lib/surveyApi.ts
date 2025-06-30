import apiClient from './apiClient'
import { Survey, CreateSurveyRequest, SubmitSurveyAnswerRequest, SurveyResult } from '../types/survey'

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
}
