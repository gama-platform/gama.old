/**
 * Created by drogoul, 21 janv. 2013
 * 
 */
package msi.gama.lang.gaml.ui;

import msi.gama.lang.gaml.ui.internal.GamlActivator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import com.google.inject.Injector;

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
		URI uri = EcoreUtil.getURI((EObject) eObject);
		Injector injector = GamlActivator.getInstance().getInjector("msi.gama.lang.gaml.Gaml");
		IURIEditorOpener opener = injector.getInstance(IURIEditorOpener.class);
		opener.open(uri, true);
	}
}
