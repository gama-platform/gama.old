/**
 * Created by drogoul, 11 mai 2014
 * 
 */
package msi.gaml.statements;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.IStatement.WithArgs;

/**
 * Class AbstractStatementSequenceWithArgs.
 * 
 * @author drogoul
 * @since 11 mai 2014
 * 
 */
public class AbstractStatementSequenceWithArgs extends AbstractStatementSequence implements WithArgs {

	final ThreadLocal<Arguments> actualArgs = new ThreadLocal<>();

	/**
	 * @param desc
	 */
	public AbstractStatementSequenceWithArgs(final IDescription desc) {
		super(desc);
	}

	/**
	 * Method setFormalArgs()
	 * 
	 * @see msi.gaml.statements.IStatement.WithArgs#setFormalArgs(msi.gaml.statements.Arguments)
	 */
	@Override
	public void setFormalArgs(final Arguments args) {
	}

	/**
	 * Method setRuntimeArgs()
	 * 
	 * @see msi.gaml.statements.IStatement.WithArgs#setRuntimeArgs(msi.gaml.statements.Arguments)
	 */
	@Override
	public void setRuntimeArgs(final Arguments args) {
		actualArgs.set(new Arguments(args));
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		scope.stackArguments(actualArgs.get());
		final Object result = super.privateExecuteIn(scope);
		return result;
	}

}
