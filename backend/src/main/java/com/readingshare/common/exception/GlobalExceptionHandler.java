package com.readingshare.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * アプリケーション全体のグローバル例外ハンドラー。
 * 各種例外を適切なHTTPレスポンスに変換する。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * DomainExceptionをHTTP 400 Bad Requestとして処理する。
     * ビジネスルール違反によるエラー。
     *
     * @param ex DomainException
     * @return エラーメッセージを含むResponseEntity
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * DuplicateUsernameExceptionをHTTP 409 Conflictとして処理する。
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUsernameException(DuplicateUsernameException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * DatabaseAccessExceptionをHTTP 500 Internal Server Errorとして処理する。
     * データベースアクセスエラー。
     *
     * @param ex DatabaseAccessException
     * @return エラーメッセージを含むResponseEntity
     */
    @ExceptionHandler(DatabaseAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseAccessException(DatabaseAccessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(),
                "Database access error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * DifferentQuestionnaireComponentExceptionをHTTP 400 Bad Requestとして処理する。
     * アンケート関連の整合性エラー。
     *
     * @param ex DifferentQuestionnaireComponentException
     * @return エラーメッセージを含むResponseEntity
     */
    @ExceptionHandler(DifferentQuestionnaireComponentException.class)
    public ResponseEntity<ErrorResponse> handleQuestionnaireException(DifferentQuestionnaireComponentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * ResourceNotFoundExceptionをHTTP 404 Not Foundとして処理する。
     * リソースが見つからない場合のエラー。
     *
     * @param ex ResourceNotFoundException
     * @return エラーメッセージを含むResponseEntity
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * ApplicationExceptionをHTTP 400 Bad Requestとして処理する。
     * アプリケーション層の一般的なエラー。
     *
     * @param ex ApplicationException
     * @return エラーメッセージを含むResponseEntity
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getCode(), ex.getMessage()); // code from
                                                                                        // ApplicationException
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * その他の予期しない例外をHTTP 500 Internal Server Errorとして処理する。
     * 詳細なエラー情報は含めず、一般的なメッセージを返す。
     *
     * @param ex Exception
     * @return 一般的なエラーメッセージを含むResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(), "An unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * エラーレスポンス用のDTO。
     */
    public static class ErrorResponse {
        private final String code;
        private final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
