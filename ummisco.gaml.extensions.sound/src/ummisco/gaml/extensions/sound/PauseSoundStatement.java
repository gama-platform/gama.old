/*******************************************************************************************************
 *
 * PauseSoundStatement.java, in ummisco.gaml.extensions.sound, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.sound;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatementSequence;
import ummisco.gaml.extensions.sound.PauseSoundStatement.PauseSoundValidator;

/**
 * The Class PauseSoundStatement.
 */
@symbol (
		name = IKeyword.PAUSE_SOUND,
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		concept = { IConcept.SOUND })
@doc ("Allows to pause the sound output")
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER, ISymbolKind.OUTPUT })
@validator (PauseSoundValidator.class)
public class PauseSoundStatement extends AbstractStatementSequence {

	/**
	 * The Class PauseSoundValidator.
	 */
	public static class PauseSoundValidator implements IDescriptionValidator<IDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {
			// what to validate?
		}
	}

	/** The sequence. */
	private AbstractStatementSequence sequence = null;

	/**
	 * Instantiates a new pause sound statement.
	 *
	 * @param desc the desc
	 */
	public PauseSoundStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> com) {
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {

		final IAgent currentAgent = scope.getAgent();

		final GamaSoundPlayer soundPlayer = SoundPlayerBroker.getInstance().getSoundPlayer(currentAgent);
		soundPlayer.pause(scope);

		if (sequence != null) { scope.execute(sequence, currentAgent, null); }

		return null;
	}
}
