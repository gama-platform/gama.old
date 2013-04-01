/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.hover;

import msi.gaml.descriptions.IGamlDescription;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;

public class GamlDocumentationProvider implements IEObjectDocumentationProvider {

	@Override
	public String getDocumentation(final EObject o) {
		IGamlDescription description = DescriptionFactory.getGamlDescription(o);
		if ( description == null ) { return "Not yet documented"; }
		return description.getDocumentation();
	}
}