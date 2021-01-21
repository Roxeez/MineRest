package net.roxeez.minerest.api;

import net.roxeez.minerest.http.Status;
import net.roxeez.minerest.security.Secured;
import net.roxeez.minerest.security.PermissionManager;

import java.util.Set;

import static spark.Spark.*;

public class ControllerManager
{
    private final PermissionManager permissionManager;

    public ControllerManager(PermissionManager manager)
    {
        this.permissionManager = manager;
    }

    public void map(Controller controller)
    {
        path("/v1", () ->
        {
            Secured secured = controller.getClass().getAnnotation(Secured.class);

            path(controller.getRoute(), () ->
            {
                if (secured != null)
                {
                    before((request, response) ->
                    {
                        String token = request.headers("X-Access-Token");
                        if (token == null)
                        {
                            halt(Status.FORBIDDEN);
                        }

                        boolean allowed = permissionManager.hasPermission(token, secured.value());
                        if (!allowed)
                        {
                            halt(Status.FORBIDDEN);
                        }
                    });
                }

                controller.map(permissionManager);
            });
        });
    }

    public void map(Controller... controllers)
    {
        for(Controller controller : controllers)
        {
            map(controller);
        }
    }
}
