package msi.gama.util.graph.loader;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;

public class GamaGraphMLEdgeImporter extends DefaultEdge {

	private static final long serialVersionUID = 2989298487961024016L;

    private Map<String,String> attributes = new HashMap<>();

    public void addAttribute(String k, String v) {attributes.put(k, v); }
    public Map<String,String> getAttributes() { return attributes; }
}
