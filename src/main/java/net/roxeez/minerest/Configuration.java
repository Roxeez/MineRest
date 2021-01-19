package net.roxeez.minerest;

import org.bukkit.configuration.file.FileConfiguration;

public class Configuration
{
    private final String token;
    private final int port;
    private final boolean debug;

    public Configuration(String token, int port, boolean debug)
    {
        this.token = token;
        this.port = port;
        this.debug = debug;
    }

    public String getToken()
    {
        return token;
    }

    public int getPort()
    {
        return port;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public static Configuration from(FileConfiguration configuration)
    {
        String token = configuration.getString("token");
        int port = configuration.getInt("port");
        boolean debug = configuration.getBoolean("debug");

        return new Configuration(token, port, debug);
    }
}
