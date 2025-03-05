package src.summer.beans;

import src.summer.exception.scan.mapping.NullVerbActionException;

import java.lang.reflect.Method;

public class VerbActionVaovao {

    String controllerName;
    String verb;
    Method action;

    public VerbActionVaovao(
            String controllerName,
            String verb,
            Method action
    )
            throws NullVerbActionException
    {
        this.setControllerName(controllerName);
        this.setVerb(verb);
        this.setAction(action);
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb)
            throws NullVerbActionException {
        if (verb == null || verb.isEmpty()) {
            throw new NullVerbActionException("Verb cannot be null or empty.");
        }
        this.verb = verb;
    }

    public Method getAction() {
        return action;
    }

    public void setAction(Method action)
            throws NullVerbActionException {
        if (action == null) {
            throw new NullVerbActionException("Method cannot be null.");
        }
        this.action = action;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) throws NullVerbActionException {
        if (controllerName == null || controllerName.isEmpty()) {
            throw new NullVerbActionException("ControllerName cannot be null or empty.");
        }
        this.controllerName = controllerName;
    }
}
