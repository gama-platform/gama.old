/*********************************************************************************************
 *
 * 'ReflexArchitecture.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.architecture.reflex;

import java.util.ArrayList;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
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
@skill (
		name = IKeyword.REFLEX,
		concept = { IConcept.BEHAVIOR, IConcept.ARCHITECTURE })
public class ReflexArchitecture extends AbstractArchitecture {

	private List<IStatement> _inits;
	private List<IStatement> _reflexes;
	private List<IStatement> _aborts;

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {
		clearBehaviors();
		for (final ISymbol c : children) {
			addBehavior((IStatement) c);
		}
	}

	protected void clearBehaviors() {
		if (_inits != null)
			_inits.clear();
		_inits = null;
		if (_aborts != null)
			_aborts.clear();
		_aborts = null;
		if (_reflexes != null)
			_reflexes.clear();
		_reflexes = null;
	}

	public void addBehavior(final IStatement c) {
		switch (c.getKeyword()) {
			case IKeyword.INIT:
				if (_inits == null)
					_inits = new ArrayList<>();
				_inits.add(0, c);
				return;

			case IKeyword.ABORT:
				if (_aborts == null)
					_aborts = new ArrayList<>();
				_aborts.add(0, c);
				return;
			case IKeyword.REFLEX:
				if (_reflexes == null)
					_reflexes = new ArrayList<>();
				_reflexes.add(c);
				break;
			default:
				;
		}

	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		return executeReflexes(scope);
	}

	protected final Object executeReflexes(final IScope scope) throws GamaRuntimeException {
		if (_reflexes == null)
			return null;
		Object result = null;
		for (final IStatement r : _reflexes) {
			if (!scope.interrupted()) {
				result = r.executeOn(scope);
			}
		}
		return result;
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		if (_inits == null)
			return true;
		for (final IStatement init : _inits) {
			if (scope.interrupted()) { return false; }
			init.executeOn(scope);
		}
		return true;
	}

	public boolean abort(final IScope scope) throws GamaRuntimeException {
		if (_aborts == null) { return true; }
		for (final IStatement init : _aborts) {
			if (scope.interrupted()) { return false; }
			init.executeOn(scope);
		}
		return true;
	}

	@Override
	public void setEnclosing(final ISymbol enclosing) {
		// Nothing to do by default
	}

}
