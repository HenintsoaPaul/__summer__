package src.summer;

public class Mapping {
    String controllerName;
    String methodName;

    public Mapping( String controllerName, String methodName ) {
        this.setControllerName( controllerName );
        this.setMethodName( methodName );
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName( String controllerName ) {
        this.controllerName = controllerName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName( String methodName ) {
        this.methodName = methodName;
    }
}
