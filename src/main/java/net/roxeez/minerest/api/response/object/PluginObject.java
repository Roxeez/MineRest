package net.roxeez.minerest.api.response;

import lombok.Builder;

@Builder
public class PluginResponse
{
    private final String name;
    private final String version;
}
