package com.readingshare.room.infrastructure.persistence;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.readingshare.account.infrastructure.persistence.AccountEntity;
import com.readingshare.chat.infrastructure.persistence.MessageEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entity representing a chat room.
 */
@Entity
@Table(name = "rooms")
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by", columnDefinition = "uuid")
    private AccountEntity createdBy;

    @OneToMany(mappedBy = "room")
    private List<MessageEntity> messages = new ArrayList<>();

    @OneToMany(mappedBy = "room")
    private List<MemberEntity> members = new ArrayList<>();

    // Constructors
    public RoomEntity() {
        this.createdAt = OffsetDateTime.now();
    }

    public RoomEntity(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    public RoomEntity(String name, String description, AccountEntity createdBy) {
        this(name, description);
        this.createdBy = createdBy;
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public AccountEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AccountEntity createdBy) {
        this.createdBy = createdBy;
    }

    public List<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageEntity> messages) {
        this.messages = messages;
    }

    public List<MemberEntity> getMembers() {
        return members;
    }

    public void setMembers(List<MemberEntity> members) {
        this.members = members;
    }

    /**
     * Helper method to add a member to this room.
     */
    public void addMember(MemberEntity member) {
        if (member != null && !members.contains(member)) {
            members.add(member);
            member.setRoom(this);
        }
    }

    /**
     * Helper method to remove a member from this room.
     */
    public void removeMember(MemberEntity member) {
        if (member != null) {
            members.remove(member);
            member.setRoom(null);
        }
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
        RoomEntity room = (RoomEntity) o;
        return id != null && id.equals(room.id);
    }
}
