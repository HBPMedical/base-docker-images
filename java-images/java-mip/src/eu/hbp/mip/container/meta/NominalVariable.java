package eu.hbp.mip.container.meta;

import java.util.ArrayList;

import eu.hbp.mip.container.meta.exceptions.MetaDataException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.List;

/**
 *
 *
 * @author Arnaud Jutzeler
 *
 */
public class NominalVariable extends Variable {

    protected String type;
    protected String[] symbols;

    /**
     *
     * @param node
     * @throws MetaDataException
     */
    public NominalVariable (JsonNode node) throws MetaDataException {
        super(node);

        type = node.get("type").asText();

        JsonNode enumeration = node.get("enumerations");
        if (enumeration == null) {
            throw new MetaDataException("Error while parsing meta-data JSON string!");
        }

        List<String> symbols = new ArrayList<>();
        for (Iterator<JsonNode> iter = enumeration.elements(); iter.hasNext(); ) {
            JsonNode element = iter.next();
            String code = element.get("code").asText();

            if (code == null) {
                throw new MetaDataException("Error while parsing meta-data JSON string!");
            }

           symbols.add(code);
        }

        this.symbols = symbols.toArray(new String[symbols.size()]);
    }

    /**
     *
     * @return
     */
    public String getType() {
        return this.type;
    }

    /**
     *
     * @return
     */
    public String[] getSymbols() {
        return this.symbols;
    }
}
