package net.roxeez.minerest.api.v1;

import com.google.gson.Gson;
import net.roxeez.minerest.api.Version;
import net.roxeez.minerest.api.v1.controller.PlayerController;
import net.roxeez.minerest.api.v1.controller.ServerController;
import org.bukkit.Server;

import static spark.Spark.path;

public class VersionOne implements Version
{
    private final Server server;
    private final Gson gson;

    public VersionOne(Server server, Gson gson)
    {
        this.server = server;
        this.gson = gson;
    }

    @Override
    public String getRoute()
    {
        return "/v1";
    }

    @Override
    public void map()
    {
        final Controller[] controllers = new Controller[]
        {
            new PlayerController(server, gson),
            new ServerController(server, gson)
        };

        for(Controller controller : controllers)
        {
            path(controller.getRoute(), controller::map);
        }
    }
}
