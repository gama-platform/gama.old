/*********************************************************************************************
 *
 * 'LetStatement.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.serializer;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.statements.LetStatement.LetSerializer;
import msi.gaml.statements.LetStatement.LetValidator;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@facets(value = { /* @facet(name = IKeyword.VAR, type = IType.NEW_TEMP_ID, optional = true), */
@facet(name = IKeyword.NAME, type = IType.NEW_TEMP_ID, optional = false),
	@facet(name = IKeyword.VALUE, type = { IType.NONE }, optional = /* AD change false */true),
	@facet(name = IKeyword.OF, type = { IType.TYPE_ID }, optional = true),
	@facet(name = IKeyword.INDEX, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.TYPE, type = { IType.TYPE_ID }, optional = true) }, omissible = IKeyword.NAME)
@symbol(name = { IKeyword.LET },
	kind = ISymbolKind.SINGLE_STATEMENT, concept = { IConcept.SYSTEM },
	with_sequence = false,
	doc = @doc("Allows to declare a temporary variable of the specified type and to initialize it with a value"))
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@validator(LetValidator.class)
@serializer(LetSerializer.class)
public class LetStatement extends SetStatement {

	public static class LetSerializer extends AssignmentSerializer {

		@Override
		protected void serialize(final SymbolDescription desc, final StringBuilder sb, final boolean includingBuiltIn) {
			sb.append(desc.getType().serialize(includingBuiltIn)).append(" ");
			super.serialize(desc, sb, includingBuiltIn);

		}

	}

	public static class LetValidator extends AssignmentValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {
			if ( Assert.nameIsValid(cd) ) {
				super.validate(cd);
			}
		}
	}

	public LetStatement(final IDescription desc) {
		super(desc);
		setName(IKeyword.LET + getVarName());
	}

	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final Object val = value.value(scope);
		varExpr.setVal(scope, val, true);
		return val;
	}

}
