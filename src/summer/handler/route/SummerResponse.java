package src.summer.handler.route;

import src.summer.beans.ModelView;

public class SummerResponse {

    private final ModelView modelView;

    /**
     * Can be a simple String or a JSON String
     */
    private final String stringResponse;

    public SummerResponse(ModelView modelView, String stringResponse) {
        this.modelView = modelView;
        this.stringResponse = stringResponse;
    }

    // get
    public ModelView getModelView() {
        return modelView;
    }

    public String getStringResponse() {
        return stringResponse;
    }
}
