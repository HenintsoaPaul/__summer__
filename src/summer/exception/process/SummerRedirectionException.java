package src.summer.exception.process;

public class SummerRedirectionException extends SummerRoutingException {
    private final String url;

    public SummerRedirectionException(String url ) {
        super();
        this.url = url;
    }

    @Override
    public String getMessage() {
        return "No route for URL: \"" + this.url + "\".";
    }
}
