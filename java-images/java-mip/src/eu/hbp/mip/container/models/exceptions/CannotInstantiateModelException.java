package eu.hbp.mip.container.models.exceptions;

/**
 *
 * @author Arnaud Jutzeler
 */
public class CannotInstantiateModelException extends Exception {

    private Exception parent;

    public CannotInstantiateModelException(Exception parent) {
        this.parent = parent;
    }

    @Override
    public String getMessage() {
        return parent.getMessage();
    }
}
