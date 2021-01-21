package net.roxeez.minerest.security;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class PermissionManager
{
    private final Logger logger;
    private final File storage;
    private final Map<String, Set<String>> permissions;

    public static final String ADMIN_PERMISSION = "admin";

    public PermissionManager(Logger logger, File storage)
    {
        this.logger = logger;
        this.storage = storage;
        this.permissions = new HashMap<>();
    }

    public boolean hasPermission(String token, String permission)
    {
        Set<String> permissions = this.permissions.get(token);
        if (permissions == null)
        {
            return false;
        }

        return permissions.contains(ADMIN_PERMISSION) || permissions.contains(permission);
    }

    public void addPermissions(String token, Collection<String> permissions)
    {
        this.permissions.put(token, new HashSet<>(permissions));
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
            addPermissions(token, configuration.getStringList(token));
        }

        logger.info("Loaded " + permissions.size() + " tokens");
    }

    public void save()
    {
        YamlConfiguration configuration = new YamlConfiguration();
        for(Map.Entry<String, Set<String>> entry : permissions.entrySet())
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
