package net.roxeez.minerest.api.response.object;

import lombok.Builder;
import org.bukkit.GameMode;

import java.util.UUID;

@Builder
public class PlayerObject
{
    private final UUID uuid;
    private final String name;
    private final boolean online;
    private final boolean banned;
    private final boolean op;
    private final long firstPlayed;
    private final long lastPlayed;
    private final GameMode gameMode;
    private final LocationObject location;
}
