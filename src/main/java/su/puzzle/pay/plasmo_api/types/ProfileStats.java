package su.puzzle.pay.plasmo_api.types;

public record ProfileStats(
            int all,
            int last_seen,
            int month,
            String on_server,
            boolean on_site,
            int today,
            int web_last_seen,
            int week,
            String world,
            int yesterday
        ) {}
