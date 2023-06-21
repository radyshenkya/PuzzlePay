package su.puzzle.pay.api.types;

import java.util.List;

public record TokenInfoResponse(
        List<String> scopes
) {}
