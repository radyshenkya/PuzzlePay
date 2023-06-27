package su.puzzle.pay.api.types;

public record MessengerMessage(int chat_id, String content, int date, int id, int type, boolean unread, int user_id) {}
