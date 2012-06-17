package msi.gaml.architecture.reflex;

import msi.gama.metamodel.agent.IGamlAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

public abstract class AbstractArchitecture extends Skill implements IArchitecture {

	public AbstractArchitecture() {
		super();
	}

	@Override
	public IArchitecture duplicate() {
		try {
			return this.getClass().newInstance();
		} catch (InstantiationException e) {
			return new ReflexArchitecture();
		} catch (IllegalAccessException e) {
			return new ReflexArchitecture();
		}
	}

	@Override
	public IGamlAgent getCurrentAgent(final IScope scope) {
		return (IGamlAgent) super.getCurrentAgent(scope);
	}

	@Override
	public IType getReturnType() {
		return null;
	}

	@Override
	public IType getReturnContentType() {
		return null;
	}

	@Override
	public String toGaml() {
		return null;
	}

	@Override
	public IDescription getDescription() {
		return null;
	}

	@Override
	public IExpression getFacet(final String key) {
		return null;
	}

	@Override
	public boolean hasFacet(final String key) {
		return false;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(final String newName) {}

	/**
	 * @see msi.gaml.compilation.ISymbol#error(java.lang.String)
	 */
	@Override
	public void error(final String s) {}

	@Override
	public void warning(final String s, final String facet) {}

	/**
	 * @see msi.gaml.compilation.ISymbol#error(java.lang.String, java.lang.String)
	 */
	@Override
	public void error(final String s, final String facet) {}

	@Override
	public Double computePertinence(final IScope scope) throws GamaRuntimeException {
		return 1.0;
	}

	@Override
	public IExpression getPertinence() {
		return null;
	}

}