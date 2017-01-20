package eu.hbp.mip.container.rapidminer.tests.models;

import java.util.Map;

import eu.hbp.mip.container.models.exceptions.ModelNotTrainedYetException;
import org.apache.commons.collections15.map.LinkedMap;

import com.rapidminer.operator.learner.lazy.DefaultLearner;
import com.rapidminer.operator.learner.lazy.DefaultModel;

import eu.hbp.mip.container.rapidminer.models.RapidMinerModel;


/**
 *
 * Trivial RapidMiner model 'DefautModel' for testing purpose only!
 *
 * @author Arnaud Jutzeler
 *
 */
public class RPMDefault extends RapidMinerModel<DefaultModel> {

    private String method;

    public RPMDefault() {
        this(System.getProperty("PARAM_MODEL_method", System.getenv("PARAM_MODEL_method")));
    }

    public RPMDefault(String method) {
        super(DefaultLearner.class);
        this.method = method;
    }

    @Override
    public Map<String, String> getParameters() {
        LinkedMap map = new LinkedMap<String, String>();
        map.put("method", this.method);
        return map;
    }

    @Override
    public String getParamsCellPFA() throws ModelNotTrainedYetException {
        if (!isAlreadyTrained()) {
            throw new ModelNotTrainedYetException(this);
        }

        String params =
                "{" +
                "   'model': {" +
                "      'type': {" +
                "         'doc': 'The model parameter'" +
                "         'name': 'value'" +
                "         'type': 'double'" +
                "      }" +
                "   'init': {" +
                "      'value': '" + trainedModel.getValue() + "'" +
                "   }" +
                "}";

        return params;
    }

    @Override
    public String getActionPFA() throws ModelNotTrainedYetException {
        String action =
                "{" +
                "   'cell': 'param'" +
                "}";

        return action;
    }

    @Override
    public  String getFcnsPFA() throws ModelNotTrainedYetException {
        return "{}";
    }
}