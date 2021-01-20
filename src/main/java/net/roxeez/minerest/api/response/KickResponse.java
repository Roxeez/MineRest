package net.roxeez.minerest.api.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public class KickResponse
{
    private final UUID playerId;
    private final String reason;
}
