/**
 * アンケートの作成、回答、結果取得を行うためのAPIです。
 *
 * @author 02001
 * @componentId C1
 * @moduleName アンケートAPI
 * @packageDocumentation
 */

import { CreateSurveyRequest, SubmitSurveyAnswerRequest, Survey, SurveyResult } from '../types/survey'
import apiClient from './apiClient'

/**
 * アンケートAPIを提供するモジュールです。
 */
export const surveyApi = {
    createSurvey: async (request: CreateSurveyRequest): Promise<Survey> => {
        const response = await apiClient.post('/surveys', request)
        return response.data
    },
    getSurvey: async (surveyId: string): Promise<Survey> => {
        const response = await apiClient.get(`/surveys/${surveyId}`)
        return response.data
    },
    answerSurvey: async (surveyId: string, request: SubmitSurveyAnswerRequest): Promise<void> => {
        const response = await apiClient.post(`/surveys/${surveyId}/answers`, request)
        return response.data
    },
    getSurveyResult: async (surveyId: string): Promise<SurveyResult> => {
        const response = await apiClient.get(`/surveys/${surveyId}/results`)
        return response.data
    },
    hasUserAnswered: async (surveyId: string, userId: string): Promise<boolean> => {
        const response = await apiClient.get(`/surveys/${surveyId}/answered`, { params: { userId } })
        return response.data
    },
}
