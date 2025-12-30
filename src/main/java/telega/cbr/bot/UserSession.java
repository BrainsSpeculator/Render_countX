package telega.cbr.bot;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

public class UserSession {
    private static final ConcurrentHashMap<Long, DialogState> userStates = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, String> userCurrencyCode = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, LocalDate> userDate = new ConcurrentHashMap<>();

    public static void setState(Long chatId, DialogState state) {
        userStates.put(chatId, state);
    }

    public static DialogState getState(Long chatId) {
        return userStates.getOrDefault(chatId, DialogState.START);
    }

    public static void setCurrencyCode(Long chatId, String code) {
        userCurrencyCode.put(chatId, code);
    }

    public static String getCurrencyCode(Long chatId) {
        return userCurrencyCode.get(chatId);
    }

    public static void setDate(Long chatId, LocalDate date) {
        userDate.put(chatId, date);
    }

    public static LocalDate getDate(Long chatId) {
        return userDate.get(chatId);
    }

    public static void clear(Long chatId) {
        userStates.remove(chatId);
        userCurrencyCode.remove(chatId);
        userDate.remove(chatId);
    }
}