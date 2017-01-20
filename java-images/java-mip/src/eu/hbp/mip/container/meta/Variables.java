package eu.hbp.mip.container.meta;

import eu.hbp.mip.container.meta.exceptions.MetaDataException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Read-only collection of Variable (meta-data)
 *
 * @author Arnaud Jutzeler
 *
 */
public class Variables {

    protected HashMap<String, Variable> metaData;

    public Variables(String metaDataString) throws MetaDataException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(metaDataString);
        } catch (IOException e) {
            throw new MetaDataException("Error while parsing meta-data JSON string!");
        }

        if (!rootNode.isObject()) {
            throw new MetaDataException("Error while parsing meta-data JSON string!");
        }

        metaData = new HashMap<>();
        for (Iterator<Map.Entry<String, JsonNode>> iter = rootNode.fields(); iter.hasNext(); ) {
            Map.Entry<String, JsonNode> element = iter.next();
            metaData.put(element.getKey(), Variable.fromJSON(element.getValue()));
        }
    }

    /**
     *
     * @param code
     * @return
     */
    public Variable get(String code) {
        return metaData.get(code);
    }
}
