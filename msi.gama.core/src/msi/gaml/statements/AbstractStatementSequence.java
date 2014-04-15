/*********************************************************************************************
 * 
 *
 * 'AbstractStatementSequence.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import java.util.List;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Strings;

public class AbstractStatementSequence extends AbstractStatement {

	protected IStatement[] commands;

	public AbstractStatementSequence(final IDescription desc) {
		super(desc);
	}

	@Override
	public String toGaml() {
		StringBuilder sb = new StringBuilder(firstLineToGaml());
		sb.append(' ').append('{').append(Strings.LN);
		if ( commands != null && !isEmpty() ) {
			for ( IStatement s : commands ) {
				sb.append('\t');
				sb.append(s.toGaml()).append(Strings.LN);
			}
		}
		sb.append('}');
		return sb.toString();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		this.commands = commands.toArray(new IStatement[0]);
	}

	public boolean isEmpty() {
		return commands.length == 0;
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		enterScope(scope);
		Object result;
		try {
			result = super.executeOn(scope);
		} finally {
			leaveScope(scope);
		}
		return result;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		Object lastResult = null;
		for ( int i = 0; i < commands.length; i++ ) {
			if ( scope.interrupted() ) { return lastResult; }
			lastResult = commands[i].executeOn(scope);
		}
		return lastResult;
	}

	public void leaveScope(final IScope scope) {
		scope.pop(this);
	}

	public void enterScope(final IScope scope) {
		scope.push(this);
	}

	// @Override
	// public IType getType() {
	// IType result = null;
	// for ( int i = 0; i < commands.length; i++ ) {
	// final IStatement c = commands[i];
	// final IType rt = c.getType();
	// if ( rt == null ) {
	// continue;
	// }
	// if ( result == null ) {
	// result = rt;
	// } else {
	// final IType ft = result;
	// final IType nt = rt;
	// if ( ft != nt ) {
	// if ( ft.id() == IType.INT && nt.id() == IType.FLOAT || nt.id() == IType.INT &&
	// rt.id() == IType.FLOAT ) {
	// result = Types.get(IType.FLOAT);
	// } else {
	// result = Types.NO_TYPE;
	// }
	// }
	// }
	//
	// }
	// return result;
	// }

	public IStatement[] getCommands() {
		return commands;
	}

}