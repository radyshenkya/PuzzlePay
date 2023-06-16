package su.puzzle.pay.plasmo_api.types;

import java.util.List;

public record BankCardsResponse(
        String active_card,
        List<BankCard> cards) {
}
