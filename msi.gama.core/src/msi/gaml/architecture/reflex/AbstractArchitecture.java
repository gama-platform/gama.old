package msi.gaml.architecture.reflex;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.ISkillConstructor;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.skills.*;
import msi.gaml.species.ISpecies;

public abstract class AbstractArchitecture extends Skill implements IArchitecture {

	ISkillConstructor duplicator;

	public AbstractArchitecture() {
		super();
	}

	@Override
	public void setDuplicator(final ISkillConstructor duplicator) {
		this.duplicator = duplicator;
	}

	@Override
	public IArchitecture duplicate() {
		ISkill duplicate = null;
		if ( duplicator == null ) {
			duplicate = new ReflexArchitecture();
			duplicate.setName(IKeyword.REFLEX);
		} else {
			duplicate = duplicator.newInstance();
			duplicate.setName(getName());
		}
		return (IArchitecture) duplicate;
	}

	// @Override
	// public IType getType() {
	// return null;
	// }

	// @Override
	// public IType getContentType() {
	// return null;
	// }
	//
	// @Override
	// public IType getKeyType() {
	// return null;
	// }

	@Override
	public String toGaml() {
		return "'" + getName() + " architecture'";
	}

	@Override
	public String getTrace(final IScope scope) {
		return "";
	}

	@Override
	public IDescription getDescription() {
		return null;
	}

	@Override
	public IExpression getFacet(final String ... key) {
		return null;
	}

	@Override
	public boolean hasFacet(final String key) {
		return false;
	}

	@Override
	public Double computePertinence(final IScope scope) throws GamaRuntimeException {
		return 1.0;
	}

	@Override
	public IExpression getPertinence() {
		return new ConstantExpression(1.0);
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {}

	@Override
	public void dispose() {}

}