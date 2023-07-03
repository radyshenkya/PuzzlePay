package su.puzzle.pay.api.types;

import java.util.*;

public record BankCardHistoryResponse(int total, List<BankCardHistory> list) {
}
