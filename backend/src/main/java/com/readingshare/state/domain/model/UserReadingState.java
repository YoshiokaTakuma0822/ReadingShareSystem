package com.readingshare.state.domain.model;

import java.util.Objects;

/**
 * ユーザーの読書状態を表すクラスです。
 * このクラスは、ユーザーID、現在のページ、およびコメントを保持します。
 *
 * @author 02003
 * @componentId C4
 * @moduleName ユーザー読書状態モデル
 */
public class UserReadingState {
    private final String userId;
    private final int currentPage;
    private final String comment;

    /**
     * コンストラクタ
     *
     * @param userId      ユーザーID
     * @param currentPage 現在のページ
     * @param comment     コメント
     */
    public UserReadingState(String userId, int currentPage, String comment) {
        this.userId = userId;
        this.currentPage = currentPage;
        this.comment = comment;
    }

    /**
     * ユーザーIDを取得します。
     *
     * @return ユーザーID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 現在のページを取得します。
     *
     * @return 現在のページ
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * コメントを取得します。
     *
     * @return コメント
     */
    public String getComment() {
        return comment;
    }

    /**
     * このオブジェクトが指定のオブジェクトと等しいかどうかを判定します。
     * ユーザーIDが同じ場合に等しいとみなします。
     *
     * @param o 比較対象のオブジェクト
     * @return 等しい場合はtrue、それ以外はfalse
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserReadingState that = (UserReadingState) o;
        return Objects.equals(userId, that.userId);
    }

    /**
     * このオブジェクトのハッシュコードを返します。
     * ユーザーIDに基づいて計算されます。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
