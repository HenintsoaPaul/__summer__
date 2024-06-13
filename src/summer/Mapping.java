package src.summer;

import java.lang.reflect.Method;

public class Mapping {
    String controllerName;
    Method method;

    public Mapping( String controllerName, Method method ) {
        this.setControllerName( controllerName );
        this.setMethod( method );
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName( String controllerName ) {
        this.controllerName = controllerName;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod( Method method ) {
        this.method = method;
    }
}
