package src.summer.exception.process;

public class NoRouteForUrlException extends SummerRoutingException {
    private final String url;

    public NoRouteForUrlException( String url ) {
        super();
        this.url = url;
    }

    @Override
    public String getMessage() {
        return "No route for URL: \"" + this.url + "\".";
    }
}
