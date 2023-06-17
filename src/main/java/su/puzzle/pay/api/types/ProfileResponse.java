package su.puzzle.pay.api.types;

import java.util.Map;
import java.util.List;

import com.google.gson.JsonObject;

public record ProfileResponse(
            String ban_reason,
            int ban_time,
            boolean banned,
            List<ProfileCharacter> characters,
            String description,
            String discord_id,
            int fusion,
            boolean has_access,
            List<PlayerDayStat> heatmap,
            int id,
            boolean in_guild,
            Map<String, UserProfileIntegration> integrations,
            boolean is_liked,
            int likes,
            JsonObject marks, // TODO: тут должен использоваться тип Marx из доков к апишке
            List<String> roles,
            String nick,
            String skin_format,
            List<ProfileStats> stats,
            List<ProfileTeam> teams,
            String uuid,
            List<ProfileWarn> warns
) {}
