package eu.hbp.mip.container;

import eu.hbp.mip.container.models.Model;
import eu.hbp.mip.container.pfa.PFASerializer;
import eu.hbp.mip.container.pfa.exceptions.PFAException;

/**
 *
 *
 * @author Arnaud Jutzeler
 */
public class Result {

    protected Task task;
    protected Model model;
    protected Exception exception;

    public Result(Task task, Model model) {
        this.task = task;
        this.model = model;
        this.exception = null;
    }

    public Result(Task task, Exception exception) {
        this.task = task;
        this.exception = exception;
        this.model = null;
    }

    /**
     * Generate the PFA representation of the experiment outcome
     *
     * @return
     * @throws PFAException
     */
    public String toPFA() throws PFAException {
        PFASerializer serializer = new PFASerializer();
        return serializer.generatePFA(this);
    }

    /**
     *
     * @return
     */
    public Task getTask() {
        return task;
    }

    /**
     *
     * @return
     */
    public Model getModel() {
        return model;
    }

    /**
     *
     * @return
     */
    public Exception getException() {
        return exception;
    }
}
