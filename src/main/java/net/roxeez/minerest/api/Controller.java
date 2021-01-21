package net.roxeez.minerest.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.roxeez.minerest.api.response.ErrorResponse;
import net.roxeez.minerest.http.GET;
import net.roxeez.minerest.http.POST;
import net.roxeez.minerest.security.Secured;
import net.roxeez.minerest.http.Status;
import net.roxeez.minerest.security.TokenManager;
import spark.Response;
import spark.Spark;

import java.lang.reflect.Method;
import java.util.Set;

import static spark.Spark.halt;

public abstract class Controller
{
    protected static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public abstract String getRoute();

    public void map(TokenManager manager)
    {
        Method[] methods = getClass().getDeclaredMethods();
        for(Method method : methods)
        {
            boolean secured = method.isAnnotationPresent(Secured.class);

            GET get = method.getAnnotation(GET.class);
            if (get != null)
            {
                method.setAccessible(true);

                if (secured)
                {
                    Spark.before(get.path(), ((request, response) ->
                    {
                        String token = request.headers("X-Access-Token");
                        Set<String> permissions = manager.getTokenPermissions(token);

                        if (!permissions.contains(getRoute() + get.path()))
                        {
                            halt(Status.FORBIDDEN);
                        }
                    }));
                }

                Spark.get(get.path(), (request, response) ->
                {
                    response.type(get.type().toString());

                    Object object = method.invoke(this, request, response);
                    switch (get.type())
                    {
                        case APPLICATION_JSON:
                            return GSON.toJson(object);
                        default:
                            return object;
                    }
                });
            }

            POST post = method.getAnnotation(POST.class);
            if (post != null)
            {
                method.setAccessible(true);

                if (secured)
                {
                    Spark.before(post.path(), ((request, response) ->
                    {
                        String token = request.headers("X-Access-Token");
                        Set<String> permissions = manager.getTokenPermissions(token);

                        if (!permissions.contains(getRoute() + post.path()))
                        {
                            halt(Status.FORBIDDEN);
                        }
                    }));
                }

                Spark.post(post.path(), post.requiredType().toString(), (request, response) ->
                {
                    response.type(post.type().toString());

                    Object object = method.invoke(this, request, response);
                    switch (post.type())
                    {
                        case APPLICATION_JSON:
                            return GSON.toJson(object);
                        default:
                            return object;
                    }
                });
            }
        }
    }

    protected <T> T Ok(Response response, T object)
    {
        response.status(Status.OK);

        return object;
    }

    protected ErrorResponse NotFound(Response response, String message)
    {
        response.status(Status.NOT_FOUND);

        return new ErrorResponse(message);
    }

    protected ErrorResponse BadRequest(Response response, String message)
    {
        response.status(Status.BAD_REQUEST);

        return new ErrorResponse(message);
    }
}
