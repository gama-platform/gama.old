/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.control;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.skills.Skill;
import msi.gaml.agents.IGamlAgent;

/**
 * Written by drogoul Modified on 12 sept. 2010
 * 
 * @todo Description
 * 
 */
@skill("reflex")
public class ReflexControl extends Skill implements IControl {

	private final List<ICommand>	_inits				= new ArrayList();
	private final List<ICommand>	_behaviors			= new ArrayList();
	private int						_behaviorsNumber	= 0;
	private int						_inits_number		= 0;

	// protected boolean hasBehavior;

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {
		for ( ISymbol c : commands ) {
			addBehavior((ICommand) c);
		}
	}

	public void addBehavior(final ICommand c) {
		if ( ISymbol.INIT.equals(c.getFacet(ISymbol.KEYWORD).literalValue()) ) {

			_inits.add(0, c);
			_inits_number = _inits.size();
			return;
		}
		_behaviors.add(c);
		_behaviorsNumber++;

	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		if ( _behaviorsNumber == 0 ) { return null; }
		Object result = null;
		IGamlAgent a = getCurrentAgent(scope);
		for ( int i = 0; i < _behaviorsNumber; i++ ) {
			ICommand r = _behaviors.get(i);
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
	public void verifyBehaviors(final IExecutionContext context) throws GamlException {
		// hasBehavior = _behaviorsNumber > 0;
	}

	// @Override
	// public boolean hasBehavior() {
	// return hasBehavior;
	// }

	@Override
	public IGamlAgent getCurrentAgent(final IScope scope) {
		return (IGamlAgent) super.getCurrentAgent(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ICommand#getReturnType()
	 */
	@Override
	public IType getReturnType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ICommand#getReturnContentType()
	 */
	@Override
	public IType getReturnContentType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ICommand#toGaml()
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
}
