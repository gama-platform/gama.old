/*********************************************************************************************
 * 
 *
 * 'ReflexArchitecture.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.reflex;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.statements.IStatement;

/**
 * Written by drogoul Modified on 12 sept. 2010
 * 
 * @todo Description
 * 
 */
@skill(name = IKeyword.REFLEX, concept = { IConcept.BEHAVIOR, IConcept.ARCHITECTURE })
public class ReflexArchitecture extends AbstractArchitecture {

	private final List<IStatement> _inits = new ArrayList();
	private final List<IStatement> _reflexes = new ArrayList();
	private int _reflexesNumber = 0;
	private int _inits_number = 0;

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		clearBehaviors();
		for ( final ISymbol c : children ) {
			addBehavior((IStatement) c);
		}
	}

	protected void clearBehaviors() {
		_inits.clear();
		_reflexes.clear();
		_reflexesNumber = 0;
		_inits_number = 0;
	}

	public void addBehavior(final IStatement c) {
		if ( IKeyword.INIT.equals(c.getFacet(IKeyword.KEYWORD).literalValue()) ) {
			_inits.add(0, c);
			_inits_number++;
			return;
		}
		_reflexes.add(c);
		_reflexesNumber++;

	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		return executeReflexes(scope);
	}

	protected final Object executeReflexes(final IScope scope) throws GamaRuntimeException {
		if ( _reflexesNumber == 0 ) { return null; }
		Object result = null;
		for ( int i = 0; i < _reflexesNumber; i++ ) {
			final IStatement r = _reflexes.get(i);
			if ( !scope.interrupted() ) {
				result = r.executeOn(scope);
			}
		}
		return result;
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		for ( int i = 0; i < _inits_number; i++ ) {
			if ( scope.interrupted() ) { return false; }
			_inits.get(i).executeOn(scope);
		}
		return true;
	}

}
