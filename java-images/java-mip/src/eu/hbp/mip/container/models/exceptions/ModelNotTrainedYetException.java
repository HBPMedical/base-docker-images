package eu.hbp.mip.container.models.exceptions;

import eu.hbp.mip.container.models.Model;

/**
 *
 * @author Arnaud Jutzeler
 */
public class ModelNotTrainedYetException extends Exception {

    private Model model;

    public ModelNotTrainedYetException(Model model) {

    }

    @Override
    public String getMessage() {
        return model.getClass().getName() + "has not been trained yet!";
    }
}
