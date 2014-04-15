/*********************************************************************************************
 * 
 * 
 * 'SpeciesConstantExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

public class SpeciesConstantExpression extends ConstantExpression {

	boolean computed = false;

	public SpeciesConstantExpression(final String val, final IType t) {
		super(val, t);
	}

	@Override
	public Object value(final IScope scope) {
		IAgent a = scope.getAgentScope();
		if ( a != null ) {
			IPopulation pop = scope.getAgentScope().getPopulationFor((String) value);
			if ( pop != null ) { return pop.getSpecies(); }
		}
		ISpecies s = scope.getSimulationScope().getModel().getSpecies((String) value);
		return s;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String toGaml() {
		if ( computed ) { return super.toGaml(); }
		return (String) value;
	}

}
