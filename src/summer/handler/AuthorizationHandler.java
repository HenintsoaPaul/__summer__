package src.summer.handler;

import src.summer.annotations.Authorized;
import src.summer.beans.authorization.AuthorizationData;
import src.summer.exception.SummerAuthorizationException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;


public class AuthorizationHandler {
    private final int NO_AUTHORIZATION_REQUIRED = -1;

    /**
     * Verify that userAuthenticated is true.
     * Verify that userRoleLevel >= requiredRoleLevel or requiredRoleLevel == 0.
     */
    public void handle( Method ctlMethod, ServletContext context, HttpSession session )
            throws SummerAuthorizationException {
        AuthorizationData authorizationData = new AuthorizationData( context, session );
        int requiredRoleLevel = this.getRequiredRoleLevel( ctlMethod );
        this.handle( requiredRoleLevel, authorizationData );
    }

    private void handle( int requiredRoleLevel, AuthorizationData authorizationData )
            throws SummerAuthorizationException {
        if ( requiredRoleLevel == this.NO_AUTHORIZATION_REQUIRED ) return;

        if ( !authorizationData.isUserAuthenticated() ) {
            throw new SummerAuthorizationException( "User is not authenticated." );
        }

        if ( authorizationData.getUserRoleLevel() < 0 ) {
            throw new SummerAuthorizationException( "Role level must be >= 0." );
        }

        // No Authorization, just Authentication only
        if ( requiredRoleLevel == 0 ) return;

        if ( requiredRoleLevel < 0 ) {
            throw new SummerAuthorizationException( "Required role level must be >= 0." );
        }

        if ( requiredRoleLevel > authorizationData.getUserRoleLevel() ) {
            throw new SummerAuthorizationException( "Not enough role level to process your request." );
        }
    }

    /**
     *  Check then return the requiredRoleLevel for a specific Method. If the method is not annotated with @Authorized,
     *  it returns a default value of -1 to say that the route need no authorization.
     *
     * @param method Controller method to be verified
     */
    public int getRequiredRoleLevel( Method method ) {
        if ( method.isAnnotationPresent( Authorized.class ) ) {
            return method.getAnnotation( Authorized.class ).roleLevel();
        }
        return this.NO_AUTHORIZATION_REQUIRED;
    }
}
