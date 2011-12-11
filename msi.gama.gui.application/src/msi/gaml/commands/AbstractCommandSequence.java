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
package msi.gaml.commands;

import static msi.gama.util.ExecutionStatus.*;
import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.util.ExecutionStatus;

@with_sequence
public class AbstractCommandSequence extends AbstractCommand {

	protected ICommand[] commands;

	public AbstractCommandSequence(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		this.commands = commands.toArray(new ICommand[0]);
	}

	public boolean isEmpty() {
		return commands.length == 0;
	}

	@Override
	public Object executeOn(final IScope stack) throws GamaRuntimeException {
		enterScope(stack);
		Object result;
		try {
			result = super.executeOn(stack);
		} finally {
			leaveScope(stack);
		}
		return result;
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		boolean allSkipped = true;
		Object lastResult = null;
		ExecutionStatus statusBeforeSkipped = success;
		for ( int i = 0; i < commands.length; i++ ) {
			lastResult = commands[i].executeOn(stack);
			ExecutionStatus status = stack.getStatus();
			if ( status != skipped ) {
				if ( status == interrupt ) {
					stack.setStatus(interrupt);
					return lastResult;
				}
				allSkipped = false;
				statusBeforeSkipped = status;
			}
		}
		stack.setStatus(allSkipped ? skipped : statusBeforeSkipped);
		return lastResult;
	}

	public void leaveScope(final IScope scope) {
		scope.pop(this);
	}

	public void enterScope(final IScope scope) {
		scope.push(this);
	}

	@Override
	public IType getReturnType() {
		IType result = null;
		for ( int i = 0; i < commands.length; i++ ) {
			ICommand c = commands[i];
			IType rt = c.getReturnType();
			if ( rt == null ) {
				continue;
			}
			if ( result == null ) {
				result = rt;
			} else {
				IType ft = result;
				IType nt = rt;
				if ( ft != nt ) {
					if ( ft.id() == IType.INT && nt.id() == IType.FLOAT || nt.id() == IType.INT &&
						rt.id() == IType.FLOAT ) {
						result = Types.get(IType.FLOAT);
					} else {
						result = Types.NO_TYPE;
					}
				}
			}

		}
		return result;
	}

}