/*******************************************************************************************************
 *
 * GamlEditorTickUpdater.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextEditorErrorTickUpdater;

import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The class GamlEditorTickUpdater.
 *
 * @author drogoul
 * @since 8 sept. 2013
 *
 */
public class GamlEditorTickUpdater extends XtextEditorErrorTickUpdater {

	@Override
	protected void updateEditorImage(final XtextEditor editor) {
		Severity severity = getSeverity(editor);
		ImageDescriptor descriptor = null;
		if (severity == null || severity == Severity.INFO) {
			descriptor = GamaIcon.named(IGamaIcons.OVERLAY_OK).descriptor();
		} else if (severity == Severity.ERROR) {
			descriptor = GamaIcon.named(IGamaIcons.OVERLAY_ERROR).descriptor();
		} else if (severity == Severity.WARNING) {
			descriptor = GamaIcon.named(IGamaIcons.OVERLAY_WARNING).descriptor();
		} else {
			super.updateEditorImage(editor);
			return;
		}
		final DecorationOverlayIcon decorationOverlayIcon =
				new DecorationOverlayIcon(editor.getDefaultImage(), descriptor, IDecoration.BOTTOM_LEFT);
		scheduleUpdateEditor(decorationOverlayIcon);
	}

}
