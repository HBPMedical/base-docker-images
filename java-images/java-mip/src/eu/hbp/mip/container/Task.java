package eu.hbp.mip.container;

import eu.hbp.mip.container.db.DBConnector;
import eu.hbp.mip.container.db.exceptions.DBException;
import eu.hbp.mip.container.meta.exceptions.MetaDataException;

import java.sql.ResultSet;
import java.sql.SQLException;

import eu.hbp.mip.container.meta.Variables;
import eu.hbp.mip.container.models.Model;
import eu.hbp.mip.container.models.exceptions.*;


/**
 *
 * @author Arnaud Jutzeler
 *
 */
public class Task {

	protected Variables metaData;
	protected String[] featuresNames;
	protected String variableName;
	protected String query;
	protected Model model;

	protected transient ResultSet data;

	protected Task() {}

	public Task(String[] featuresNames,
				String variableName,
				String query,
				String metaDataString,
				String modelClassName) throws DBException, MetaDataException, CannotInstantiateModelException {

		this.featuresNames = featuresNames;
		this.variableName = variableName;
		this.query = query;
		this.metaData = new Variables(metaDataString);
		this.data = queryData();

		try {
			Class modelClass = Class.forName(modelClassName);
			this.model = (Model) modelClass.newInstance();
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new CannotInstantiateModelException(e);
		}
	}

	public Task(String[] featuresNames,
				String variableName,
				String query,
				String metaDataString,
				Model model) throws DBException, MetaDataException {

		this.featuresNames = featuresNames;
		this.variableName = variableName;
		this.query = query;
		this.metaData = new Variables(metaDataString);
		this.data = queryData();
		this.model = model;
	}

	/**
	 * Can run only one time!
	 *
	 * @return
     */
	public Result run() throws DBException, ModelExecutionException{
		model.run(this);
		close();
		return new Result(this, model);
	}

	/**
	 *
	 * Get the data from DB
	 *
	 * @return
	 * @throws DBException
	 */
	private ResultSet queryData() throws DBException {
		DBConnector connector = new DBConnector(query, DBConnector.Direction.DATA_IN);
		return connector.connect();
	}

	/**
	 *
	 * @throws DBException
     */
	private void close() throws DBException {
		if(data != null) {
			try {
				this.data.close();
			} catch(SQLException e) {
				throw new DBException(e);
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public Variables getMetaData() {
		return metaData;
	}

	/**
	 *
	 * @return
	 */
	public String getVariableName() {
		return variableName;
	}

	/**
	 *
	 * @return
	 */
	public String getQuery() {
		return query;
	}

	/**
	 *
	 * Return the relevant data structure to pass as input to a model
	 *
	 * @return
	 */
	public ResultSet getData() {
		return data;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static Task fromEnv(String[] args) throws DBException, MetaDataException, CannotInstantiateModelException {
		// Read first system property then env variables
		final String labelName = System.getProperty("PARAM_variables", System.getenv("PARAM_variables"));
		final String[] featuresNames = System.getProperty("PARAM_covariables", System.getenv("PARAM_covariables")).split(",");
		final String query = System.getProperty("PARAM_query", System.getenv().getOrDefault("PARAM_query", ""));
		final String metaData = System.getProperty("PARAM_meta", System.getenv().getOrDefault("PARAM_meta", "{}"));

		if (args != null && args.length > 0) {
			String modelClassName = args[0];
			return new Task(featuresNames, labelName, query, metaData, modelClassName);
		}

		throw new CannotInstantiateModelException(new Exception("No model class name was provided!"));
	}
}