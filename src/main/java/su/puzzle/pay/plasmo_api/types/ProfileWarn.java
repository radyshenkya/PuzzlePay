package su.puzzle.pay.plasmo_api.types;

public record ProfileWarn(
        int date,
        boolean force,
        String helper,
        String message,
        boolean revoked) {
}
