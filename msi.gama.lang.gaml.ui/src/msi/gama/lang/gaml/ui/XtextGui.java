/**
 * Created by drogoul, 21 janv. 2013
 * 
 */
package msi.gama.lang.gaml.ui;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import com.google.inject.Inject;

/**
 * The class XtextGui.
 * 
 * @author drogoul
 * @since 21 janv. 2013
 * 
 */
public class XtextGui extends msi.gama.gui.swt.SwtGui {

	@Override
	public void openEditorAndSelect(final Object eObject) {
		if ( !(eObject instanceof EObject) ) { return; }
		//URI uri = (EObject) eObject.
	}
}
