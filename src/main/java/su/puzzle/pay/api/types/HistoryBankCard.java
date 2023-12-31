package su.puzzle.pay.api.types;

public record HistoryBankCard(String bank_code, String holder, int holder_id, int holder_type, int id, String name) {
    public String getNormalId() {
        return "EB-" + String.format("%04d", id);
    }
}
