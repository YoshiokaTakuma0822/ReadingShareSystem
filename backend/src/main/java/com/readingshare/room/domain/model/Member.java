package com.readingshare.room.domain.model;

import java.util.Objects;

import com.readingshare.account.domain.model.Account;

/**
 * Domain model for a chat room member.
 */
public class Member {
    private Long id;
    private String name;
    private Account account;
    private Room room;

    private Member(Long id, String name, Account account, Room room) {
        this.id = id;
        this.name = name;
        this.account = account;
        this.room = room;
        validate();
    }

    /** Factory for new Member in a room. */
    public static Member create(String name, Account account, Room room) {
        return new Member(null, name, account, room);
    }

    /**
     * Reconstitute an existing Member from persistence.
     */
    public static Member reconstitute(Long id, String name, Account account, Room room) {
        return new Member(id, name, account, room);
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Member name must not be blank");
        }
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        }
        if (room == null) {
            throw new IllegalArgumentException("Room must not be null");
        }
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

    public Account getAccount() {
        return account;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Member))
            return false;
        Member member = (Member) o;
        return id != null && id.equals(member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
