package eu.hbp.mip.container.meta;

import eu.hbp.mip.container.meta.exceptions.MetaDataException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 *
 * @author Arnaud Jutzeler
 *
 */
abstract public class Variable {

    /**
     *
     */
    protected String code;

    /**
     *
     */
    protected String name;

    /**
     *
     */
    protected String description;

    /**
     *
     */
    protected String methodology;

    /**
     *
     */
    protected String units;

    /**
     *
     * @param node
     */
    protected Variable (JsonNode node) throws MetaDataException {

        code = node.get("code").asText();

        if (code == null) {
            throw new MetaDataException("Error while parsing meta-data JSON string");
        }

        name = node.get("name").asText();

        if (name == null) {
            throw new MetaDataException("Error while parsing meta-data JSON string");
        }

        units = node.get("units").asText();

        if (units == null) {
            throw new MetaDataException("Error while parsing meta-data JSON string");
        }

        description = node.get("description").asText();
        methodology = node.get("methodology").asText();
    }

    /**
     *
     * @param node
     * @return
     * @throws MetaDataException
     */
    public static Variable fromJSON(JsonNode node) throws MetaDataException {

        String typeName = node.get("type").asText();

        if (typeName == null) {
            throw new MetaDataException("Error while parsing meta-data JSON string!");
        }

        Variable variable = null;
        if (typeName.contains("nominal")) {
            variable = new NominalVariable(node);
        } else {
            variable = new ContinuousVariable(node);
        }

        return variable;
    }

    public abstract String getType();
}