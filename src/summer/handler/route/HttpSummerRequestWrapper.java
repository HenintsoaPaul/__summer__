package src.summer.handler.route;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpSummerRequestWrapper extends HttpServletRequestWrapper {

    private String contextPath;
    private String url;
    private String route;
    private String httpMethod;

    public HttpSummerRequestWrapper(HttpServletRequest request, String url) {
        super(request);
        this.contextPath = request.getContextPath();
        this.url = url;

        this.route = request.getRequestURI();
        this.httpMethod = request.getMethod();
    }

    public HttpSummerRequestWrapper(HttpServletRequest request, String url, boolean isRedirection) {
        super(request);
        this.contextPath = request.getContextPath();
        this.url = url;

        this.route = request.getRequestURI();
        this.httpMethod = request.getMethod();

        if (isRedirection) {
            parseRedirectionUrl();
        }
    }

    private void parseRedirectionUrl() {
        // Format attendu : "redirect:<HTTP_METHOD>:<ROUTE>"
        if (url == null || !url.startsWith("redirect:")) {
            throw new IllegalArgumentException("URL invalide. Format attendu : redirect:<HTTP_METHOD>:<ROUTE>");
        }

        String[] parts = url.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Format d'URL incorrect");
        }

        System.out.println("contextpath: " + contextPath);

        this.httpMethod = parts[1].toUpperCase();
        this.route = parts[2].startsWith(this.contextPath) ?
                parts[2] :
                this.contextPath + parts[2];

        // Validation du méthode HTTP
        String[] validMethods = {"GET", "POST", "PUT", "DELETE", "PATCH"};
        boolean isValidMethod = false;
        for (String method : validMethods) {
            if (method.equals(this.httpMethod)) {
                isValidMethod = true;
                break;
            }
        }
        if (!isValidMethod) {
            throw new IllegalArgumentException("Méthode HTTP invalide : " + this.httpMethod);
        }
    }

    public String getRoute() {
        return route;
    }

    @Override
    public String getRequestURI() {
        return route;
    }

    @Override
    public String getMethod() {
        return httpMethod;
    }
}