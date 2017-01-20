package eu.hbp.mip.container.models;

import eu.hbp.mip.container.Task;
import eu.hbp.mip.container.models.exceptions.*;

/**
 *
 *
 * @author Arnaud Jutzeler
 */
public interface Model {

    /**
     * To be overriden to train a predictive model, run clustering, ...
     */
    void run(Task task) throws ModelExecutionException;

    /**
     *
     * Generates trained model's parameters in PFA
     *
     * By default: '{}'
     *
     * @return JSON dictionary
     */
    String getParamsCellPFA() throws ModelNotTrainedYetException;

    /**
     *
     * Generates the PFA's code for model's prediction based on internal representation (given by getModelRepPFA())
     *
     * By default: '{}'
     *
     * @return JSON dictionary
     */
    String getActionPFA() throws ModelNotTrainedYetException;


    /**
     *
     * Additional functions to be written in cells.fncs in the output PFA document
     *
     * By default: '{}'
     *
     * @return JSON dictionary
     */
    String getFcnsPFA() throws ModelNotTrainedYetException;
}
