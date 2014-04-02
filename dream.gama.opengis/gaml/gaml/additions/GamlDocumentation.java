package gaml.additions;
import java.util.*;
import msi.gaml.compilation.GamlElementDocumentation;

public class GamlDocumentation {
	protected final static GamlElementDocumentation AS = new GamlElementDocumentation(null);
protected static GamlElementDocumentation S(final String ... strings) { return new GamlElementDocumentation(strings);}

public final List<GamlElementDocumentation> contents = new ArrayList();static GamlDocumentation instance;static GamlDocumentation getInstance() {if (instance == null) {	instance = new GamlDocumentation(); instance.initialize();} return instance;}
	 void initialize() {
contents.add(S("WMS: A simple call to WMS!",(String)null));
contents.add(S("REST: Read data from RESTService!",(String)null));
contents.add(S("WMS: A simple call to WMS!",(String)null));
contents.add(S("WMS: A simple call to WFS/GML2!",(String)null));

	}
}