package net.roxeez.minerest.utility;

import java.util.UUID;

public final class StringUtility
{
    private StringUtility()
    {

    }

    public static UUID safeParseUUID(String value)
    {
        try
        {
            return UUID.fromString(value);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
