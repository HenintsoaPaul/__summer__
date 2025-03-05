package src.summer.handler.route;

import src.summer.exception.route.SummerRoutingException;
import src.summer.handler.HttpSummerRequestWrapper;

public class RouterFacade {

    public String getRoute(HttpSummerRequestWrapper request) throws SummerRoutingException {
        // extraire la route du request
        String route = RouterUtil.getRoute(request.getRequestURI());

        // gerer les parametres envoyees par url
        route = RouterUtil.gererParametreDansRoute(route, request);

        return route;
    }
}
