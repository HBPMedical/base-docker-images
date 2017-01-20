package eu.hbp.mip.container.meta;

import eu.hbp.mip.container.meta.exceptions.MetaDataException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 *
 * @author Arnaud Jutzeler
 *
 */
public class ContinuousVariable extends Variable {
    static final String type = "REAL";

    /**
     *
     * @param node
     * @throws MetaDataException
     */
    public ContinuousVariable(JsonNode node) throws MetaDataException {
        super(node);
    }

    /**
     *
     * @return
     */
    public String getType() {
        return type;
    }
}
