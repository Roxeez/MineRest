package net.roxeez.minerest.http;

public enum ContentType
{
    APPLICATION_JSON("application/json"),
    TEXT_HTML("text/html");

    private final String value;

    ContentType(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
