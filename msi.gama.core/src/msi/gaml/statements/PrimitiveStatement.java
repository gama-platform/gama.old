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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.compilation.IDescriptionValidator.NullValidator;
import msi.gaml.descriptions.*;
import msi.gaml.skills.ISkill;
import msi.gaml.types.IType;

/**
 * The Class ActionCommand.
 * 
 * @author drogoul
 */
@symbol(name = IKeyword.PRIMITIVE, kind = ISymbolKind.BEHAVIOR, with_sequence = true, with_args = true)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL }, symbols = IKeyword.CHART)
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	// @facet(name = IKeyword.JAVA, type = IType.ID, optional = false),
	@facet(name = IKeyword.VIRTUAL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true) }, omissible = IKeyword.NAME)
// Necessary to avoid running the validator from ActionStatement
@validator(NullValidator.class)
public class PrimitiveStatement extends ActionStatement {

	// Declaring a null validator because primites dont need to be checked

	private ISkill skill = null;
	private final GamaHelper helper;

	/**
	 * The Constructor.
	 * 
	 * @param actionDesc the action desc
	 * @param sim the sim
	 */

	public PrimitiveStatement(final IDescription desc) {
		super(desc);
		helper = getDescription().getHelper();
		// skill = desc.getSpeciesContext().getSkillFor(((StatementDescription) desc).getHelper().getSkillClass());
		skill = AbstractGamlAdditions.getSkillInstanceFor(helper.getSkillClass());
	}

	@Override
	public PrimitiveDescription getDescription() {
		return (PrimitiveDescription) description;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		Object result = null;
		scope.stackArguments(actualArgs);
		final IAgent agent = scope.getAgentScope();
		result = helper.run(scope, agent, skill == null ? agent : skill);
		return result;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		actualArgs = args;
	}
	//
	// @Override
	// public IType getType() {
	// return helper.getReturnType();
	// }

	// FIXME for the moment, only scarce information about primitives
	// @Override
	// public IType getContentType() {
	// return getType().getContentType();
	// }
	//
	// @Override
	// public IType getKeyType() {
	// return getType().getKeyType();
	// }

}
