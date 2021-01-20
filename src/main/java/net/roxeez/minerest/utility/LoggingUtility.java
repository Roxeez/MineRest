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
                + (request.body() != null && !request.body().equals("") ? System.lineSeparator() : "")
                + (request.body() != null && !request.body().equals("") ? request.body() : "")
                .trim();
    }

    public static String format(Response response)
    {
        return "RESPONSE | "
                + response.status() + " | "
                + (response.type() != null ? response.type() : "text/html")
                + (response.body() != null && !response.body().equals("") ? System.lineSeparator() : "")
                + (response.body() != null && !response.body().equals("") ? response.body() : "")
                .trim();
    }
}
