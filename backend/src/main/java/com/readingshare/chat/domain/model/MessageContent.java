package com.readingshare.chat.domain.model;

<<<<<<< Updated upstream
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 * チャットメッセージの内容を表すバリューオブジェクト。
 * ChatMessageエンティティに埋め込まれる。
 */
@Embeddable
public class MessageContent {

    @NotNull
    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String value; // メッセージの実際のテキスト内容

    // デフォルトコンストラクタ (JPAのために必要)
    protected MessageContent() {}

    public MessageContent(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be null or empty.");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
