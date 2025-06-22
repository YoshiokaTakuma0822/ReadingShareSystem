package com.readingshare.room.service.dto;

/**
 * 部屋作成APIのリクエストDTO
 */
public class CreateRoomRequest {

    private String roomName;
    private Long hostUserId;
    private Integer totalPages;      // 本のページ数
    private Integer pageTurnSpeed;   // ページめくり速度
    private String genre;           // ジャンル
    private String startTime;       // 開始時刻（例: "2025-06-22T10:00:00"）
    private String endTime;         // 終了時刻
    private String bookTitle;

    public CreateRoomRequest() {
        // デフォルトコンストラクタ
    }

    public CreateRoomRequest(String roomName, Long hostUserId, Integer totalPages, Integer pageTurnSpeed, String genre, String startTime, String endTime) {
        this.roomName = roomName;
        this.hostUserId = hostUserId;
        this.totalPages = totalPages;
        this.pageTurnSpeed = pageTurnSpeed;
        this.genre = genre;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Long getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(Long hostUserId) {
        this.hostUserId = hostUserId;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPageTurnSpeed() {
        return pageTurnSpeed;
    }

    public void setPageTurnSpeed(Integer pageTurnSpeed) {
        this.pageTurnSpeed = pageTurnSpeed;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    @Override
    public String toString() {
        return "CreateRoomRequest{" +
                "roomName='" + roomName + '\'' +
                ", hostUserId=" + hostUserId +
                ", totalPages=" + totalPages +
                ", pageTurnSpeed=" + pageTurnSpeed +
                ", genre='" + genre + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                '}';
    }
}