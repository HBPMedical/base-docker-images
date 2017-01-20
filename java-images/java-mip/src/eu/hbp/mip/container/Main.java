package eu.hbp.mip.container;

import eu.hbp.mip.container.db.DBConnector;
import eu.hbp.mip.container.db.exceptions.DBException;
import eu.hbp.mip.container.meta.exceptions.MetaDataException;
import eu.hbp.mip.container.models.exceptions.*;
import eu.hbp.mip.container.pfa.exceptions.PFAException;

/**
 *
 * Entrypoint
 *
 * @author Arnaud Jutzeler
 *
 */
public class Main {

    public static void main(String[] args) {

        try {

            // Get task from environment variables
            Task task = Task.fromEnv(args);

            // Run the task
            Result result = task.run();

            // Write results PFA in DB
            String pfa = result.toPFA();
            DBConnector.saveResults(pfa);

        } catch (DBException | ClassCastException | MetaDataException |
                CannotInstantiateModelException | ModelExecutionException | PFAException e) {
            e.printStackTrace();
        }
    }
}