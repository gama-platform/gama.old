/*********************************************************************************************
 *
 * 'GamaFoldingActionContributor.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor.folding;

import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.actions.IActionContributor;

//hook for using our own action group, where we want to have a string collapse action
/**
 * adapted from
 * {@link org.eclipse.xtext.ui.editor.folding.FoldingActionContributor}
 */
public class GamaFoldingActionContributor implements IActionContributor {

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
