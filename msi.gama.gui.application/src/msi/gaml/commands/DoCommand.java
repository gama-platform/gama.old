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

import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.descriptions.*;
import msi.gama.internal.expressions.Arguments;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.no_scope;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_args;
import msi.gama.precompiler.*;

/**
 * Written by drogoul Modified on 7 févr. 2010
 * 
 * @todo Description
 * 
 */
@symbol(name = { ISymbol.DO, ISymbol.REPEAT }, kind = ISymbolKind.SINGLE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@facets({ @facet(name = ISymbol.ACTION, type = IType.ID, optional = false),
	@facet(name = ISymbol.WITH, type = IType.MAP_STR, optional = true) })
@with_args
@no_scope
public class DoCommand extends AbstractCommandSequence implements ICommand.WithArgs {

	Arguments	args;

	public DoCommand(final IDescription desc) {
		super(desc);
		setName(getLiteral(ISymbol.ACTION));
	}

	public Arguments getArgs() {
		return args;
	}

	@Override
	public void setFormalArgs(final Arguments args) throws GamlException {
		verifyArgs(args);
		this.args = args;
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		ISpecies context = stack.getAgentScope().getSpecies(); // TODO change to
																// getAgentScope.getExecutionContext???
		ICommand.WithArgs executer = context.getAction(name);
		executer.setRuntimeArgs(args);
		Object result = executer.executeOn(stack);
		return result;
	}

	public void verifyArgs(final Arguments args) throws GamlException {
		CommandDescription executer =
			((ExecutionContextDescription) description.getDescriptionDeclaringAction(name))
				.getAction(name);
		if ( executer == null ) { throw new GamlException("Unknown action " + getName()); }
		executer.verifyArgs(args.keySet());
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {}

	@Override
	public IType getReturnType() {
		CommandDescription executer = description.getSpeciesContext().getAction(name);
		return executer.getReturnType();
	}

	@Override
	public IType getReturnContentType() {
		CommandDescription executer = description.getSpeciesContext().getAction(name);
		return executer.getReturnContentType();
	}
}
