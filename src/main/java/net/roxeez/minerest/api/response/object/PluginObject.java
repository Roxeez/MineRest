package net.roxeez.minerest.api.response.object;

import lombok.Builder;

@Builder
public class PluginObject
{
    private final String name;
    private final String version;
}
