package com.readingshare.room.domain.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * RoomIdは部屋の識別子を表すバリューオブジェクト。
 * 外部キーとして利用される場合がある。
 */
@Embeddable // 他のエンティティに埋め込み可能であることを示す
public class RoomId implements Serializable {

    private Long value;

    // デフォルトコンストラクタ (JPAのために必要)
    protected RoomId() {}

    public RoomId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("RoomId must be a positive non-null value.");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomId roomId = (RoomId) o;
        return Objects.equals(value, roomId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "RoomId{" +
               "value=" + value +
               '}';
    }
}