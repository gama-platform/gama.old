/*******************************************************************************************************
 *
 * msi.gaml.statements.PrimitiveStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISkill;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator.NullValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.compilation.IGamaHelper;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.PrimitiveDescription;
import msi.gaml.species.AbstractSpecies;
import msi.gaml.types.IType;

/**
 * The Class ActionCommand.
 *
 * @author drogoul
 */
@symbol (
		name = IKeyword.PRIMITIVE,
		kind = ISymbolKind.BEHAVIOR,
		with_sequence = true,
		with_args = true,
		internal = true,
		concept = { IConcept.ACTION, IConcept.SYSTEM })
@inside (
		kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL },
		symbols = IKeyword.CHART)
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				doc = { @doc ("The name of this primitive") }),
				@facet (
						name = IKeyword.VIRTUAL,
						type = IType.BOOL,
						optional = true,
						doc = { @doc ("Indicates if this primitive is virtual or not. A virtual primitive does not contain code and must be redefined in the species that implement the skill or extend the species that contain it") }),
				@facet (
						name = IKeyword.TYPE,
						type = IType.TYPE_ID,
						optional = true,
						doc = { @doc ("The type of the value returned by this primitive") }) },
		omissible = IKeyword.NAME)
// Necessary to avoid running the validator from ActionStatement
@validator (NullValidator.class)
@doc ("A primitve is an action written in Java (as opposed to GAML for regular actions")
@SuppressWarnings ({ "rawtypes" })
public class PrimitiveStatement extends ActionStatement {

	// Declaring a null validator because primites dont need to be checked

	private ISkill skill = null;
	private final IGamaHelper helper;

	public PrimitiveStatement(final IDescription desc) {
		super(desc);
		helper = getDescription().getHelper();
	}

	@Override
	public PrimitiveDescription getDescription() {
		return (PrimitiveDescription) description;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		Object result = null;
		scope.stackArguments(actualArgs.get());
		final IAgent agent = scope.getAgent();
		result = helper.run(scope, agent, skill == null ? agent : skill);
		return result;
	}

	@Override
	public void setRuntimeArgs(final IScope scope, final Arguments args) {
		actualArgs.set(args);
	}

	@Override
	public void setEnclosing(final ISymbol enclosing) {
		if (enclosing instanceof AbstractSpecies) {
			skill = ((AbstractSpecies) enclosing).getSkillInstanceFor(helper.getSkillClass());
		}
	}

}
