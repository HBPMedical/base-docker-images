package eu.hbp.mip.container.pfa;

import java.io.*;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import eu.hbp.mip.container.Task;
import eu.hbp.mip.container.Result;
import eu.hbp.mip.container.models.Model;
import eu.hbp.mip.container.models.exceptions.ModelNotTrainedYetException;
import eu.hbp.mip.container.pfa.exceptions.PFAException;

/**
 *
 * @author Arnaud Jutzeler
 */
public class PFASerializer {

    final static private String TEMPLATE_FILE = "result.json-tmpl";

    public PFASerializer() {}

    /**
     * Use Mustache templating based on 'result.json-tmpl' json file
     *
     * @param result
     * @return
     */
    public String generatePFA(Result result) throws PFAException {

        try {
            // Context
            Object context = new Object () {

                // Metadata
                public Object input = generatePFAContextFromTask(result.getTask());
                public String error = result.getException().getMessage();

                // Model
                Model model = result.getModel();
                public String paramsCellPFA = model.getParamsCellPFA();
                public String fcnsPFA = model.getFcnsPFA();
                public String actionPFA = model.getActionPFA();
            };

            Writer writer = new StringWriter();
            MustacheFactory mf = new DefaultMustacheFactory();

            Mustache mustache = mf.compile(new BufferedReader(new FileReader("pfa/" + TEMPLATE_FILE)), "result");
            mustache.execute(writer, context);
            writer.flush();
            return writer.toString();
        } catch (IOException | ModelNotTrainedYetException e) {
            throw new PFAException(e);
        }
    }

    //TODO
    private static Object generatePFAContextFromTask(Task task) {
        return new Object(){};
    }

}
