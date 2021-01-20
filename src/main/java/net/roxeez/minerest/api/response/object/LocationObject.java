package net.roxeez.minerest.api.response.object;

import lombok.Builder;

@Builder
public class LocationObject
{
    private final String world;
    private final double x;
    private final double y;
    private final double z;
}
