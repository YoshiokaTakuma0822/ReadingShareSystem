import { UserId } from './auth';
import { RoomId } from './room';

export type SurveyId = string; // アンケートID

export interface Question {
  questionId: number;
  questionText: string;
  questionType: 'text' | 'single_choice' | 'multiple_choice'; // 質問タイプ
  options?: string[]; // 選択肢 (single_choice, multiple_choiceの場合)
}

export interface Survey {
  surveyId: SurveyId;
  roomId: RoomId;
  title: string;
  description?: string;
  questions: Question[];
  createdAt: string; // 作成時刻
}

export interface SurveyAnswer {
  questionId: number;
  answer: string | string[]; // 回答内容 (テキスト、単一選択、複数選択)
}

export interface SubmitSurveyAnswerRequest {
  surveyId: SurveyId;
  // userId: UserId; // バックエンドでセッションから取得
  answers: SurveyAnswer[];
}

export interface CreateSurveyRequest {
  roomId: RoomId;
  title: string;
  description?: string;
  questions: Omit<Question, 'questionId'>[]; // 作成時はquestionIdは不要
}

export interface SurveyResult {
  surveyId: SurveyId;
  title: string;
  results: {
    questionId: number;
    questionText: string;
    answers: {
      [key: string]: number; // 例: "選択肢A": 10 (回答数)
    } | string[]; // テキスト回答の場合は文字列の配列
  }[];
}
