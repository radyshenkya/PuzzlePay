package su.puzzle.pay.api.types;

public record TransferRequest(int amount, String from, String message, String to) {
}
