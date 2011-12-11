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
import msi.gama.internal.descriptions.ExecutionContextDescription;
import msi.gama.internal.expressions.Arguments;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_args;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;

/**
 * The Class ActionCommand.
 * 
 * @author drogoul
 */
@symbol(name = ISymbol.PRIMITIVE, kind = ISymbolKind.BEHAVIOR)
@inside(kinds = ISymbolKind.SPECIES)
@facets({ @facet(name = ISymbol.NAME, type = IType.ID, optional = false),
	@facet(name = ISymbol.JAVA, type = IType.ID, optional = false),
	@facet(name = ISymbol.RETURNS, type = IType.TYPE_ID, optional = true) })
@with_sequence
@with_args
public class PrimitiveCommand extends ActionCommand {

	private ISkill skill = null;
	private final IPrimitiveExecuter executer;

	/**
	 * The Constructor.
	 * 
	 * @param actionDesc the action desc
	 * @param sim the sim
	 */
	public PrimitiveCommand(final IDescription desc) {
		super(desc);
		String methodName = getLiteral(ISymbol.JAVA);

		ExecutionContextDescription context = desc.getSpeciesContext();
		Class methodClass = context.getSkillClassFor(methodName);
		skill = context.getSharedSkill(methodClass);
		executer = GamlCompiler.getPrimitive(methodClass, methodName);
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		Object result = null;
		actualArgs.stack(stack);
		IAgent agent = stack.getAgentScope();
		result = executer.execute(skill == null ? agent : skill, agent, stack);
		return result;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		actualArgs = args;
	}

	@Override
	public void verifyArgs(final Arguments args) throws GamlException {
		// for ( String arg : formalArgs.names() ) {
		// if ( formalArgs.getArg(arg) == null && !args.has(arg) ) { throw new
		// GamlException("Missing argument " + arg + " in call to " + getName()); }
		// }
		// For the moment, primitive arguments are not considered as "mandatory"
		for ( String arg : args.keySet() ) {
			if ( !formalArgs.containsKey(arg) ) { throw new GamlException("Unknown argument " +
				arg + " in call to " + getName()); }
		}
	}

	@Override
	public IType getReturnType() {
		return executer.getReturnType();
	}

}
