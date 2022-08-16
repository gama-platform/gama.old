package msi.gama.util.graph.loader;

import java.util.HashMap;
import java.util.Map;

/*
 * A custom graph vertex.
 * From jGrapht Example
 */
public class GamaGraphMLNodeImporter
{
    private String id;
    private Map<String,String> attributes;

    public GamaGraphMLNodeImporter(String id)
    {
        this.id = id;
        this.attributes = new HashMap<>();
    }

    @Override
    public int hashCode() { return (id == null) ? 0 : id.hashCode(); }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GamaGraphMLNodeImporter other = (GamaGraphMLNodeImporter) obj;
        if (id == null) {
            return other.id == null;
        } else {
            return id.equals(other.id);
        }
    }

    public String getId() { return id;}
    public void setId(String id){ this.id = id; }
    
    public void addAttribute(String k, String v) {attributes.put(k, v); }
    public Map<String,String> getAttributes() { return attributes; }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        return sb.toString();
    }
}		
