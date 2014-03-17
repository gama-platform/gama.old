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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
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