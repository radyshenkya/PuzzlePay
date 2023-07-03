package su.puzzle.pay.api.types;

import java.util.*;

public record BankCardsResponse(
        String active_card,
        List<BankCard> cards
) {
}
