package net.roxeez.minerest.api.controller;

import com.google.gson.Gson;
import net.roxeez.minerest.api.Controller;
import net.roxeez.minerest.api.request.CreateTokenRequest;
import net.roxeez.minerest.api.response.CreateTokenResponse;
import net.roxeez.minerest.http.ContentType;
import net.roxeez.minerest.http.POST;
import net.roxeez.minerest.security.Secured;
import net.roxeez.minerest.security.TokenManager;
import spark.Request;
import spark.Response;

import java.util.UUID;

public class TokenController extends Controller
{
    private final TokenManager manager;
    private final Gson gson;

    public TokenController(TokenManager manager, Gson gson)
    {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public String getRoute()
    {
        return "/token";
    }

    @Secured
    @POST(path = "/create", requiredType = ContentType.APPLICATION_JSON, type = ContentType.APPLICATION_JSON)
    private Object create(Request request, Response response)
    {
        CreateTokenRequest input = gson.fromJson(request.body(), CreateTokenRequest.class);
        if (input == null)
        {
            return BadRequest(response, "Unable to parse request");
        }

        String token = UUID.randomUUID().toString();
        manager.addToken(token, input.permissions);

        CreateTokenResponse output = CreateTokenResponse.builder()
                .token(token)
                .build();

        return Ok(response, output);
    }
}
