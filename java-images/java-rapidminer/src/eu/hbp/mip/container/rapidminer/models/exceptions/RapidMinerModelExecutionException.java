package eu.hbp.mip.container.rapidminer.models.exceptions;

import eu.hbp.mip.container.models.exceptions.ModelExecutionException;

/**
 *
 * @author Arnaud Jutzeler
 */
public class RapidMinerModelExecutionException extends ModelExecutionException {

    public RapidMinerModelExecutionException(Exception parent) {
        super(parent);
    }
}
