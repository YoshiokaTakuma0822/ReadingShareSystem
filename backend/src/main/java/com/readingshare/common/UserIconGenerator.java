package com.readingshare.common;

public class UserIconGenerator {
    /**
     * ユーザIDやユーザ名からSVGアバターを生成
     * @param userId ユーザID
     * @param userName ユーザ名
     * @return SVG文字列
     */
    public static String generateAvatarSvg(String userId, String userName) {
        String color = pickColor(userId);
        String initial = (userName != null && !userName.isEmpty()) ? userName.substring(0, 1) : "?";
        return "<svg width='64' height='64' xmlns='http://www.w3.org/2000/svg'>"
            + "<circle cx='32' cy='32' r='32' fill='" + color + "'/>"
            + "<text x='50%' y='55%' text-anchor='middle' font-size='32' fill='#fff' dy='.3em'>" + initial + "</text>"
            + "</svg>";
    }

    private static String pickColor(String userId) {
        int hash = userId != null ? userId.hashCode() : 0;
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = (hash & 0x0000FF);
        return String.format("#%02x%02x%02x", r, g, b);
    }
}
