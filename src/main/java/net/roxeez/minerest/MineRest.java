package net.roxeez.minerest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.roxeez.minerest.api.Controller;
import net.roxeez.minerest.api.controller.PlayerController;
import net.roxeez.minerest.api.controller.ServerController;
import net.roxeez.minerest.http.Status;
import net.roxeez.minerest.utility.LoggingUtility;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static spark.Spark.*;

public final class MineRest extends JavaPlugin
{
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final Logger logger = getLogger();

    @Override
    public void onEnable()
    {
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

            before((request, response) -> logger.info(LoggingUtility.format(request)));
            after((request, response) -> logger.info(LoggingUtility.format(response)));
        }

        path("/v1", () ->
        {
            // Restrict access with token
            before((request, response) ->
            {
               String token = request.headers("X-Access-Token");
               if (token == null || !token.equals(configuration.getToken()))
               {
                   halt(Status.FORBIDDEN);
               }
            });

            Controller[] controllers = new Controller[]
            {
                new PlayerController(getServer(), GSON),
                new ServerController(getServer(), GSON)
            };

            logger.info("Mapping controllers");
            for(Controller controller : controllers)
            {
                path(controller.getRoute(), controller::map);
            }
        });
    }

    @Override
    public void onDisable()
    {
        logger.info("Stopping http server running on port " + port());
        stop();
    }
}
