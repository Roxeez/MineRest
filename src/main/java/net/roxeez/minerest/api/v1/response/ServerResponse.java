package net.roxeez.minerest.api.v1.response;

import lombok.Builder;

@Builder
public class ServerResponse
{
    private final String name;
    private final String motd;
    private final String version;
    private final String bukkitVersion;
    private final String gameMode;
}
