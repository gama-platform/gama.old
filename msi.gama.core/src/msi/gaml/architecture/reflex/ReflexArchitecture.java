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
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;

/**
 * Written by drogoul Modified on 12 sept. 2010
 * 
 * @todo Description
 * 
 */
@skill(name = IKeyword.REFLEX)
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
			_inits_number = _inits.size();
			return;
		}
		_reflexes.add(c);
		_reflexesNumber++;

	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		// if ( scope.interrupted() ) { return null; }
		return executeReflexes(scope);
	}

	protected final Object executeReflexes(final IScope scope) {
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
	public void init(final IScope scope) throws GamaRuntimeException {
		for ( int i = 0; i < _inits_number; i++ ) {
			if ( scope.interrupted() ) { return; }
			_inits.get(i).executeOn(scope);
		}
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {
		// hasBehavior = _behaviorsNumber > 0;
	}

	@Override
	public void dispose() {}
}
