/**
 * Created by drogoul, 22 avr. 2012
 * 
 */
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;

/**
 * The class BreakCommand.
 * 
 * @author drogoul
 * @since 22 avr. 2012
 * 
 */
@symbol(name = IKeyword.BREAK, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(symbols = { IKeyword.MATCH, IKeyword.MATCH_BETWEEN, IKeyword.MATCH_ONE, IKeyword.DEFAULT })
public class BreakStatement extends AbstractStatement {

	/**
	 * @param desc
	 */
	public BreakStatement(final IDescription desc) {
		super(desc);
	}

	/**
	 * @see msi.gaml.commands.AbstractCommand#privateExecuteIn(msi.gama.runtime.IScope)
	 */
	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		scope.interruptLoop();
		return null; // How to return the last object ??
	}

}
