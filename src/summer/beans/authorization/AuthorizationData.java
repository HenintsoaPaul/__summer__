package src.summer.beans.authorization;

import src.summer.exception.SummerAuthorizationException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

public class AuthorizationData {
    private boolean userAuthenticated = false;
    private int userRoleLevel = 0;

    /**
     * Get the name of authorization data stored in the web.xml using the {@code context} object of the
     * {@code FrontServlet}. Then, it takes the values of those authorization data from the current {@code session}.
     * If {@code session} is null, an exception is thrown. If no values are found in the {@code session}, it will use
     * default values.
     *
     * @param context
     * @param session
     * @throws SummerAuthorizationException If {@code session} parameter is null
     */
    public AuthorizationData( ServletContext context, HttpSession session )
            throws SummerAuthorizationException {
        String userAuthenticatedVarName = context.getInitParameter( "var_user_authenticated" ),
                userRoleLevelVarName = context.getInitParameter( "var_user_role_level" );

        if ( session == null ) {
            throw new SummerAuthorizationException( "Session is null" );
        }

        Object objAuth = session.getAttribute( userAuthenticatedVarName ),
                objRoleLevel = session.getAttribute( userRoleLevelVarName );

        boolean auth = objAuth == null ? false : ( boolean ) objAuth ;
        int roleLevel = objRoleLevel == null ? 0 : ( int ) objRoleLevel;

        this.setUserAuthenticated( auth );
        this.setUserRoleLevel( roleLevel );
    }

    // Getters n Setters
    public boolean isUserAuthenticated() {
        return userAuthenticated;
    }

    private void setUserAuthenticated( boolean userAuthenticated ) {
        this.userAuthenticated = userAuthenticated;
    }

    public int getUserRoleLevel() {
        return userRoleLevel;
    }

    private void setUserRoleLevel( int userRoleLevel )
            throws SummerAuthorizationException {
        if ( userRoleLevel < 0 ) {
            throw new SummerAuthorizationException( "User role level must be >= 0." );
        }

        this.userRoleLevel = userRoleLevel;
    }
}
