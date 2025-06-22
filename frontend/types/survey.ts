import { UserId } from './auth'
import { RoomId } from './room'

export type SurveyId = string // アンケートID (UUID)

export type QuestionType = 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE' // バックエンドの列挙型に合わせて変更

export interface Question {
    questionText: string // バックエンドのエンティティに合わせてquestionIdを削除、questionTextに変更
    options: string[] // 選択肢
    questionType: QuestionType // 質問タイプ
    allowAnonymous?: boolean // 匿名投票を許可 (バックエンドから追加)
    allowAddOptions?: boolean // 選択肢の追加を許可 (バックエンドから追加)
}

export interface Survey {
    id: SurveyId // バックエンドのエンティティに合わせてsurveyIdからidに変更
    roomId: RoomId
    title: string
    questions: Question[]
    createdAt: string // 作成時刻 (LocalDateTime)
}

export interface SurveyAnswer {
    id: string // UUID
    surveyId: SurveyId
    userId: UserId
    answers: Map<string, string[]> // バックエンドのエンティティに合わせて変更
    isAnonymous: boolean // 匿名回答かどうか (バックエンドから追加)
    answeredAt: string // 回答時刻 (LocalDateTime)
}

export interface SubmitSurveyAnswerRequest {
    surveyId: SurveyId
    userId: UserId // 追加
    answers: Record<string, string[]> // Map型からRecord型に修正
    isAnonymous?: boolean // 匿名回答かどうか
}

export interface CreateSurveyRequest {
    roomId: RoomId
    title: string
    questions: Question[] // バックエンドのエンティティに合わせて変更
}

export interface SurveyResult {
    surveyId: SurveyId
    title: string
    results: {
        questionText: string
        votes: { [key: string]: number } // バックエンドのvotesに合わせる
    }[]
}
