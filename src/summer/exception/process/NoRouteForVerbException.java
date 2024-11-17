package src.summer.exception.process;

public class NoRouteForVerbException extends SummerRoutingException {
    private final String url;
    private final String httpVerb;

    public NoRouteForVerbException( String url, String httpVerb ) {
        super();
        this.url = url;
        this.httpVerb = httpVerb;
    }

    @Override
    public String getMessage() {
        return "No httpVerb \"" + httpVerb + "\" for this URL: \"" + this.url + "\".";
    }
}
