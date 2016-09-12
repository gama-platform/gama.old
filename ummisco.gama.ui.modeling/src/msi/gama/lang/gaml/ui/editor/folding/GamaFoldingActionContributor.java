/*******************************************************************************
 * Copyright (c) 2013 AKSW Xturtle Project, itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
