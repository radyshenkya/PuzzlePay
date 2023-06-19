package su.puzzle.pay.api.types;

public record BankCard(
        String bank,
        String bank_code,
        String currency,
        int design,
        String holder,
        int holder_id,
        int holder_type,
        int id,
        String name,
        int permissions,
        boolean text_inverted,
        int value,
        boolean value_hidden
) {
    public String getNormalId() {
        return "EB-" + String.format("%04d", id);
    }
}
