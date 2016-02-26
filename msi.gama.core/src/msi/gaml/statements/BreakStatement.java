/*********************************************************************************************
 * 
 * 
 * 'BreakStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.serializer;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.*;
import msi.gaml.statements.BreakStatement.BreakSerializer;
import msi.gaml.statements.BreakStatement.BreakValidator;

/**
 * The class BreakCommand.
 * 
 * @author drogoul
 * @since 22 avr. 2012
 * 
 */
@symbol(name = IKeyword.BREAK, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = { IConcept.LOOP })
@inside(kinds = ISymbolKind.SEQUENCE_STATEMENT)
@doc(value = "`" + IKeyword.BREAK + "` allows to interrupt the current sequence of statements.")
@validator(BreakValidator.class)
@serializer(BreakSerializer.class)
public class BreakStatement extends AbstractStatement {

	public static class BreakSerializer extends SymbolSerializer<StatementDescription> {

		@Override
		protected void
			serialize(final StatementDescription desc, final StringBuilder sb, final boolean includingBuiltIn) {
			sb.append(BREAK).append(";");
		}
	}

	public static class BreakValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			IDescription superDesc = description.getEnclosingDescription();
			while (superDesc != null && superDesc instanceof StatementDescription) {
				if ( ((StatementDescription) superDesc).isBreakable() ) { return; }
				superDesc = superDesc.getEnclosingDescription();
			}
			description.error("'break' must be used in the context of a loop, a switch or an ask statement",
				IGamlIssue.WRONG_CONTEXT);
		}
	}

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
