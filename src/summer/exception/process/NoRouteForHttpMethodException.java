package src.summer.exception.process;

public class NoRouteForHttpMethodException extends SummerRoutingException {
    private final String url;
    private final String httpVerb;

    public NoRouteForHttpMethodException(String url, String httpVerb ) {
        super();
        this.url = url;
        this.httpVerb = httpVerb;
    }

    @Override
    public String getMessage() {
        return "No httpMethod \"" + httpVerb + "\" for URL: \"" + this.url + "\".";
    }
}
