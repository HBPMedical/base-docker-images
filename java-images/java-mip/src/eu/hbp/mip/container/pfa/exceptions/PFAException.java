package eu.hbp.mip.container.pfa.exceptions;

/**
 *
 * @author Arnaud Jutzeler
 */
public class PFAException extends Exception {

    private Exception parent;

    public PFAException(Exception parent) {
        this.parent = parent;
    }

    @Override
    public String getMessage() {
        return parent.getMessage();
    }
}

