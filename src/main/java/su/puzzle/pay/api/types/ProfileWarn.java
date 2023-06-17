package su.puzzle.pay.api.types;

public record ProfileWarn(
        int date,
        boolean force,
        String helper,
        String message,
        boolean revoked) {
}
