package src.summer.utils;

import src.summer.beans.ModelView;

public abstract class ModelViewUtil {
    /**
     * Verify whether an object is an instance of ModelView class.
     */
    public static boolean isInstance( Object instance ) {
        String className = instance.getClass().getName();
        return className.equals( ModelView.class.getName() );
    }
}
