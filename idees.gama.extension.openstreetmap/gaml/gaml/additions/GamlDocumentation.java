package gaml.additions;
import java.util.*;
import msi.gaml.compilation.GamlElementDocumentation;

public class GamlDocumentation {
	protected final static GamlElementDocumentation AS = new GamlElementDocumentation(null);
protected static GamlElementDocumentation S(final String ... strings) { return new GamlElementDocumentation(strings);}

public static final List<GamlElementDocumentation> contents = new ArrayList();
	 static {

	}
}