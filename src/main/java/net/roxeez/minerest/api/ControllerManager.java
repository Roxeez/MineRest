package net.roxeez.minerest.api;

import net.roxeez.minerest.http.Status;
import net.roxeez.minerest.security.Secured;
import net.roxeez.minerest.security.PermissionManager;

import java.util.Set;
import java.util.logging.Logger;

import static spark.Spark.*;

public class ControllerManager
{
    private final Logger logger;
    private final PermissionManager permissionManager;

    public ControllerManager(Logger logger, PermissionManager manager)
    {
        this.logger = logger;
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

                logger.info("Mapping " + controller.getRoute() + " controller");
                controller.map(logger, permissionManager);
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
