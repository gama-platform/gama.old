/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.architecture.reflex;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IGamlAgent;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.skills.Skill;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 12 sept. 2010
 * 
 * @todo Description
 * 
 */
@skill(name = "reflex")
public class ReflexArchitecture extends Skill implements IArchitecture {

	private final List<IStatement> _inits = new ArrayList();
	private final List<IStatement> _reflexes = new ArrayList();
	private int _reflexesNumber = 0;
	private int _inits_number = 0;

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		for ( ISymbol c : children ) {
			addBehavior((IStatement) c);
		}
	}

	public void addBehavior(final IStatement c) {
		if ( IKeyword.INIT.equals(c.getFacet(IKeyword.KEYWORD).literalValue()) ) {

			_inits.add(0, c);
			_inits_number = _inits.size();
			return;
		}
		_reflexes.add(c);
		_reflexesNumber++;

	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		return executeReflexes(scope);
	}

	protected final Object executeReflexes(final IScope scope) {
		if ( _reflexesNumber == 0 ) { return null; }
		Object result = null;
		IGamlAgent a = getCurrentAgent(scope);
		for ( int i = 0; i < _reflexesNumber; i++ ) {
			IStatement r = _reflexes.get(i);
			if ( !a.dead() ) {
				result = r.executeOn(scope);
			}
		}
		return result;
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		for ( int i = 0; i < _inits_number; i++ ) {
			_inits.get(i).executeOn(scope);
		}
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {
		// hasBehavior = _behaviorsNumber > 0;
	}

	@Override
	public IGamlAgent getCurrentAgent(final IScope scope) {
		return (IGamlAgent) super.getCurrentAgent(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IStatement#getReturnType()
	 */
	@Override
	public IType getReturnType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IStatement#getReturnContentType()
	 */
	@Override
	public IType getReturnContentType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IStatement#toGaml()
	 */
	@Override
	public String toGaml() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ISymbol#dispose()
	 */
	@Override
	public void dispose() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ISymbol#getDescription()
	 */
	@Override
	public IDescription getDescription() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ISymbol#getFacet(java.lang.String)
	 */
	@Override
	public IExpression getFacet(final String key) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ISymbol#hasFacet(java.lang.String)
	 */
	@Override
	public boolean hasFacet(final String key) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.INamed#getName()
	 */
	@Override
	public String getName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.INamed#setName(java.lang.String)
	 */
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
