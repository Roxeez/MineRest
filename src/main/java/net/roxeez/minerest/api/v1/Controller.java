package net.roxeez.minerest.api.v1;

import net.roxeez.minerest.api.v1.response.ErrorResponse;
import net.roxeez.minerest.http.ContentType;
import net.roxeez.minerest.http.Status;
import spark.Response;

public abstract class Controller
{
    public abstract String getRoute();
    public abstract void map();

    protected <T> T Ok(Response response, T object)
    {
        response.status(Status.OK);
        response.type(ContentType.APPLICATION_JSON);

        return object;
    }

    protected ErrorResponse NotFound(Response response, String message)
    {
        response.status(Status.NOT_FOUND);
        response.type(ContentType.APPLICATION_JSON);

        return new ErrorResponse(message);
    }

    protected ErrorResponse BadRequest(Response response, String message)
    {
        response.status(Status.BAD_REQUEST);
        response.type(ContentType.APPLICATION_JSON);

        return new ErrorResponse(message);
    }
}
