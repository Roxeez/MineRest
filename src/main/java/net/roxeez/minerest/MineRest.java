package net.roxeez.minerest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.roxeez.minerest.api.Version;
import net.roxeez.minerest.api.v1.VersionOne;
import net.roxeez.minerest.http.Status;
import net.roxeez.minerest.utility.LoggingUtility;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static spark.Spark.*;

public class MineRest extends JavaPlugin
{
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final Logger logger = getLogger();

    @Override
    public void onEnable()
    {
        logger.info("Saving default configuration");
        saveDefaultConfig();

        logger.info("Loading configuration");
        Configuration configuration = Configuration.from(getConfig());

        if (configuration.getToken() == null)
        {
            logger.severe("Token is not defined in config.yml, http server won't start");
            return;
        }

        logger.info("Starting http server on port " + configuration.getPort());
        port(configuration.getPort());

        if (configuration.isDebug())
        {
            logger.info("Enabling request/response debugging");

            before((request, response) -> getLogger().info(LoggingUtility.format(request)));
            after((request, response) -> getLogger().info(LoggingUtility.format(response)));
        }

        logger.info("Adding security token check");
        before((request, response) ->
        {
            String token = request.headers("X-Access-Token");
            if (token == null || !token.equals(configuration.getToken()))
            {
               halt(Status.FORBIDDEN);
            }
        });

        /*
        Array containing all supported version of API
        Made for backward compatibility if we do breaking change in the future
         */
        Version[] versions = new Version[]
        {
            new VersionOne(getServer(), GSON)
        };

        logger.info("Mapping all versions endpoints");
        for(Version version : versions)
        {
            logger.info("Mapping " + version.getRoute() + " endpoints");
            path(version.getRoute(), version::map);
        }
    }

    @Override
    public void onDisable()
    {
        stop();
    }
}
