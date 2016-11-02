/*********************************************************************************************
 *
 * 'GamlEditorTickUpdater.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextEditorErrorTickUpdater;

import ummisco.gama.ui.resources.GamaIcons;
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
		Severity severity = null;
		//
		// try {
		severity = getSeverity(editor);
		//
		// } catch (ResourceException e) {
		// // do nothing, emitted when a marker cannot be found
		// }
		ImageDescriptor descriptor = null;
		if (severity == null || severity == Severity.INFO) {
			descriptor = GamaIcons.create(IGamaIcons.OVERLAY_OK).descriptor();
		} else if (severity == Severity.ERROR) {
			descriptor = GamaIcons.create("overlay.error2").descriptor();
		} else if (severity == Severity.WARNING) {
			descriptor = GamaIcons.create("overlay.warning2").descriptor();
		} else {
			super.updateEditorImage(editor);
			return;
		}
		final DecorationOverlayIcon decorationOverlayIcon = new DecorationOverlayIcon(editor.getDefaultImage(),
				descriptor, IDecoration.BOTTOM_LEFT);
		scheduleUpdateEditor(decorationOverlayIcon);

	}

	@Override
	public void modelChanged(final IAnnotationModel model) {
		// TODO A place where we can hook something ? (for instance the feedback
		// to another editor, etc..
		super.modelChanged(model);
	}

}
