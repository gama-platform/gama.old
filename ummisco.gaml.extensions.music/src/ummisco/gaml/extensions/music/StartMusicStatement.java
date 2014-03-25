package ummisco.gaml.extensions.music;

import java.io.File;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;
import ummisco.gaml.extensions.music.StartMusicStatement.StartMusicValidator;


@symbol(name = IKeyword.START_MUSIC, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true,
		doc = @doc("Starts playing a music file. The supported formats are aif, au, mp3, wav. One agent"))
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = { 
			@facet(name = IKeyword.SOURCE, type = IType.STRING, optional = false, doc = @doc("The path to music file. This path is relative to the path of the model.")),
			@facet(name = IKeyword.MODE, type = IType.ID, values = { IKeyword.OVERWRITE, IKeyword.IGNORE }, optional = true, doc = @doc("Mode of ")),
			@facet(name = IKeyword.REPEAT, type = IType.BOOL, optional = true, doc = @doc("")) })
@validator(StartMusicValidator.class)
public class StartMusicStatement extends AbstractStatementSequence {
	
	public static class StartMusicValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {
			if (cd.getFacets().getLabel(IKeyword.SOURCE) == null ) { cd.error("missing 'source' facet"); }
		}
	}
	 
	
	private IExpression source;
	private IExpression mode;
	private IExpression repeat;

	private AbstractStatementSequence sequence = null;

	public StartMusicStatement(IDescription desc) {
		super(desc);
		
		source = getFacet(IKeyword.SOURCE);
		mode = getFacet(IKeyword.MODE);
		repeat = getFacet(IKeyword.REPEAT);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}
	
	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		
		IAgent currentAgent = scope.getAgentScope();
		
		GamaMusicPlayer musicPlayer = MusicPlayerBroker.getInstance().getMusicPlayer(currentAgent);
		String musicFilePath = scope.getModel().getRelativeFilePath((String) source.value(scope), false);
		
		if (musicPlayer != null) {
			musicPlayer.play(new File(musicFilePath), 
					mode != null ? (String) mode.value(scope) : GamaMusicPlayer.OVERWRITE_MODE, 
					repeat != null ? (Boolean) repeat.value(scope) : false);
		} else {
			//System.out.println("No more player in pool!");
		}

		if (sequence != null) {
			Object[] result = new Object[1];
			scope.execute(sequence, currentAgent, null, result);
		}
		
		
		return null;
	}
}
