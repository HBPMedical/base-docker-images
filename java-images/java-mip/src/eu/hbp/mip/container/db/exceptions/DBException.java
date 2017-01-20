package eu.hbp.mip.container.db.exceptions;

/**
 *
 * @author Arnaud Jutzeler
 */
public class DBException extends Exception {

    private Exception parent;

    public DBException(Exception parent) {
        this.parent = parent;
    }

    @Override
    public String getMessage() {
        return parent.getMessage();
    }
}
