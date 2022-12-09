/*******************************************************************************************************
 *
 * ContinueStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.StatementWithChildrenDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.statements.ContinueStatement.ContinueSerializer;
import msi.gaml.statements.ContinueStatement.ContinueValidator;

/**
 * The class ContinueStatement.
 *
 * @author drogoul
 * @since 22 avr. 2012
 *
 */
@symbol (
		name = IKeyword.CONTINUE,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.LOOP })
@inside (
		kinds = ISymbolKind.SEQUENCE_STATEMENT)
@doc (
		value = "`" + IKeyword.CONTINUE
				+ "` allows to skip the remaining statements inside a loop and an ask and directly move to the next element. Inside a switch, it has the same effect as break.")
@validator (ContinueValidator.class)
@serializer (ContinueSerializer.class)
public class ContinueStatement extends AbstractStatement {

	/**
	 * The Class BreakSerializer.
	 */
	public static class ContinueSerializer extends SymbolSerializer<StatementDescription> {

		@Override
		protected void serialize(final SymbolDescription desc, final StringBuilder sb, final boolean includingBuiltIn) {
			sb.append(CONTINUE).append(";");
		}
	}

	/**
	 * The Class BreakValidator.
	 */
	public static class ContinueValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final StatementDescription description) {
			IDescription superDesc = description.getEnclosingDescription();
			while (superDesc instanceof StatementWithChildrenDescription) {
				if (((StatementWithChildrenDescription) superDesc).isContinuable()) return;
				superDesc = superDesc.getEnclosingDescription();
			}
			description.error("'continue' must be used in the context of " + SymbolProto.CONTINUABLE_STATEMENTS,
					IGamlIssue.WRONG_CONTEXT);
		}
	}

	/**
	 * @param desc
	 */
	public ContinueStatement(final IDescription desc) {
		super(desc);
	}

	/**
	 * @see msi.gaml.commands.AbstractCommand#privateExecuteIn(msi.gama.runtime.IScope)
	 */
	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		scope.setContinueStatus();
		return null; // How to return the last object ??
	}

}
