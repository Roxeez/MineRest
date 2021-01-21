package net.roxeez.minerest.security;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class TokenManager
{
    private final Logger logger;
    private final File storage;
    private final Map<String, Set<String>> tokens;

    public TokenManager(Logger logger, File storage)
    {
        this.logger = logger;
        this.storage = storage;
        this.tokens = new HashMap<>();
    }

    public Set<String> getTokenPermissions(String token)
    {
        return tokens.getOrDefault(token, new HashSet<>());
    }

    public void addToken(String token, Collection<String> permissions)
    {
        tokens.put(token, new HashSet<>(permissions));
    }

    public void load()
    {
        if (!storage.exists())
        {
            logger.info("Can't found tokens.yml file");
            return;
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(storage);
        for(String token : configuration.getKeys(false))
        {
            addToken(token, configuration.getStringList(token));
        }

        logger.info("Loaded " + tokens.size() + " tokens");
    }

    public void save()
    {
        YamlConfiguration configuration = new YamlConfiguration();
        for(Map.Entry<String, Set<String>> entry : tokens.entrySet())
        {
            String token = entry.getKey();
            Set<String> permissions = entry.getValue();

            configuration.set(token, permissions);
        }

        try
        {
            configuration.save(storage);
        }
        catch (IOException exception)
        {
            logger.severe("Failed to save token in tokens.yml");
            exception.printStackTrace();
        }
    }
}
