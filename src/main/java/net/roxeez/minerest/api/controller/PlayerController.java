package net.roxeez.minerest.api.v1.controller;

import com.google.gson.Gson;
import net.roxeez.minerest.api.v1.Controller;
import net.roxeez.minerest.api.v1.request.KickRequest;
import net.roxeez.minerest.api.v1.response.KickResponse;
import net.roxeez.minerest.api.v1.response.PlayerResponse;
import net.roxeez.minerest.http.ContentType;
import net.roxeez.minerest.utility.StringUtility;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import spark.Request;
import spark.Response;

import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;

public class PlayerController extends Controller
{
    private final Server server;
    private final Gson gson;

    public PlayerController(Server server, Gson gson)
    {
        this.server = server;
        this.gson = gson;
    }

    @Override
    public String getRoute()
    {
        return "/player";
    }

    @Override
    public void map()
    {
        get("/by-name/:name", this::getByName, gson::toJson);
        get("/by-id/:id", this::getById, gson::toJson);

        post("/kick", ContentType.APPLICATION_JSON, this::kick, gson::toJson);
    }

    public Object getByName(Request request, Response response)
    {
        String name = request.params(":name");
        if (name == null)
        {
            return BadRequest(response, "Can't found parameter 'name'");
        }

        OfflinePlayer player = server.getOfflinePlayer(name);

        PlayerResponse output = PlayerResponse.builder()
                .uuid(player.getUniqueId())
                .name(player.getName())
                .build();

        return Ok(response, output);
    }

    private Object getById(Request request, Response response)
    {
        String id = request.params(":id");
        if (id == null)
        {
            return BadRequest(response, "Can't found parameter 'name'");
        }

        UUID uniqueId = StringUtility.safeParseUUID(id);
        if (uniqueId == null)
        {
            return BadRequest(response, "Can't parse id " + id + " to UUID");
        }

        OfflinePlayer player = server.getOfflinePlayer(uniqueId);

        PlayerResponse output = PlayerResponse.builder()
                .uuid(player.getUniqueId())
                .name(player.getName())
                .build();

        return Ok(response, output);
    }

    private Object kick(Request request, Response response)
    {
        KickRequest input = gson.fromJson(request.body(), KickRequest.class);
        if (input == null)
        {
            return BadRequest(response, "Unable to parse request");
        }

        Player player = server.getPlayer(input.name);
        if (player == null)
        {
            return NotFound(response, "Can't found player with name " + input.name);
        }

        String reason = input.reason == null ? "No reason specified." : input.reason;

        player.kickPlayer(reason);

        KickResponse output = KickResponse.builder()
                .playerId(player.getUniqueId())
                .reason(reason)
                .build();

        return Ok(response, output);
    }
}
