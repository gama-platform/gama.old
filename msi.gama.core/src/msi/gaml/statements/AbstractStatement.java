/*******************************************************************************************************
 *
 * AbstractStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;

/**
 * Written by drogoul Modified on 6 févr. 2010
 *
 */

public abstract class AbstractStatement extends Symbol implements IStatement {

	/**
	 * Instantiates a new abstract statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public AbstractStatement(final IDescription desc) {
		super(desc);
		if (desc != null) {
			final String k = getKeyword();
			final String n = desc.getName();
			setName(k == null ? "" : k + " " + (n == null ? "" : n));
		}
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		try {
			scope.setCurrentSymbol(this);
			return privateExecuteIn(scope);
		} catch (final GamaRuntimeException e) {
			e.addContext(this);
			GAMA.reportAndThrowIfNeeded(scope, e, true);
			return null;
		} finally {
			scope.setCurrentSymbol(null);
		}
	}

	/**
	 * Private execute in.
	 *
	 * @param scope
	 *            the scope
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected abstract Object privateExecuteIn(IScope scope) throws GamaRuntimeException;

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {}

	@Override
	public String toString() {
		return description.serialize(true);
	}

	@Override
	public StatementDescription getDescription() { return (StatementDescription) super.getDescription(); }

}
