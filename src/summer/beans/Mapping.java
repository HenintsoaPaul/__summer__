package src.summer.beans;

import java.lang.reflect.Method;

public class Mapping {
    String controllerName, urlVerb;
    Method method;

    public Mapping( String controllerName, String urlVerb, Method method ) {
        this.setControllerName( controllerName );
        this.setUrlVerb( urlVerb );
        this.setMethod( method );
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName( String controllerName ) {
        this.controllerName = controllerName;
    }

    public String getUrlVerb() {
        return urlVerb;
    }

    public void setUrlVerb( String urlVerb ) {
        this.urlVerb = urlVerb;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod( Method method ) {
        this.method = method;
    }
}
