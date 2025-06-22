package com.readingshare.chat.domain.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

/**
 * グループチャットのメッセージ内容値オブジェクト。
 *
 * @author 02001
 * @componentId C4
 * @moduleName メッセージ内容値オブジェクト
 */
@Embeddable
public class MessageContent {

    @NotNull
    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String value; // メッセージの実際のテキスト内容

    /**
     * デフォルトコンストラクタ。
     * JPAの要件により必要です。
     */
    protected MessageContent() {
    }

    /**
     * メッセージ内容を指定してMessageContentを作成します。
     *
     * @param value メッセージ内容
     * @throws IllegalArgumentException メッセージ内容がnullまたは空の場合
     */
    public MessageContent(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be null or empty.");
        }
        this.value = value;
    }

    /**
     * メッセージ内容を取得します。
     *
     * @return メッセージ内容
     */
    public String getValue() {
        return value;
    }

    /**
     * このオブジェクトが指定されたオブジェクトと等しいかどうかを判定します。
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
        MessageContent that = (MessageContent) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "MessageContent{" +
                "value='" + value + '\'' +
                '}';
    }
}
