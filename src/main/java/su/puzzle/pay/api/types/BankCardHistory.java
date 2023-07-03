package su.puzzle.pay.api.types;

public record BankCardHistory(int id, HistoryBankCard card, int amount, String message, int date) {
}
