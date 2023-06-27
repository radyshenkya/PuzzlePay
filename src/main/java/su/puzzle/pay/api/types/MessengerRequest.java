package su.puzzle.pay.api.types;

public record MessengerRequest(int chat_id, String content, int user_id) {}
