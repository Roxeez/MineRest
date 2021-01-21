package net.roxeez.minerest.api;

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
                if (secured)
                {
                    Spark.before(get.path(), ((request, response) ->
                    {
                        String token = request.headers("X-Access-Token");
                        Set<String> permissions = manager.getTokenPermissions(token);

                        if (!permissions.contains(get.path()))
                        {
                            halt(Status.FORBIDDEN);
                        }
                    }));
                }

                Spark.get(get.path(), (request, response) ->
                {
                    response.type(get.type().toString());
                    return method.invoke(this, request, response);
                });
            }

            POST post = method.getAnnotation(POST.class);
            if (post != null)
            {
                if (secured)
                {
                    Spark.before(post.path(), ((request, response) ->
                    {
                        String token = request.headers("X-Access-Token");
                        Set<String> permissions = manager.getTokenPermissions(token);

                        if (!permissions.contains(post.path()))
                        {
                            halt(Status.FORBIDDEN);
                        }
                    }));
                }

                Spark.post(post.path(), post.requiredType().toString(), (request, response) ->
                {
                    response.type(post.type().toString());
                    return method.invoke(this, request, response);
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
