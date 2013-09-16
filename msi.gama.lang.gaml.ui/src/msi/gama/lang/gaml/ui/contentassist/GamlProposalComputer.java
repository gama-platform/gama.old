/**
 * Created by drogoul, 29 avr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.contentassist;

import msi.gama.lang.gaml.validation.GamlJavaValidator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.xtext.ui.editor.contentassist.CompletionProposalComputer;
import com.google.inject.Inject;

/**
 * The class GamlProposalComputer.
 * 
 * @author drogoul
 * @since 29 avr. 2012
 * 
 */
public class GamlProposalComputer extends CompletionProposalComputer {

	/**
	 * @param state
	 * @param viewer
	 * @param offset
	 */

	@Inject
	GamlJavaValidator validator;

	public GamlProposalComputer(final State state, final ITextViewer viewer, final int offset) {
		super(state, viewer, offset);
	}

}
