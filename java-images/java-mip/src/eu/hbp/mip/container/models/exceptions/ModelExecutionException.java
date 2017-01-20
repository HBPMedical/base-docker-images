package eu.hbp.mip.container.models.exceptions;

/**
 *
 * @author Arnaud Jutzeler
 */
public class ModelExecutionException extends Exception {

    private Exception parent;

    public ModelExecutionException(Exception parent) {
        this.parent = parent;
    }

    @Override
    public String getMessage() {
        return parent.getMessage();
    }
}
