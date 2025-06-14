package com.readingshare.common.exception;

/**
 * アンケート関連のコンポーネント間で、変数の欠損や誤りなど、
 * 整合性の問題が発生した場合にスローされる例外。
 * 内部設計書に基づき作成。
 */
public class DifferentQuestionnaireComponentException extends ApplicationException {

    public DifferentQuestionnaireComponentException(String message) {
        super(message);
    }

    public DifferentQuestionnaireComponentException(String message, Throwable cause) {
        super(message, cause);
    }
}