package gaml.additions;
import msi.gama.outputs.layers.*;
import msi.gama.outputs.*;
import msi.gama.kernel.batch.*;
import msi.gaml.architecture.weighted_tasks.*;
import msi.gaml.architecture.user.*;
import msi.gaml.architecture.reflex.*;
import msi.gaml.architecture.finite_state_machine.*;
import msi.gaml.species.*;
import msi.gama.metamodel.shape.*;
import msi.gaml.expressions.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.population.*;
import msi.gama.kernel.simulation.*;
import java.util.*;
import  msi.gama.metamodel.shape.*;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.*;
import java.lang.*;
import msi.gama.metamodel.agent.*;
import msi.gaml.types.*;
import msi.gaml.compilation.*;
import msi.gaml.factories.*;
import msi.gaml.descriptions.*;
import msi.gama.util.file.*;
import msi.gama.util.matrix.*;
import msi.gama.util.graph.*;
import msi.gama.util.path.*;
import msi.gama.util.*;
import msi.gama.runtime.exceptions.*;
import msi.gaml.factories.*;
import msi.gaml.statements.*;
import msi.gaml.skills.*;
import msi.gaml.variables.*;
import msi.gama.kernel.experiment.*;
import msi.gaml.operators.*;
import msi.gaml.operators.Random;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Points;
import msi.gaml.operators.Spatial.Properties;
import msi.gaml.operators.System;
import static msi.gaml.operators.Cast.*;
import static msi.gaml.operators.Spatial.*;
import static msi.gama.common.interfaces.IKeyword.*;

public class GamlAdditions extends AbstractGamlAdditions {
		protected static GamlElementDocumentation DOC(int i) { return GamlDocumentation.contents.get(i);}

public void initialize() {
_display("web",msi.gama.display.web.WebDisplaySurface.class, new IDisplayCreator(){ @Override public IDisplaySurface create(Object...args){return new msi.gama.display.web.WebDisplaySurface(args);}});
	}
}