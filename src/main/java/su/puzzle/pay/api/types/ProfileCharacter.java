package su.puzzle.pay.api.types;

public record ProfileCharacter(
        String description,
        int id,
        String name,
        String role,
        String skin_format
) {
}
