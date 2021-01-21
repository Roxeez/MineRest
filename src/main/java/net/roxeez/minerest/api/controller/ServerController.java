package net.roxeez.minerest.api.controller;

import com.google.gson.Gson;
import net.roxeez.minerest.api.Controller;
import net.roxeez.minerest.api.request.BroadcastRequest;
import net.roxeez.minerest.api.response.BroadcastResponse;
import net.roxeez.minerest.api.response.object.PluginObject;
import net.roxeez.minerest.api.response.ServerResponse;
import net.roxeez.minerest.http.ContentType;
import net.roxeez.minerest.http.GET;
import net.roxeez.minerest.http.POST;
import net.roxeez.minerest.security.Secured;
import org.bukkit.Server;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerController extends Controller
{
    private final Server server;
    private final Gson gson;

    public ServerController(Server server, Gson gson)
    {
        this.server = server;
        this.gson = gson;
    }

    @Override
    public String getRoute()
    {
        return "/server";
    }

    @GET(type = ContentType.APPLICATION_JSON)
    private Object getServer(Request request, Response response)
    {
        List<PluginObject> plugins = Arrays.stream(server.getPluginManager().getPlugins())
                .map(x -> PluginObject.builder()
                        .name(x.getName())
                        .version(x.getDescription().getVersion())
                        .build())
                .collect(Collectors.toList());

        ServerResponse output = ServerResponse.builder()
                .motd(server.getMotd())
                .version(server.getVersion())
                .bukkitVersion(server.getBukkitVersion())
                .players(server.getOnlinePlayers().size())
                .maxPlayers(server.getMaxPlayers())
                .plugins(plugins)
                .build();

        return Ok(response, output);
    }

    @Secured
    @POST(path = "/broadcast", type = ContentType.APPLICATION_JSON, requiredType = ContentType.APPLICATION_JSON)
    private Object broadcast(Request request, Response response)
    {
        BroadcastRequest input = gson.fromJson(request.body(), BroadcastRequest.class);
        if (input == null)
        {
            return BadRequest(response, "Unable to parse request");
        }

        int count = server.broadcast(input.message, input.permission != null ? input.permission : Server.BROADCAST_CHANNEL_USERS);

        BroadcastResponse output = BroadcastResponse.builder()
                .count(count)
                .build();

        return Ok(response, output);
    }

    @GET(path = "/plugins", type = ContentType.APPLICATION_JSON)
    private Object getPlugins(Request request, Response response)
    {
        List<PluginObject> plugins = Arrays.stream(server.getPluginManager().getPlugins())
                .map(x -> PluginObject.builder()
                        .name(x.getName())
                        .version(x.getDescription().getVersion())
                        .build())
                .collect(Collectors.toList());

        return Ok(response, plugins);
    }
}
