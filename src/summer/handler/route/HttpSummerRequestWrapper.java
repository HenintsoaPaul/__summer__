package src.summer.handler.route;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Wrapper pour HttpServletRequest permettant de gérer la redirection en fonction d’une URL
 * au format "redirect:&lt;HTTP_METHOD&gt;:&lt;ROUTE&gt;".
 */
public final class HttpSummerRequestWrapper extends HttpServletRequestWrapper {

    private static final Set<String> VALID_METHODS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"))
    );

    private final String contextPath;
    private final String originalUrl;
    private String route;
    private String httpMethod;

    /**
     * Constructeur sans redirection.
     *
     * @param request la requête HTTP d’origine
     * @param url     l’URL à traiter
     */
    public HttpSummerRequestWrapper(HttpServletRequest request, String url) {
        this(request, url, false);
    }

    /**
     * Constructeur permettant de spécifier si l’URL doit être traitée comme une redirection.
     *
     * @param request       la requête HTTP d’origine
     * @param url           l’URL à traiter
     * @param isRedirection indique si l’URL correspond à une redirection
     */
    public HttpSummerRequestWrapper(HttpServletRequest request, String url, boolean isRedirection) {
        super(Objects.requireNonNull(request, "La requête ne peut pas être nulle"));
        this.contextPath = Objects.requireNonNull(request.getContextPath(), "Le contextPath ne peut pas être nul");
        this.originalUrl = Objects.requireNonNull(url, "L'URL ne peut pas être nulle");

        this.route = request.getRequestURI();
        this.httpMethod = request.getMethod();

        if (isRedirection) {
            parseRedirectionUrl();
        }
    }

    /**
     * Analyse l’URL de redirection et met à jour les attributs {@code httpMethod} et {@code route}.
     * Le format attendu est "redirect:&lt;HTTP_METHOD&gt;:&lt;ROUTE&gt;".
     *
     * @throws IllegalArgumentException si le format de l’URL est incorrect ou si la méthode HTTP est invalide.
     */
    private void parseRedirectionUrl() {
        // Format attendu : "redirect:<HTTP_METHOD>:<ROUTE>"
        if (!originalUrl.startsWith("redirect:")) {
            throw new IllegalArgumentException("URL invalide. Format attendu : redirect:<HTTP_METHOD>:<ROUTE>");
        }

        // On limite le découpage à 3 parties pour autoriser des deux-points dans la route.
        String[] parts = originalUrl.split(":", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Format d'URL incorrect");
        }

        this.httpMethod = parts[1].toUpperCase();
        if (!VALID_METHODS.contains(this.httpMethod)) {
            throw new IllegalArgumentException("Méthode HTTP invalide : " + this.httpMethod);
        }


        // Si la route extraite ne commence pas par le contextPath, on le préfixe.
        this.route = parts[2].startsWith(this.contextPath) ?
                parts[2] : this.contextPath + parts[2];
    }

    /**
     * Retourne la route de la requête.
     *
     * @return la route
     */
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
