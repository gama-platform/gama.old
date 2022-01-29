/*******************************************************************************************************
 *
 * GamaFoldingActionGroup.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor.folding;

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

import msi.gama.lang.gaml.ui.editor.folding.GamaFoldingRegionProvider.TypedFoldedPosition;

/**
 * The Class GamaFoldingActionGroup.
 */
class GamaFoldingActionGroup extends FoldingActionGroup {

	/**
	 * The Class FoldingAction.
	 */
	private class FoldingAction extends Action implements IUpdate {

		/**
		 * Instantiates a new folding action.
		 */
		FoldingAction() {
			super("Collapse comments", IAction.AS_PUSH_BUTTON);
		}

		@Override
		public void update() {
			setEnabled(GamaFoldingActionGroup.this.isEnabled() && pViewer.isProjectionMode());
		}
	}

	/** The p viewer. */
	ProjectionViewer pViewer;
	
	/** The collapse strings. */
	private FoldingAction collapseStrings;

	/**
	 * Instantiates a new gama folding action group.
	 *
	 * @param editor the editor
	 * @param viewer the viewer
	 */
	GamaFoldingActionGroup(final ITextEditor editor, final ITextViewer viewer) {
		super(editor, viewer);
		if (!(viewer instanceof ProjectionViewer)) { return; }
		this.pViewer = (ProjectionViewer) viewer;

		collapseStrings = new FoldingAction() { // $NON-NLS-1$
			// private final EClass type = GamlPackage.

			@Override
			public void run() {
				final ProjectionAnnotationModel model = pViewer.getProjectionAnnotationModel();
				final Iterator<?> iterator = model.getAnnotationIterator();
				final List<Annotation> toCollapse = new ArrayList<>();
				while (iterator.hasNext()) {
					final Object next = iterator.next();
					if (next instanceof ProjectionAnnotation) {
						final ProjectionAnnotation pa = (ProjectionAnnotation) next;
						final Position position = model.getPosition(pa);
						if (position instanceof TypedFoldedPosition) {
							if (((TypedFoldedPosition) position).getType().equals("__comment")) {
								pa.markCollapsed();
								toCollapse.add(pa);
							}
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
