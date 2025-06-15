package com.readingshare.chat.dto;

/**
 * 進捗記録リクエストのDTO。
 */
public class RecordProgressRequest {
    private int currentPage;

    // Getters and Setters
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
