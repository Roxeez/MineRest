package net.roxeez.minerest.api.response;

import lombok.Builder;
import net.roxeez.minerest.api.response.object.PluginObject;

import java.util.List;

@Builder
public class ServerResponse
{
    private final String name;
    private final String motd;
    private final String version;
    private final String bukkitVersion;
    private final String gameMode;
    private final int players;
    private final int maxPlayers;
    private final List<PluginObject> plugins;
}
