package su.puzzle.pay.api.types;

public record ProfileTeam(
        String banner,
        String description,
        String discord,
        int id,
        int marx,
        int members,
        String name,
        String owner,
        boolean recruit,
        String url
) {
}
