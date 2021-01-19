package net.roxeez.minerest.utility;

import spark.Request;
import spark.Response;

public final class LoggingUtility
{
    private LoggingUtility()
    {
        
    }

    public static String format(Request request)
    {
        return "REQUEST | "
                + request.protocol() + " | "
                + request.requestMethod() + " | "
                + request.uri()
                + (request.body() != null && !request.body().equals("") ? "\r\n" + request.body() : "")
                .trim();
    }

    public static String format(Response response)
    {
        return "RESPONSE | "
                + response.status() + " | "
                + (response.type() != null ? response.type() : "text/html")
                + (response.body() != null && !response.body().equals("") ? "\r\n" + response.body() : "")
                .trim();
    }
}
