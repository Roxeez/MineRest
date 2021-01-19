package net.roxeez.minerest.api.v1.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public class PlayerResponse
{
    private final UUID uuid;
    private final String name;
}
