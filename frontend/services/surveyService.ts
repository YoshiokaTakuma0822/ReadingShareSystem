import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const surveyService = {
  createSurvey: async (data: any) => {
    const response = await axios.post(`${API_BASE_URL}/surveys`, data);
    return response.data;
  },
  getSurvey: async (surveyId: string) => {
    const response = await axios.get(`${API_BASE_URL}/surveys/${surveyId}`);
    return response.data;
  },
  answerSurvey: async (surveyId: string, answer: any) => {
    const response = await axios.post(`${API_BASE_URL}/surveys/${surveyId}/answer`, answer);
    return response.data;
  },
  getSurveyResult: async (surveyId: string) => {
    const response = await axios.get(`${API_BASE_URL}/surveys/${surveyId}/result`);
    return response.data;
  },
};
