package ummisco.gaml.ext.maths.statements;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import ummisco.gaml.ext.maths.utils.*;

@facets(value = { @facet(name = IKeyword.EQUATION, type = IType.ID, optional = false),
	@facet(name = IKeyword.METHOD, type = IType.STRING_STR /* CHANGE */, optional = false)
/** Numerous other facets to plan : step, init, etc.) **/
}, omissible = IKeyword.EQUATION)
@symbol(name = { IKeyword.SOLVE }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = ISymbolKind.SPECIES)
public class SolveStatement extends AbstractStatementSequence {

	Solver solver;
	StatementDescription equations;

	// Have the same organization as in DrawStatement :
	// The statement contains an abstract subclass called "Solver"; Different solvers (maybe
	// corresponding to different integrators?) are then subclasses of this one. And the statement
	// only calls the one which has been chosen at the beginning.
	// Find a way to declare an initial state (either with the "with:" facet, or using assignments
	// in the body of "solve")

	public SolveStatement(final IDescription desc) {
		super(desc);
		
//		ISpecies context = scope.getAgentScope().getSpecies();
//		IStatement.WithArgs executer = context.getAction(name);
//		executer.setRuntimeArgs(args);
//		Object result = executer.executeOn(scope);
//		String s = returnString;
//		if ( s != null ) {
//			scope.setVarValue(s, result);
//		}
		
		
		List<IDescription> statements = desc.getSpeciesContext().getChildren();
		String eqName = getFacet(IKeyword.EQUATION).literalValue();
		for ( IDescription s : statements ) {
			if ( s.getName().equals(eqName) ) {
				equations =  (StatementDescription) s;
			}
		}
		// Based on the facets, choose a solver and init it;
		String method=getFacet("method").literalValue();
		if(method.equals("rk4")){
			solver = new Rk4Solver(equations); 
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		solver.solve(scope);		
		return super.privateExecuteIn(scope);
	}


}
