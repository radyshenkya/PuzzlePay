package su.puzzle.pay.plasmo_api.types;

public record TransferRequest(int amount, String from, String message, String to) {}
