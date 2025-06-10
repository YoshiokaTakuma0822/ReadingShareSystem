package com.readingshare.room.infrastructure.persistence;

import com.readingshare.account.infrastructure.persistence.AccountEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity representing a member in the chat system.
 * Authentication is handled by AccountEntity.
 * MemberEntity is focused solely on chat participation.
 */
@Entity
@Table(name = "members")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Each member is linked to the account that created it
    @ManyToOne
    @JoinColumn(name = "account_id", columnDefinition = "uuid")
    private AccountEntity account;

    // Each member belongs to one room
    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    public MemberEntity() {
    }

    /**
     * Constructor for creating a chat member with display name only.
     * Used for temporary or guest members without authentication.
     */
    public MemberEntity(String name) {
        this.name = name;
    }

    /**
     * Constructor for creating an authenticated chat member.
     * Links the member to their authentication account.
     */
    public MemberEntity(String name, AccountEntity account) {
        this.name = name;
        this.account = account;
    }

    /**
     * Constructor for creating a member associated with both account and room.
     */
    public MemberEntity(String name, AccountEntity account, RoomEntity room) {
        this.name = name;
        this.account = account;
        this.room = room;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MemberEntity user = (MemberEntity) o;
        return id != null && id.equals(user.id);
    }
}
