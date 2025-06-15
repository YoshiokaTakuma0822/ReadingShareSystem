package readingshare.state.domain.model;

import java.util.Objects;

public class UserReadingState {
    private final String userId;
    private final int currentPage;
    private final String comment;

    public UserReadingState(String userId, int currentPage, String comment) {
        this.userId = userId;
        this.currentPage = currentPage;
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserReadingState that = (UserReadingState) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
