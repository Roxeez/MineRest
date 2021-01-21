package net.roxeez.minerest;

import net.roxeez.minerest.api.Controller;
import net.roxeez.minerest.api.ControllerManager;
import net.roxeez.minerest.api.controller.PlayerController;
import net.roxeez.minerest.api.controller.ServerController;
import net.roxeez.minerest.api.controller.TokenController;
import net.roxeez.minerest.security.PermissionManager;
import net.roxeez.minerest.utility.LoggingUtility;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

import static spark.Spark.*;

public final class MineRest extends JavaPlugin
{
    private final Logger logger;
    private final PermissionManager permissionManager;
    private final ControllerManager controllerManager;

    public MineRest()
    {
        this.logger = getLogger();
        this.permissionManager = new PermissionManager(getLogger(), new File(getDataFolder(), "tokens.yml"));
        this.controllerManager = new ControllerManager(getLogger(), permissionManager);
    }

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        logger.info("Loading token manager");
        permissionManager.load();

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

        Controller[] controllers = new Controller[]
        {
            new PlayerController(getServer()),
            new ServerController(getServer()),
            new TokenController(permissionManager)
        };

        logger.info("Mapping built-in controllers");
        controllerManager.map(controllers);
    }

    @Override
    public void onDisable()
    {
        logger.info("Saving token manager");
        permissionManager.save();

        logger.info("Stopping http server running on port " + port());
        stop();
    }

    public ControllerManager getControllerManager()
    {
        return controllerManager;
    }
}
