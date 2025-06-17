import apiClient from './apiClient'

export const surveyApi = {
    createSurvey: async (data: any) => {
        const response = await apiClient.post('/surveys', data)
        return response.data
    },
    getSurvey: async (surveyId: string) => {
        const response = await apiClient.get(`/surveys/${surveyId}`)
        return response.data
    },
    answerSurvey: async (surveyId: string, answer: any) => {
        const response = await apiClient.post(`/surveys/${surveyId}/answer`, answer)
        return response.data
    },
    getSurveyResult: async (surveyId: string) => {
        const response = await apiClient.get(`/surveys/${surveyId}/result`)
        return response.data
    },
}
