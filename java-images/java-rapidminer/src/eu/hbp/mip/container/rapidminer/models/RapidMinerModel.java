package eu.hbp.mip.container.rapidminer.models;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rapidminer.RapidMiner;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRowFactory;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.example.table.ResultSetDataRowReader;
import com.rapidminer.operator.*;
import com.rapidminer.tools.Ontology;
import eu.hbp.mip.container.Task;
import eu.hbp.mip.container.db.exceptions.DBException;
import eu.hbp.mip.container.meta.NominalVariable;
import eu.hbp.mip.container.meta.Variable;
import com.fasterxml.jackson.core.JsonGenerator;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.learner.AbstractLearner;
import com.rapidminer.operator.learner.PredictionModel;
import com.rapidminer.tools.OperatorService;
import com.rapidminer.Process;

import eu.hbp.mip.container.models.exceptions.ModelExecutionException;
import eu.hbp.mip.container.rapidminer.exceptions.RapidMinerException;
import eu.hbp.mip.container.rapidminer.models.exceptions.RapidMinerModelExecutionException;


/**
 *
 * Wrapper around RapidMiner Learner and corresponding Model
 * This is the only models dependent to be subclassed when integrating new RapidMiner algorithms
 *
 * @author Arnaud Jutzeler
 *
 */
public abstract class RapidMinerModel<M extends PredictionModel> implements eu.hbp.mip.container.models.Model {

    private static boolean isRPMInit = false;

    private Class<? extends AbstractLearner> learnerClass;

    // Remark: This should be private, but we need the IOObject
    // to build the PFA representation for some models as they all have private fields...
    protected Process process;

    protected M trainedModel;

    public RapidMinerModel(Class<? extends AbstractLearner> learnerClass) {
        this.learnerClass = learnerClass;
    }

    public void run(Task task) throws ModelExecutionException {

        try {

            if(!isRPMInit) {
                initializeRPM();
            }

            if(this.isAlreadyTrained()) {
                System.out.println("This experiment was already run!");
                return;
            }

            // Train the model
            ExampleSet exampleSet = taskToExampleSet(task);
            this.train(exampleSet);

        } catch (OperatorCreationException | OperatorException | ClassCastException | DBException | RapidMinerException ex) {
            throw new RapidMinerModelExecutionException(ex);
        }
    }

    /**
     *
     * Extract ExampleSet from Task
     *
     * @return
     * @throws DBException
     */
    private static ExampleSet taskToExampleSet(Task task) throws DBException, RapidMinerException {

        ResultSet rs = task.getData();

        // Create attribute list
        ResultSetMetaData rsmd = null;
        List<Attribute> attributes = new ArrayList<>();
        try {
            rsmd = rs.getMetaData();
            for(int i = 1; i <= rsmd.getColumnCount(); i++) {
                String code = rsmd.getColumnName(i);
                int type = getType(task, code);
                attributes.add(AttributeFactory.createAttribute(code, type));
            }
        } catch(SQLException e) {
            throw new DBException(e);
        }

        // Create table
        MemoryExampleTable table = new MemoryExampleTable(attributes);

        ResultSetDataRowReader reader = new ResultSetDataRowReader(new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, '.'), attributes, rs);
        while(reader.hasNext()){
            table.addDataRow(reader.next());
        }

        // Create example set
        try {
            return table.createExampleSet(table.findAttribute(task.getVariableName()));
        }catch(OperatorException e){
            throw new RapidMinerException(e);
        }
    }

    /**
     *
     * @param code
     * @return
     */
    private static int getType(Task task, String code) {

        String type = task.getMetaData().get(code).getType();

        if (type.contains("nominal")) {
            return Ontology.NOMINAL;
        } else {
            return Ontology.REAL;
        }
    }

    /**
     *
     * @param code
     * @return
     */
    private static String[] getSymbols(Task task, String code) {

        String[] symbols = new String[]{};
        Variable variable = task.getMetaData().get(code);
        if (variable.getType().contains("nominal")) {
            symbols = ((NominalVariable) variable).getSymbols();
        }

        return symbols;
    }

    @SuppressWarnings("unchecked")
    public void train(ExampleSet exampleSet) throws OperatorCreationException, OperatorException {

        // Create the RapidMiner process
        process = new Process();

        // Model training
        Operator modelOp = OperatorService.createOperator(this.learnerClass);
        Map<String, String> parameters = getParameters();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            modelOp.setParameter(entry.getKey(), entry.getValue());
        }
        process.getRootOperator().getSubprocess(0).addOperator(modelOp);
        process.getRootOperator()
                .getSubprocess(0)
                .getInnerSources()
                .getPortByIndex(0)
                .connectTo(modelOp.getInputPorts().getPortByName("training set"));
        modelOp.getOutputPorts().getPortByName("model").connectTo(process.getRootOperator()
                .getSubprocess(0)
                .getInnerSinks()
                .getPortByIndex(0));

        // Run process
        IOContainer ioResult = process.run(new IOContainer(exampleSet, exampleSet, exampleSet));

        trainedModel = (M) ioResult.get(PredictionModel.class, 0);
    }

    protected abstract Map<String,String> getParameters();

    public String toRMP() {
        return process.getRootOperator().getXML(false);
    }

    public boolean isAlreadyTrained() {
        return trainedModel != null;
    }

    /**
     * Initialize RapidMiner
     * Must be run only once
     */
    private static void initializeRPM() {
        // Init RapidMiner
        System.setProperty("rapidminer.home", System.getProperty("user.dir"));

        RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.COMMAND_LINE);
        RapidMiner.init();
        isRPMInit= true;
    }

    /**
     * Connect the output-port <code>fromPortName</code> from Operator
     * <code>from</code> with the input-port <code>toPortName</code> of Operator
     * <code>to</code>.
     */
    private static void connect(Operator from, String fromPortName,
                                Operator to, String toPortName) {
        from.getOutputPorts().getPortByName(fromPortName).connectTo(
                to.getInputPorts().getPortByName(toPortName));
    }

    /**
     * Connect the output-port <code>fromPortName</code> from Subprocess
     * <code>from</code> with the input-port <code>toPortName</code> of Operator
     * <code>to</code>.
     */
    private static void connect(ExecutionUnit from, String fromPortName,
                                Operator to, String toPortName) {
        from.getInnerSources().getPortByName(fromPortName).connectTo(
                to.getInputPorts().getPortByName(toPortName));
    }

    /**
     * Connect the output-port <code>fromPortName</code> from Operator
     * <code>from</code> with the input-port <code>toPortName</code> of
     * Subprocess <code>to</code>.
     */
    private static void connect(Operator from, String fromPortName,
                                ExecutionUnit to, String toPortName) {
        from.getOutputPorts().getPortByName(fromPortName).connectTo(
                to.getInnerSinks().getPortByName(toPortName));
    }
}
