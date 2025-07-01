package com.readingshare.room.dto;

import com.readingshare.room.domain.model.Room;
import java.time.Instant;
import java.util.UUID;

public class RoomResponse {
    private UUID id;
    private String roomName;
    private String bookTitle;
    private UUID hostUserId;
    private String hostUsername;
    private Integer maxPage;
    private String genre;
    private Instant startTime;
    private Instant endTime;
    private Integer pageSpeed;

    public RoomResponse(UUID id, String roomName, String bookTitle, UUID hostUserId, String hostUsername, Integer maxPage, String genre, Instant startTime, Instant endTime, Integer pageSpeed) {
        this.id = id;
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.hostUsername = hostUsername;
        this.maxPage = maxPage;
        this.genre = genre;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageSpeed = pageSpeed;
    }

    public UUID getId() { return id; }
    public String getRoomName() { return roomName; }
    public String getBookTitle() { return bookTitle; }
    public UUID getHostUserId() { return hostUserId; }
    public String getHostUsername() { return hostUsername; }
    public Integer getMaxPage() { return maxPage; }
    public String getGenre() { return genre; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public Integer getPageSpeed() { return pageSpeed; }

    public static RoomResponse fromRoom(Room room) {
        return new RoomResponse(
            room.getId(),
            room.getRoomName(),
            room.getBookTitle(),
            room.getHostUserId(),
            null, // hostUsername: 必要なら後で取得
            room.getMaxPage(),
            room.getGenre(),
            room.getStartTime(),
            room.getEndTime(),
            room.getPageSpeed()
        );
    }
}
