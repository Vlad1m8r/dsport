package ru.weu.dsport.service;

public final class CurrentUserContext {

    public static final String CURRENT_USER_ID_ATTRIBUTE = "currentUserId";
    public static final String CURRENT_TG_USER_ID_ATTRIBUTE = "currentTgUserId";

    private static final ThreadLocal<CurrentUserInfo> CURRENT_USER = new ThreadLocal<>();

    private CurrentUserContext() {
    }

    public static void set(Long userId, Long tgUserId) {
        CURRENT_USER.set(new CurrentUserInfo(userId, tgUserId));
    }

    public static Long getCurrentUserId() {
        CurrentUserInfo info = CURRENT_USER.get();
        return info == null ? null : info.userId();
    }

    public static Long getCurrentTgUserId() {
        CurrentUserInfo info = CURRENT_USER.get();
        return info == null ? null : info.tgUserId();
    }

    public static boolean hasCurrentUser() {
        return CURRENT_USER.get() != null;
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    private record CurrentUserInfo(Long userId, Long tgUserId) {
    }
}
