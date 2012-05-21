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
package msi.gaml.commands;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_args;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.skills.*;
import msi.gaml.types.IType;

/**
 * The Class ActionCommand.
 * 
 * @author drogoul
 */
@symbol(name = IKeyword.PRIMITIVE, kind = ISymbolKind.BEHAVIOR)
@inside(kinds = ISymbolKind.SPECIES)
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.JAVA, type = IType.ID, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true) }, omissible = IKeyword.NAME)
// @with_sequence
@with_args
// TODO Verify this
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
		String methodName = getLiteral(IKeyword.JAVA);

		SpeciesDescription context = desc.getSpeciesContext();
		Class methodClass = context.getSkillClassFor(methodName);
		if ( Skill.class.isAssignableFrom(methodClass) ) {
			skill = AbstractGamlAdditions.getSkillInstanceFor(methodClass);
		}
		executer = AbstractGamlAdditions.getPrimitive(methodClass, methodName);
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

	// @Override
	// public void verifyArgs(final Arguments args) {
	// // for ( String arg : formalArgs.names() ) {
	// // if ( formalArgs.getArg(arg) == null && !args.has(arg) ) { throw new
	// // GamlException("Missing argument " + arg + " in call to " + getName()); }
	// // }
	// // For the moment, primitive arguments are not considered as "mandatory"
	// for ( String arg : args.keySet() ) {
	// if ( !formalArgs.containsKey(arg) ) {
	// error("Unknown argument " + arg + " in call to " + getName(), arg);
	// }
	// }
	// }

	@Override
	public IType getReturnType() {
		return executer.getReturnType();
	}

}
