package gaml.additions;
import msi.gama.outputs.*;
import msi.gama.kernel.batch.*;
import msi.gaml.architecture.weighted_tasks.*;
import msi.gama.outputs.layers.*;
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
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gama.util.matrix.*;
import msi.gama.util.graph.*;
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
import msi.gama.kernel.experiment.AbstractExperiment.ExperimentatorPopulation.ExperimentatorAgent;
import static msi.gaml.operators.Cast.*;
import static msi.gaml.operators.Spatial.*;
import static msi.gama.common.interfaces.IKeyword.*;

public class GamlAdditions extends AbstractGamlAdditions {
	public void initialize() {
_symbol(ummisco.gaml.ext.maths.statements.SingleEquationStatement.class,2,F,false,T,F,new String[]{"equation"},AI,new FacetProto[]{new FacetProto("left",new String[]{"unknown"},AS,false),new FacetProto("right",new String[]{"float"},AS,false)},"right",new ISymbolConstructor() {public ISymbol create(IDescription d) {return new ummisco.gaml.ext.maths.statements.SingleEquationStatement(d);}},"=");
_symbol(ummisco.gaml.ext.maths.statements.SystemOfEquationsStatement.class,11,F,false,T,T,AS,new int[]{0},new FacetProto[]{new FacetProto(NAME,new String[]{"id"},AS,false)},NAME,new ISymbolConstructor() {public ISymbol create(IDescription d) {return new ummisco.gaml.ext.maths.statements.SystemOfEquationsStatement(d);}},"equation");
_symbol(ummisco.gaml.ext.maths.statements.SolveStatement.class,11,F,false,T,T,AS,new int[]{0},new FacetProto[]{new FacetProto("equation",new String[]{"id"},AS,false),new FacetProto("method",new String[]{"string"},AS,false)},"equation",new ISymbolConstructor() {public ISymbol create(IDescription d) {return new ummisco.gaml.ext.maths.statements.SolveStatement(d);}},"solve");
_binary(new String[] {"diff"},D,D,D,98,F,-13,-13,new IOpRun(){public Double run(IScope s,Object t,Object r){return ummisco.gaml.ext.maths.statements.SingleEquationStatement.diff(s,asFloat(s,t),asFloat(s,r));}});
_binary(new String[] {"diff2"},D,D,D,98,F,-13,-13,new IOpRun(){public Double run(IScope s,Object t,Object r){return ummisco.gaml.ext.maths.statements.SingleEquationStatement.diff2(s,asFloat(s,t),asFloat(s,r));}});
	}
}