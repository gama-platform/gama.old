/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.hover;

import msi.gama.lang.gaml.gaml.Statement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

public class GamlHoverProvider extends DefaultEObjectHoverProvider {

	@Override
	protected String getFirstLine(final EObject o) {
		if ( o instanceof Statement ) { return "<b>" + ((Statement) o).getKey() + "</b>"; }
		// if ( o instanceof DefFacet ) { return "<b>" + "facet " + ((DefFacet) o).getName() +
		// "</b>"; }
		return super.getFirstLine(o);
	}

}