/*********************************************************************************************
 *
 *
 * 'GamlEditorCallback.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.XtextEditor;

/**
 * The class GamlEditorCallback. Calls the functionalities of
 * ValidatingEditorCallback and marks the resource as "edited" or not, which
 * allows them to process the online doc, etc. 08/16 NOT USED ANYMORE. SHOULD BE
 * REMOVED SOON
 *
 * @author drogoul
 * @since 11 avr. 2014
 *
 */
public class GamlEditorCallback extends IXtextEditorCallback.NullImpl {

	@Override
	public void afterCreatePartControl(final XtextEditor editor) {
	}

	@Override
	public void afterSave(final XtextEditor editor) {
	}
}