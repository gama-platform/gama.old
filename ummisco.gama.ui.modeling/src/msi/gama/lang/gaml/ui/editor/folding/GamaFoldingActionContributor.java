/*******************************************************************************************************
 *
 * GamaFoldingActionContributor.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor.folding;

import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.actions.IActionContributor;

//hook for using our own action group, where we want to have a string collapse action
/**
 * adapted from
 * {@link org.eclipse.xtext.ui.editor.folding.FoldingActionContributor}
 */
public class GamaFoldingActionContributor implements IActionContributor {

	/** The folding action group. */
	private GamaFoldingActionGroup foldingActionGroup;

	@Override
	public void contributeActions(final XtextEditor editor) {
		foldingActionGroup = new GamaFoldingActionGroup(editor, editor.getInternalSourceViewer());
	}

	@Override
	public void editorDisposed(final XtextEditor editor) {
		if (foldingActionGroup != null)
			foldingActionGroup.dispose();
	}
}
