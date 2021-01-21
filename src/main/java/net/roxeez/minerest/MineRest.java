package net.roxeez.minerest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.roxeez.minerest.api.Controller;
import net.roxeez.minerest.api.controller.PlayerController;
import net.roxeez.minerest.api.controller.ServerController;
import net.roxeez.minerest.http.Status;
import net.roxeez.minerest.security.Secured;
import net.roxeez.minerest.security.TokenManager;
import net.roxeez.minerest.utility.LoggingUtility;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static spark.Spark.*;

public final class MineRest extends JavaPlugin
{
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final Logger logger;
    private final TokenManager tokenManager;

    public MineRest()
    {
        this.logger = getLogger();
        this.tokenManager = new TokenManager(getLogger(), new File(getDataFolder(), "tokens.yml"));
    }

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        logger.info("Loading token manager");
        tokenManager.load();

        int port = getConfig().getInt("port", 5555);

        logger.info("Starting http server on port " + port);
        port(port);

        boolean debug = getConfig().getBoolean("debug");
        if (debug)
        {
            logger.info("Enabling request/response debugging");

            before((request, response) -> logger.info(LoggingUtility.format(request)));
            after((request, response) -> logger.info(LoggingUtility.format(response)));
        }

        path("/v1", () ->
        {
            Controller[] controllers = new Controller[]
            {
                new PlayerController(getServer(), GSON),
                new ServerController(getServer(), GSON)
            };

            logger.info("Mapping controllers");
            for(Controller controller : controllers)
            {
                boolean secured = controller.getClass().isAnnotationPresent(Secured.class);

                path(controller.getRoute(), () ->
                {
                    if (secured)
                    {
                        before((request, response) ->
                        {
                            String token = request.headers("X-Access-Token");
                            Set<String> permissions = tokenManager.getTokenPermissions(token);

                            if (!permissions.contains(controller.getRoute()))
                            {
                                halt(Status.FORBIDDEN);
                            }
                        });
                    }
                    controller.map(tokenManager);
                });
            }
        });
    }

    @Override
    public void onDisable()
    {
        logger.info("Saving token manager");
        tokenManager.save();

        logger.info("Stopping http server running on port " + port());
        stop();
    }
}
