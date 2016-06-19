/*******************************************************************************
 * Copyright (c) 2013 AKSW Xturtle Project, itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package msi.gama.lang.gaml.ui.folding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.xtext.ui.editor.folding.FoldingActionGroup;

import msi.gama.lang.gaml.ui.folding.GamaFoldingRegionProvider.TypedFoldedPosition;

class GamaFoldingActionGroup extends FoldingActionGroup {

	private class FoldingAction extends Action implements IUpdate {

		FoldingAction() {
			super("Collapse comments", IAction.AS_PUSH_BUTTON);
		}

		@Override
		public void update() {
			setEnabled(GamaFoldingActionGroup.this.isEnabled() && viewwer.isProjectionMode());
		}
	}

	private ProjectionViewer viewwer;
	private FoldingAction collapseStrings;

	GamaFoldingActionGroup(final ITextEditor editor, final ITextViewer viewer) {
		super(editor, viewer);
		if (!(viewer instanceof ProjectionViewer)) {
			return;
		}
		this.viewwer = (ProjectionViewer) viewer;

		collapseStrings = new FoldingAction() { // $NON-NLS-1$
			// private final EClass type = GamlPackage.

			@Override
			public void run() {
				final ProjectionAnnotationModel model = viewwer.getProjectionAnnotationModel();
				final Iterator<?> iterator = model.getAnnotationIterator();
				final List<Annotation> toCollapse = new ArrayList<Annotation>();
				while (iterator.hasNext()) {
					final Object next = iterator.next();
					if (next instanceof ProjectionAnnotation) {
						final ProjectionAnnotation pa = (ProjectionAnnotation) next;
						final Position position = model.getPosition(pa);
						if (position instanceof TypedFoldedPosition)
							if (((TypedFoldedPosition) position).getType().equals("__comment")) {
								pa.markCollapsed();
								toCollapse.add(pa);
							}
					}
				}
				model.modifyAnnotations(null, null, toCollapse.toArray(new Annotation[0]));
			}
		};
		collapseStrings.setActionDefinitionId("org.xtext.example.folding.ui.folding.collapseStrings");
		editor.setAction("FoldingCollapseStrings", collapseStrings); //$NON-NLS-1$

	}

	@Override
	protected void update() {
		super.update();
		if (isEnabled()) {
			collapseStrings.update();
		}
	}
}
